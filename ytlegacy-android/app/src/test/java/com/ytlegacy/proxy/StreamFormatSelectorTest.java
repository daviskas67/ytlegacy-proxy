package com.ytlegacy.proxy;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class StreamFormatSelectorTest {

    @Test
    public void defaultsToACompatible360pProgressiveMp4() {
        JsonObject response = parse(
            "{\"videoDetails\":{\"videoId\":\"abcdefghijk\",\"title\":\"Test\"}," +
            "\"streamingData\":{\"formats\":[" +
            format(22, 720, "avc1.64001F, mp4a.40.2") + "," +
            format(18, 360, "avc1.42001E, mp4a.40.2") +
            "]}}"
        );

        VideoData video = VideoData.fromInnerTube(response);
        assertNotNull(video);
        assertEquals(18, video.itag);
        assertEquals(360, video.height);
        assertTrue(video.streamUrl.contains("itag18"));
        assertFalse(video.isHd);
    }

    @Test
    public void requested720pSelects720pAndAutoChoosesBestUpTo720p() {
        JsonObject response = parse(
            "{\"videoDetails\":{\"videoId\":\"abcdefghijk\"}," +
            "\"streamingData\":{\"formats\":[" +
            format(18, 360, "avc1.42001E, mp4a.40.2") + "," +
            format(22, 720, "avc1.64001F, mp4a.40.2") + "," +
            format(37, 1080, "avc1.640028, mp4a.40.2") +
            "]}}"
        );

        VideoData requested = VideoData.fromInnerTube(response, "720p");
        assertEquals(22, requested.itag);
        assertEquals(720, requested.height);
        assertTrue(requested.isHd);

        VideoData automatic = VideoData.fromInnerTube(response, "auto");
        assertEquals(22, automatic.itag);
        assertEquals(720, automatic.height);
    }

    @Test
    public void adaptiveVideoOnlyTrackIsNeverUsedAsPlayableMp4() {
        JsonObject response = parse(
            "{\"videoDetails\":{\"videoId\":\"abcdefghijk\"}," +
            "\"streamingData\":{\"adaptiveFormats\":[" +
            "{\"itag\":137,\"height\":1080,\"url\":\"https://r.googlevideo.com/video-only\"," +
            "\"mimeType\":\"video/mp4; codecs=\\\"avc1.640028\\\"\"}," +
            "{\"itag\":140,\"url\":\"https://r.googlevideo.com/audio-only\"," +
            "\"mimeType\":\"audio/mp4; codecs=\\\"mp4a.40.2\\\"\"}" +
            "]}}"
        );

        VideoData video = VideoData.fromInnerTube(response);
        assertTrue(video.streamUrl == null || video.streamUrl.length() == 0);
        assertFalse(video.isHd);
    }

    @Test
    public void adaptiveFormatIsAllowedOnlyWhenItContainsBothTracksInOneMp4() {
        JsonObject response = parse(
            "{\"videoDetails\":{\"videoId\":\"abcdefghijk\"}," +
            "\"streamingData\":{\"adaptiveFormats\":[" +
            "{\"itag\":999,\"height\":480,\"width\":854," +
            "\"url\":\"https://r.googlevideo.com/muxed\"," +
            "\"mimeType\":\"video/mp4; codecs=\\\"avc1.4d401e, mp4a.40.2\\\"\"}" +
            "]}}"
        );

        VideoData video = VideoData.fromInnerTube(response);
        assertEquals(999, video.itag);
        assertEquals(480, video.height);
        assertTrue(video.streamUrl.contains("muxed"));
    }

    @Test
    public void rejectsVideoMp4WithoutAudioCodecEvenForKnownItag() {
        JsonObject response = parse(
            "{\"videoDetails\":{\"videoId\":\"abcdefghijk\"}," +
            "\"streamingData\":{\"formats\":[" +
            "{\"itag\":18,\"height\":360,\"url\":\"https://r.googlevideo.com/silent\"," +
            "\"mimeType\":\"video/mp4; codecs=\\\"avc1.42001e\\\"\"}" +
            "]}}"
        );

        VideoData video = VideoData.fromInnerTube(response);
        assertTrue(video.streamUrl == null || video.streamUrl.length() == 0);
    }

    @Test
    public void InvidiousSelectionUsesRequestedHeightAndItsTypeField() {
        JsonObject response = parse(
            "{\"videoId\":\"abcdefghijk\",\"formatStreams\":[" +
            "{\"itag\":18,\"resolution\":\"360p\",\"url\":\"https://inv.test/360\"," +
            "\"type\":\"video/mp4; codecs=\\\"avc1.42001E, mp4a.40.2\\\"\"}," +
            "{\"itag\":22,\"resolution\":\"720p\",\"url\":\"https://inv.test/720\"," +
            "\"type\":\"video/mp4; codecs=\\\"avc1.64001F, mp4a.40.2\\\"\"}" +
            "]}"
        );

        VideoData video = VideoData.fromInvidious(response, "720p");
        assertEquals(22, video.itag);
        assertEquals(720, video.height);
        assertTrue(video.streamUrl.endsWith("/720"));
    }

    @Test
    public void qualityInputIsBoundedToLegacyMaximum() {
        assertEquals(360, StreamFormatSelector.targetHeight(null));
        assertEquals(720, StreamFormatSelector.targetHeight("auto"));
        assertEquals(720, StreamFormatSelector.targetHeight("1080p"));
        assertEquals(360, StreamFormatSelector.targetHeight("nonsense"));
    }

    @Test
    public void playerXmlAdvertisesTheSelectedFormatInsteadOfHardcodedItag18() {
        VideoData video = new VideoData();
        video.id = "abcdefghijk";
        video.title = "Test";
        video.itag = 22;
        video.width = 1280;
        video.height = 720;
        video.isHd = true;

        String xml = GDataXmlBuilder.buildPlayerEntry(video);
        assertTrue(xml.contains("/video/abcdefghijk.mp4"));
        assertTrue(xml.contains("yt:format=\"22\""));
        assertTrue(xml.contains("itag=\"22\""));
        assertTrue(xml.contains("width=\"1280\""));
        assertTrue(xml.contains("height=\"720\""));
    }

    private static String format(int itag, int height, String codecs) {
        return "{\"itag\":" + itag + ",\"height\":" + height +
            ",\"width\":" + ((height * 16) / 9) +
            ",\"url\":\"https://r.googlevideo.com/itag" + itag + "\"," +
            "\"mimeType\":\"video/mp4; codecs=\\\"" + codecs + "\\\"\"}";
    }

    private static JsonObject parse(String json) {
        return new JsonParser().parse(json).getAsJsonObject();
    }
}
