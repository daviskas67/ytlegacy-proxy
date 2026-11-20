package com.ytlegacy.proxy;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class Yt2009FormatSelectorTest {

    @Test
    public void defaultSelectionUses360pProgressiveMp4() {
        Map<Integer, String> formats = formats();
        Yt2009FormatSelector.Selection selected =
            Yt2009FormatSelector.select(formats, null);
        assertEquals(18, selected.itag);
        assertEquals(360, selected.height);
    }

    @Test
    public void requested480pSelectsTheProgressive480pMp4() {
        Yt2009FormatSelector.Selection selected =
            Yt2009FormatSelector.select(formats(), "480p");
        assertEquals(59, selected.itag);
        assertEquals(480, selected.height);
    }

    @Test
    public void requested720pAndAutoNeverSelectAbove720p() {
        Yt2009FormatSelector.Selection selected720 =
            Yt2009FormatSelector.select(formats(), "720p");
        Yt2009FormatSelector.Selection selectedAuto =
            Yt2009FormatSelector.select(formats(), "auto");
        assertEquals(22, selected720.itag);
        assertEquals(22, selectedAuto.itag);
        assertEquals(720, selectedAuto.height);
    }

    @Test
    public void excludesFlvWebmAdaptiveAndAudioOnlyItags() {
        Map<Integer, String> unsupported = new HashMap<Integer, String>();
        unsupported.put(5, "https://video.example/flv");
        unsupported.put(43, "https://video.example/webm");
        unsupported.put(137, "https://video.example/video-only");
        unsupported.put(140, "https://video.example/audio-only");
        assertNull(Yt2009FormatSelector.select(unsupported, "auto"));
    }

    @Test
    public void ignoresBlankAndMissingUrls() {
        Map<Integer, String> formats = new HashMap<Integer, String>();
        formats.put(18, " ");
        formats.put(22, null);
        assertNull(Yt2009FormatSelector.select(formats, "720p"));
    }

    private static Map<Integer, String> formats() {
        Map<Integer, String> formats = new HashMap<Integer, String>();
        formats.put(18, "https://r.googlevideo.com/itag18");
        formats.put(59, "https://r.googlevideo.com/itag59");
        formats.put(22, "https://r.googlevideo.com/itag22");
        formats.put(37, "https://r.googlevideo.com/itag37");
        formats.put(137, "https://r.googlevideo.com/video-only");
        formats.put(140, "https://r.googlevideo.com/audio-only");
        return formats;
    }
}
