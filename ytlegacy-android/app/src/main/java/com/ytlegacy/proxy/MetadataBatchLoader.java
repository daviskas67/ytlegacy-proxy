package com.ytlegacy.proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Bounded, shared executor for metadata fan-out requests.
 *
 * Results are returned in input order. A single deadline applies to the whole
 * batch, so a slow upstream cannot make the legacy client wait once per ID.
 */
final class MetadataBatchLoader {
    interface Loader<T> {
        T load(String key) throws Exception;
    }

    private static final int THREADS = 6;
    private static final int QUEUE_CAPACITY = 256;
    private static final AtomicInteger THREAD_NUMBER = new AtomicInteger();

    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
        THREADS,
        THREADS,
        30L,
        TimeUnit.SECONDS,
        new ArrayBlockingQueue<Runnable>(QUEUE_CAPACITY),
        new ThreadFactory() {
            @Override public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable,
                    "ytlegacy-metadata-" + THREAD_NUMBER.incrementAndGet());
                thread.setDaemon(true);
                return thread;
            }
        },
        new ThreadPoolExecutor.AbortPolicy()
    );

    private MetadataBatchLoader() {}

    static <T> List<T> load(final List<String> keys, final Loader<T> loader,
                            long deadlineMs) {
        List<T> results = new ArrayList<T>();
        if (keys == null || keys.isEmpty() || loader == null) return results;

        for (int i = 0; i < keys.size(); i++) results.add(null);

        final long timeoutNanos = TimeUnit.MILLISECONDS.toNanos(
            Math.max(1L, deadlineMs));
        final long deadline = System.nanoTime() + timeoutNanos;
        CompletionService<IndexedValue<T>> completion =
            new ExecutorCompletionService<IndexedValue<T>>(EXECUTOR);
        List<Future<IndexedValue<T>>> futures =
            new ArrayList<Future<IndexedValue<T>>>();
        int submitted = 0;

        for (int i = 0; i < keys.size(); i++) {
            final int index = i;
            final String key = keys.get(i);
            try {
                Future<IndexedValue<T>> future = completion.submit(
                    new Callable<IndexedValue<T>>() {
                        @Override public IndexedValue<T> call() throws Exception {
                            return new IndexedValue<T>(index, loader.load(key));
                        }
                    });
                futures.add(future);
                submitted++;
            } catch (RejectedExecutionException ignored) {
                // The shared queue is full. Omit this item instead of running
                // network I/O on NanoHTTPD's request thread.
            }
        }

        int completed = 0;
        try {
            while (completed < submitted) {
                long remaining = deadline - System.nanoTime();
                if (remaining <= 0L) break;

                Future<IndexedValue<T>> future =
                    completion.poll(remaining, TimeUnit.NANOSECONDS);
                if (future == null) break;

                completed++;
                try {
                    IndexedValue<T> value = future.get();
                    if (value != null && value.index >= 0
                            && value.index < results.size()) {
                        results.set(value.index, value.value);
                    }
                } catch (Exception ignored) {
                    // Keep the slot null; one provider failure must not fail
                    // the whole legacy protobuf response.
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            for (Future<IndexedValue<T>> future : futures) {
                if (future != null && !future.isDone()) future.cancel(true);
            }
            // Remove cancelled queued tasks so repeated timeouts do not fill
            // the bounded queue with work that can no longer be used.
            EXECUTOR.purge();
        }

        return results;
    }

    private static final class IndexedValue<T> {
        final int index;
        final T value;

        IndexedValue(int index, T value) {
            this.index = index;
            this.value = value;
        }
    }
}
