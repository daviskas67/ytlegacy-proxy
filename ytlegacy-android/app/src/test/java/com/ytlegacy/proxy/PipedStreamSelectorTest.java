package com.ytlegacy.proxy;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class PipedStreamSelectorTest {

    @Test
    public void picksMuxedMp4FromPipedPoolResponse() {
        JsonObject response = parse(
            "{\"proxyUrl\":\"https://pipedproxy.example\",\"videoStreams\":[" +
            stream("360p", 360, "false", "avc1.42001e") + "," +
            stream("720p", 720, "false", "avc1.64002a") +
            "]}"
        );

        PipedStreamSelector.Selection selected =
            PipedStreamSelector.select(response, "720p");
        assertEquals(720, selected.height);
        assertTrue(selected.url.endsWith("/720"));
    }

    @Test
    public void chooses360pByDefaultAndCapsAutoAt720p() {
        JsonObject response = parse(
            "{\"proxyUrl\":\"https://pipedproxy.example\",\"videoStreams\":[" +
            stream("360p", 360, "false", "avc1.42001e") + "," +
            stream("720p", 720, "false", "avc1.64002a") +
            "]}"
        );
        assertEquals(360, PipedStreamSelector.select(response, null).height);
        assertEquals(720, PipedStreamSelector.select(response, "auto").height);
    }

    @Test
    public void rejectsVideoOnlyAndNonH264Streams() {
        JsonObject response = parse(
            "{\"proxyUrl\":\"https://pipedproxy.example\",\"videoStreams\":[" +
            stream("720p", 720, "true", "avc1.64002a") + "," +
            stream("360p", 360, "false", "vp09.00.30.08") +
            "]}"
        );
        assertNull(PipedStreamSelector.select(response, "auto"));
    }

    @Test
    public void streamMustBelongToTheAdvertisedProxyOrigin() {
        JsonObject response = parse(
            "{\"proxyUrl\":\"https://pipedproxy.example\",\"videoStreams\":[" +
            "{\"url\":\"https://evil.example/720\",\"format\":\"MPEG_4\"," +
            "\"mimeType\":\"video/mp4\",\"codec\":\"avc1.64002a\"," +
            "\"height\":720,\"videoOnly\":false}" +
            "]}"
        );
        assertNull(PipedStreamSelector.select(response, "720p"));
        assertTrue(!PipedStreamSelector.isTrustedStreamUrl(
            "https://pipedproxy.example", "https://evil.example/720"));
    }

    private static String stream(String quality, int height, String videoOnly, String codec) {
        return "{\"url\":\"https://pipedproxy.example/" + height + "\"," +
            "\"format\":\"MPEG_4\",\"mimeType\":\"video/mp4\"," +
            "\"codec\":\"" + codec + "\",\"quality\":\"" + quality + "\"," +
            "\"height\":" + height + ",\"width\":" + (height * 16 / 9) +
            ",\"videoOnly\":" + videoOnly + "}";
    }

    private static JsonObject parse(String json) {
        return new JsonParser().parse(json).getAsJsonObject();
    }
}
