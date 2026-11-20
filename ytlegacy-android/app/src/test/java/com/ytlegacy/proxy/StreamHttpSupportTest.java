package com.ytlegacy.proxy;

import com.sun.net.httpserver.HttpServer;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StreamHttpSupportTest {

    @Test
    public void forwardsByteRangeAndReceivesPartialContent() throws Exception {
        final AtomicReference<String> receivedRange = new AtomicReference<String>();
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/video", exchange -> {
            receivedRange.set(exchange.getRequestHeaders().getFirst("Range"));
            exchange.getResponseHeaders().add("Content-Type", "video/mp4");
            exchange.getResponseHeaders().add("Accept-Ranges", "bytes");
            exchange.getResponseHeaders().add("Content-Range", "bytes 5-9/20");
            byte[] body = new byte[] {5, 6, 7, 8, 9};
            exchange.sendResponseHeaders(206, body.length);
            OutputStream output = exchange.getResponseBody();
            output.write(body);
            output.close();
        });
        server.start();

        HttpURLConnection connection = null;
        try {
            URL url = new URL("http://127.0.0.1:" + server.getAddress().getPort() + "/video");
            connection = (HttpURLConnection) url.openConnection();
            StreamHttpSupport.applyRangeHeader(connection, "bytes=5-9");

            assertEquals(206, connection.getResponseCode());
            assertEquals("bytes=5-9", receivedRange.get());
            assertEquals("bytes 5-9/20", connection.getHeaderField("Content-Range"));
            assertEquals("bytes", connection.getHeaderField("Accept-Ranges"));
            assertTrue(StreamHttpSupport.isPartialContent(connection.getResponseCode()));
            assertTrue(StreamHttpSupport.isSuccessfulStreamStatus(connection.getResponseCode()));
            assertTrue(StreamHttpSupport.isPlayableContentType(connection.getContentType()));
            assertEquals(5, connection.getInputStream().readAllBytes().length);
        } finally {
            if (connection != null) connection.disconnect();
            server.stop(0);
        }
    }

    @Test
    public void preservesRangeNotSatisfiableStatusAndContentRange() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/video", exchange -> {
            exchange.getResponseHeaders().add("Content-Range", "bytes */20");
            exchange.getResponseHeaders().add("Accept-Ranges", "bytes");
            exchange.sendResponseHeaders(416, -1);
            exchange.close();
        });
        server.start();

        HttpURLConnection connection = null;
        try {
            URL url = new URL("http://127.0.0.1:" + server.getAddress().getPort() + "/video");
            connection = (HttpURLConnection) url.openConnection();
            StreamHttpSupport.applyRangeHeader(connection, "bytes=999-");

            int status = connection.getResponseCode();
            assertEquals(416, status);
            assertTrue(StreamHttpSupport.isRangeNotSatisfiable(status));
            assertEquals("bytes */20", connection.getHeaderField("Content-Range"));
        } finally {
            if (connection != null) connection.disconnect();
            server.stop(0);
        }
    }

    @Test
    public void rejectsMalformedOrUnboundedRangeHeaders() throws Exception {
        final AtomicReference<String> receivedRange = new AtomicReference<String>();
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/video", exchange -> {
            receivedRange.set(exchange.getRequestHeaders().getFirst("Range"));
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        });
        server.start();

        HttpURLConnection connection = null;
        try {
            URL url = new URL("http://127.0.0.1:" + server.getAddress().getPort() + "/video");
            connection = (HttpURLConnection) url.openConnection();
            StreamHttpSupport.applyRangeHeader(connection, "bytes=0-10\r\nInjected: yes");
            assertEquals(200, connection.getResponseCode());
            assertEquals(null, receivedRange.get());
        } finally {
            if (connection != null) connection.disconnect();
            server.stop(0);
        }
    }

    @Test
    public void acceptsOnlySafeFormatHostsForDirectYt2009Urls() throws Exception {
        assertTrue(StreamHttpSupport.isSafeRedirectTarget(
            new URL("https://r.googlevideo.com/videoplayback")));
        assertTrue(StreamHttpSupport.isSafeRedirectTarget(
            new URL("http://femboy.email:730/video/abcdefghijk")));
        assertFalse(StreamHttpSupport.isSafeRedirectTarget(
            new URL("http://googlevideo.com/videoplayback")));
        assertFalse(StreamHttpSupport.isSafeRedirectTarget(
            new URL("https://example.com/video")));
        assertFalse(StreamHttpSupport.isSafeRedirectTarget(
            new URL("http://femboy.email:80/video")));
    }
}
