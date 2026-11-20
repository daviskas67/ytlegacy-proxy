package com.ytlegacy.proxy;

import org.junit.Test;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class StreamHttpSupportTest {

    @Test
    public void forwardsValidByteRangeHeader() throws Exception {
        MockConnection connection = new MockConnection();
        StreamHttpSupport.applyRangeHeader(connection, "bytes=5-9");
        assertEquals("bytes=5-9", connection.requestHeaders.get("Range"));
    }

    @Test
    public void rejectsMalformedOrUnboundedRangeHeaders() throws Exception {
        MockConnection connection = new MockConnection();
        StreamHttpSupport.applyRangeHeader(connection, "bytes=0-10\r\nInjected: yes");
        assertNull(connection.requestHeaders.get("Range"));
        StreamHttpSupport.applyRangeHeader(connection, "items=0-10");
        assertNull(connection.requestHeaders.get("Range"));
        StreamHttpSupport.applyRangeHeader(connection, "bytes=" + repeat("1", 129));
        assertNull(connection.requestHeaders.get("Range"));
    }

    @Test
    public void classifiesOkPartialAndUnsatisfiableRangeStatuses() {
        assertTrue(StreamHttpSupport.isSuccessfulStreamStatus(200));
        assertTrue(StreamHttpSupport.isSuccessfulStreamStatus(206));
        assertFalse(StreamHttpSupport.isSuccessfulStreamStatus(416));
        assertTrue(StreamHttpSupport.isPartialContent(206));
        assertFalse(StreamHttpSupport.isPartialContent(200));
        assertTrue(StreamHttpSupport.isRangeNotSatisfiable(416));
        assertFalse(StreamHttpSupport.isRangeNotSatisfiable(404));
    }

    @Test
    public void recognizesPlayableContentTypes() {
        assertTrue(StreamHttpSupport.isPlayableContentType("video/mp4"));
        assertTrue(StreamHttpSupport.isPlayableContentType("application/octet-stream"));
        assertFalse(StreamHttpSupport.isPlayableContentType("text/html"));
        assertFalse(StreamHttpSupport.isPlayableContentType(null));
    }

    @Test
    public void preservesContentRangeHeaderWithFallback() throws Exception {
        MockConnection connection = new MockConnection();
        connection.responseHeaders.put("Content-Range", "bytes */20");
        assertEquals("bytes */20",
            StreamHttpSupport.responseHeader(connection, "Content-Range", "missing"));
        assertEquals("bytes", StreamHttpSupport.responseHeader(connection, "Accept-Ranges", "bytes"));
    }

    @Test
    public void genericRedirectPolicyAllowsGoogleVideoButNotPinnedYt2009Host() throws Exception {
        assertTrue(StreamHttpSupport.isSafeRedirectTarget(
            new URL("https://r.googlevideo.com/videoplayback")));
        assertFalse(StreamHttpSupport.isSafeRedirectTarget(
            new URL("http://googlevideo.com/videoplayback")));
        assertFalse(StreamHttpSupport.isSafeRedirectTarget(
            new URL("https://femboy.email:730/video/abcdefghijk")));
        assertFalse(StreamHttpSupport.isSafeRedirectTarget(
            new URL("https://example.com/video")));
    }

    private static String repeat(String value, int count) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < count; i++) result.append(value);
        return result.toString();
    }

    private static final class MockConnection extends HttpURLConnection {
        final Map<String, String> requestHeaders = new HashMap<String, String>();
        final Map<String, String> responseHeaders = new HashMap<String, String>();

        MockConnection() throws Exception {
            super(new URL("http://localhost/test"));
        }

        @Override public void disconnect() {}
        @Override public boolean usingProxy() { return false; }
        @Override public void connect() {}

        @Override public void setRequestProperty(String key, String value) {
            requestHeaders.put(key, value);
        }

        @Override public String getHeaderField(String name) {
            return responseHeaders.get(name);
        }
    }
}
