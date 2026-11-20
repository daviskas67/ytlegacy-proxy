package com.ytlegacy.proxy;

import com.google.gson.JsonObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class SourceManagerTest {

    @Test
    public void fallsBackAfterAValidEmptyResponse() {
        final List<String> calls = new ArrayList<String>();
        SourceManager manager = new SourceManager();

        SourceManager.Result<List<String>> result = manager.firstAvailable("search",
            SourceManager.Source.<List<String>>of("invidious", new SourceManager.Fetcher<List<String>>() {
                @Override public List<String> fetch() {
                    calls.add("invidious");
                    return new ArrayList<String>();
                }
            }, hasItems()),
            SourceManager.Source.<List<String>>of("piped", new SourceManager.Fetcher<List<String>>() {
                @Override public List<String> fetch() {
                    calls.add("piped");
                    return Arrays.asList("video");
                }
            }, hasItems())
        );

        assertEquals(SourceManager.Outcome.SUCCESS, result.outcome);
        assertEquals("piped", result.source);
        assertEquals(Arrays.asList("invidious", "piped"), calls);
        assertEquals(Arrays.asList("video"), result.value);
    }

    @Test
    public void allValidEmptyResponsesRemainEmptyNotError() {
        SourceManager manager = new SourceManager();

        SourceManager.Result<List<String>> result = manager.firstAvailable("trending",
            emptySource("invidious"),
            emptySource("piped")
        );

        assertEquals(SourceManager.Outcome.EMPTY, result.outcome);
        assertNotNull(result.value);
        assertTrue(result.value.isEmpty());
        assertEquals(0, result.failed);
    }

    @Test
    public void nullResponsesAndExceptionsAreProviderFailures() {
        SourceManager manager = new SourceManager();

        SourceManager.Result<String> result = manager.firstAvailable("search",
            SourceManager.Source.<String>of("invidious", new SourceManager.Fetcher<String>() {
                @Override public String fetch() { return null; }
            }, nonEmptyString()),
            SourceManager.Source.<String>of("piped", new SourceManager.Fetcher<String>() {
                @Override public String fetch() throws Exception {
                    throw new IllegalStateException("upstream unavailable");
                }
            }, nonEmptyString())
        );

        assertEquals(SourceManager.Outcome.ERROR, result.outcome);
        assertNull(result.value);
        assertEquals(2, result.attempted);
        assertEquals(2, result.failed);
        assertTrue(result.detail.contains("upstream unavailable"));
    }

    @Test
    public void statusTracksSuccessEmptyAndFailureSeparately() {
        SourceManager manager = new SourceManager();
        manager.firstAvailable("search",
            emptySource("invidious"),
            SourceManager.Source.<List<String>>of("piped", new SourceManager.Fetcher<List<String>>() {
                @Override public List<String> fetch() { return null; }
            }, hasItems())
        );

        JsonObject status = manager.getStatus().getAsJsonObject("providers");
        JsonObject inv = status.getAsJsonObject("invidious");
        JsonObject piped = status.getAsJsonObject("piped");

        assertEquals(1L, inv.get("emptyResponses").getAsLong());
        assertEquals(0L, inv.get("failures").getAsLong());
        assertEquals("empty", inv.get("lastOutcome").getAsString());
        assertEquals(1L, piped.get("failures").getAsLong());
        assertEquals("error", piped.get("lastOutcome").getAsString());
    }

    @Test
    public void successfulProviderStopsTheFallbackChain() {
        final int[] calls = new int[] {0};
        SourceManager manager = new SourceManager();

        SourceManager.Result<String> result = manager.firstAvailable("search",
            SourceManager.Source.<String>of("first", new SourceManager.Fetcher<String>() {
                @Override public String fetch() { calls[0]++; return "usable"; }
            }, nonEmptyString()),
            SourceManager.Source.<String>of("second", new SourceManager.Fetcher<String>() {
                @Override public String fetch() { calls[0]++; return "should not run"; }
            }, nonEmptyString())
        );

        assertTrue(result.isSuccess());
        assertEquals("first", result.source);
        assertEquals(1, calls[0]);
        assertFalse(result.detail.length() > 0);
    }

    private static SourceManager.DataCheck<List<String>> hasItems() {
        return new SourceManager.DataCheck<List<String>>() {
            @Override public boolean hasData(List<String> value) {
                return value != null && !value.isEmpty();
            }
        };
    }

    private static SourceManager.DataCheck<String> nonEmptyString() {
        return new SourceManager.DataCheck<String>() {
            @Override public boolean hasData(String value) {
                return value != null && value.length() > 0;
            }
        };
    }

    private static SourceManager.Source<List<String>> emptySource(final String name) {
        return SourceManager.Source.<List<String>>of(name,
            new SourceManager.Fetcher<List<String>>() {
                @Override public List<String> fetch() { return new ArrayList<String>(); }
            }, hasItems());
    }
}
