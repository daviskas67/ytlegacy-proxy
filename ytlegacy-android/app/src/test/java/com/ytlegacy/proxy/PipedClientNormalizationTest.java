package com.ytlegacy.proxy;

import com.google.gson.JsonArray;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PipedClientNormalizationTest {

    @Test
    public void failedUpstreamResponseIsNotReportedAsEmptyFeed() {
        PipedClient client = new PipedClient();
        assertNull(client.normalizeVideos(null));
    }

    @Test
    public void validEmptyArrayRemainsAValidEmptyResponse() {
        PipedClient client = new PipedClient();
        JsonArray result = client.normalizeVideos(new JsonArray());
        assertEquals(0, result.size());
    }
}
