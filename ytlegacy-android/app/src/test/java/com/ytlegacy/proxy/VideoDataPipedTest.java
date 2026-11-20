package com.ytlegacy.proxy;

import com.google.gson.JsonObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class VideoDataPipedTest {

    @Test
    public void parsesPipedVideoMetadataAndMuxedMp4Stream() {
        String videoId = "abcdefghijk";
        JsonObject response = new JsonObject();
        response.addProperty("title", "Piped video title");
        response.addProperty("description", "Video description");
        response.addProperty("uploader", "Uploader");
        response.addProperty("uploaderUrl", "/channel/UC1234567890123456789012");
        response.addProperty("duration", 125);
        response.addProperty("views", 987654L);
        response.addProperty("likes", 1234L);
        response.addProperty("uploaded", 1718000000000L);
        response.addProperty("livestream", false);
        response.addProperty("proxyUrl", "https://piped.example");

        com.google.gson.JsonArray streams = new com.google.gson.JsonArray();
        JsonObject stream = new JsonObject();
        stream.addProperty("url", "https://piped.example/videoplayback?id=1");
        stream.addProperty("mimeType", "video/mp4");
        stream.addProperty("codec", "avc1.64001F, mp4a.40.2");
        stream.addProperty("format", "MPEG_4");
        stream.addProperty("videoOnly", false);
        stream.addProperty("height", 360);
        stream.addProperty("width", 640);
        streams.add(stream);
        response.add("videoStreams", streams);

        VideoData video = VideoData.fromPiped(response, videoId, "360p");
        assertNotNull(video);
        assertEquals(videoId, video.id);
        assertEquals("Piped video title", video.title);
        assertEquals("Uploader", video.author);
        assertEquals("UC1234567890123456789012", video.channelId);
        assertEquals(125, video.lengthSeconds);
        assertEquals(987654L, video.viewCount);
        assertEquals(1234L, video.likeCount);
        assertEquals("Video description", video.description);
        assertEquals("https://piped.example/videoplayback?id=1", video.streamUrl);
        assertEquals(360, video.height);
        assertTrue(video.published.startsWith("2024-"));
        assertEquals("Piped video title", video.toYouTubeV3Json().get("title").getAsString());
        assertEquals("Uploader", video.toYouTubeV3Json().get("author").getAsString());
    }

    @Test
    public void rejectsAnApiErrorWithoutVideoFields() {
        JsonObject response = new JsonObject();
        response.addProperty("error", "Video unavailable");
        assertNull(VideoData.fromPiped(response, "abcdefghijk", "Auto"));
    }

    @Test
    public void acceptsMetadataWithoutAProgressiveStream() {
        JsonObject response = new JsonObject();
        response.addProperty("title", "Metadata only");
        response.addProperty("uploader", "Uploader");
        response.addProperty("duration", 60);
        VideoData video = VideoData.fromPiped(response, "abcdefghijk", "Auto");
        assertNotNull(video);
        assertEquals("Metadata only", video.title);
        assertEquals("", video.streamUrl);
    }
}
