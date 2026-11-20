package com.ytlegacy.proxy;

import com.google.gson.JsonObject;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Ordered provider fallback for legacy feed operations.
 *
 * A null value or exception means the provider failed. A non-null value with
 * no usable entries means the provider answered successfully but had no data.
 * This distinction prevents an upstream outage from being reported as an
 * ordinary empty feed.
 */
public final class SourceManager {

    public enum Outcome {
        SUCCESS,
        EMPTY,
        ERROR
    }

    public interface Fetcher<T> {
        T fetch() throws Exception;
    }

    public interface DataCheck<T> {
        boolean hasData(T value) throws Exception;
    }

    public static final class Source<T> {
        public final String name;
        private final Fetcher<T> fetcher;
        private final DataCheck<T> dataCheck;

        private Source(String name, Fetcher<T> fetcher, DataCheck<T> dataCheck) {
            if (name == null || name.trim().length() == 0)
                throw new IllegalArgumentException("Source name is required");
            if (fetcher == null) throw new IllegalArgumentException("Fetcher is required");
            if (dataCheck == null) throw new IllegalArgumentException("Data check is required");
            this.name = name;
            this.fetcher = fetcher;
            this.dataCheck = dataCheck;
        }

        public static <T> Source<T> of(String name, Fetcher<T> fetcher, DataCheck<T> dataCheck) {
            return new Source<T>(name, fetcher, dataCheck);
        }
    }

    public static final class Result<T> {
        public final Outcome outcome;
        public final T value;
        public final String source;
        public final int attempted;
        public final int failed;
        public final String detail;

        private Result(Outcome outcome, T value, String source,
                       int attempted, int failed, String detail) {
            this.outcome = outcome;
            this.value = value;
            this.source = source;
            this.attempted = attempted;
            this.failed = failed;
            this.detail = detail;
        }

        public boolean isSuccess() {
            return outcome == Outcome.SUCCESS;
        }
    }

    private static final class ProviderState {
        long attempts;
        long successes;
        long emptyResponses;
        long failures;
        long lastLatencyMs = -1L;
        long lastAttemptAt;
        long lastSuccessAt;
        String lastOutcome = "never";
        String lastOperation = "";
        String lastError = "";
    }

    private final Map<String, ProviderState> providers =
        new LinkedHashMap<String, ProviderState>();

    @SafeVarargs
    public final <T> Result<T> firstAvailable(String operation, Source<T>... orderedSources) {
        T lastEmptyValue = null;
        String lastEmptySource = "";
        String lastError = "";
        int attempted = 0;
        int failed = 0;
        boolean receivedValidResponse = false;

        if (orderedSources == null || orderedSources.length == 0) {
            return new Result<T>(Outcome.ERROR, null, "", 0, 0, "No sources configured");
        }

        for (Source<T> source : orderedSources) {
            if (source == null) continue;
            attempted++;
            long startedAt = System.currentTimeMillis();
            T value = null;
            Outcome outcome;
            String error = "";

            try {
                value = source.fetcher.fetch();
                if (value == null) {
                    outcome = Outcome.ERROR;
                    error = "Provider returned null";
                } else if (source.dataCheck.hasData(value)) {
                    outcome = Outcome.SUCCESS;
                    receivedValidResponse = true;
                } else {
                    outcome = Outcome.EMPTY;
                    receivedValidResponse = true;
                }
            } catch (Exception e) {
                outcome = Outcome.ERROR;
                error = describe(e);
            }

            long latency = Math.max(0L, System.currentTimeMillis() - startedAt);
            record(source.name, operation, outcome, latency, error);

            if (outcome == Outcome.SUCCESS) {
                return new Result<T>(Outcome.SUCCESS, value, source.name,
                    attempted, failed, "");
            }
            if (outcome == Outcome.EMPTY) {
                lastEmptyValue = value;
                lastEmptySource = source.name;
            } else {
                failed++;
                lastError = source.name + ": " + error;
            }
        }

        if (receivedValidResponse) {
            return new Result<T>(Outcome.EMPTY, lastEmptyValue, lastEmptySource,
                attempted, failed, lastError);
        }
        return new Result<T>(Outcome.ERROR, null, "", attempted, failed,
            lastError.length() == 0 ? "All providers failed" : lastError);
    }

    private synchronized void record(String name, String operation, Outcome outcome,
                                     long latency, String error) {
        ProviderState state = providers.get(name);
        if (state == null) {
            state = new ProviderState();
            providers.put(name, state);
        }
        state.attempts++;
        state.lastAttemptAt = System.currentTimeMillis();
        state.lastLatencyMs = latency;
        state.lastOperation = operation == null ? "" : operation;
        state.lastOutcome = outcome.name().toLowerCase(java.util.Locale.US);
        if (outcome == Outcome.SUCCESS) {
            state.successes++;
            state.lastSuccessAt = state.lastAttemptAt;
            state.lastError = "";
        } else if (outcome == Outcome.EMPTY) {
            state.emptyResponses++;
            state.lastSuccessAt = state.lastAttemptAt;
            state.lastError = "";
        } else {
            state.failures++;
            state.lastError = error == null ? "Provider failed" : error;
        }
    }

    public synchronized JsonObject getStatus() {
        JsonObject root = new JsonObject();
        root.addProperty("policy", "ordered-fallback");
        JsonObject items = new JsonObject();
        for (Map.Entry<String, ProviderState> entry : providers.entrySet()) {
            ProviderState state = entry.getValue();
            JsonObject item = new JsonObject();
            item.addProperty("attempts", state.attempts);
            item.addProperty("successes", state.successes);
            item.addProperty("emptyResponses", state.emptyResponses);
            item.addProperty("failures", state.failures);
            item.addProperty("lastOutcome", state.lastOutcome);
            item.addProperty("lastOperation", state.lastOperation);
            item.addProperty("lastLatencyMs", state.lastLatencyMs);
            item.addProperty("lastAttemptAt", state.lastAttemptAt);
            item.addProperty("lastSuccessAt", state.lastSuccessAt);
            item.addProperty("lastError", state.lastError);
            items.add(entry.getKey(), item);
        }
        root.add("providers", items);
        return root;
    }

    private static String describe(Exception error) {
        if (error == null) return "Unknown error";
        String message = error.getMessage();
        if (message == null || message.trim().length() == 0)
            message = error.getClass().getSimpleName();
        if (message.length() > 160) message = message.substring(0, 160);
        return error.getClass().getSimpleName() + ": " + message;
    }
}
