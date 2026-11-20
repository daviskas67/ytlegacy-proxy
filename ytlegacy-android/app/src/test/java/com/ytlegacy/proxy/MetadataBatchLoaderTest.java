package com.ytlegacy.proxy;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MetadataBatchLoaderTest {

    @Test
    public void loadsInParallelAndPreservesInputOrder() {
        final AtomicInteger active = new AtomicInteger();
        final AtomicInteger maximumActive = new AtomicInteger();

        List<String> result = MetadataBatchLoader.load(
            Arrays.asList("slow", "fast-a", "fast-b", "fast-c"),
            new MetadataBatchLoader.Loader<String>() {
                @Override public String load(String key) throws Exception {
                    int now = active.incrementAndGet();
                    updateMaximum(maximumActive, now);
                    try {
                        Thread.sleep("slow".equals(key) ? 120L : 25L);
                        return key.toUpperCase(java.util.Locale.US);
                    } finally {
                        active.decrementAndGet();
                    }
                }
            },
            2000L
        );

        assertEquals(Arrays.asList("SLOW", "FAST-A", "FAST-B", "FAST-C"), result);
        assertTrue("metadata IDs should execute concurrently", maximumActive.get() > 1);
        assertTrue("parallelism must stay within the shared pool limit", maximumActive.get() <= 6);
    }

    @Test
    public void oneDeadlineBoundsTheWholeBatch() {
        List<String> ids = Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h");
        long started = System.nanoTime();
        List<String> result = MetadataBatchLoader.load(ids,
            new MetadataBatchLoader.Loader<String>() {
                @Override public String load(String key) throws Exception {
                    try {
                        Thread.sleep(1500L);
                        return key;
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return null;
                    }
                }
            }, 150L);
        long elapsedMs = (System.nanoTime() - started) / 1000000L;

        assertEquals(ids.size(), result.size());
        assertTrue("the shared deadline should return before all slow tasks finish",
            elapsedMs < 1200L);
    }

    private static void updateMaximum(AtomicInteger maximum, int value) {
        while (true) {
            int previous = maximum.get();
            if (value <= previous || maximum.compareAndSet(previous, value)) return;
        }
    }
}
