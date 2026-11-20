package com.ytlegacy.proxy;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class YouTubeV3ProtoTest {

    @Test
    public void activitiesUsesTheUploadFieldNumbersExpectedByYouTube5021() {
        String videoId = "abcdefghijk";
        String channelId = "UC1234567890123456789012";
        JsonArray videos = new JsonArray();
        JsonObject video = new JsonObject();
        video.addProperty("videoId", videoId);
        video.addProperty("authorId", channelId);
        videos.add(video);

        byte[] encoded = YouTubeV3Proto.activities(videos, 20);
        List<Field> response = fields(encoded);
        assertEquals("one activity item", 1, response.size());

        Field activityField = requireField(response, 1007, 2);
        List<Field> activity = fields(activityField.payload);
        assertEquals(videoId, text(requireField(activity, 101, 2)));
        assertEquals("youtube#activity", text(requireField(activity, 200, 2)));

        List<Field> snippet = fields(requireField(activity, 2, 2).payload);
        assertEquals(channelId, text(requireField(snippet, 2, 2)));
        assertEquals("upload activity type", 1L, requireField(snippet, 3, 0).scalar);

        List<Field> contentDetails = fields(requireField(activity, 3, 2).payload);
        assertNull("field 1 is not contentDetails.upload",
            findField(contentDetails, 1));

        // The YouTube 5.0.21 generated parser maps both ContentDetails.upload
        // and Upload.videoId to protobuf field number 10101.
        Field upload = requireField(contentDetails, 10101, 2);
        List<Field> uploadFields = fields(upload.payload);
        assertEquals(videoId, text(requireField(uploadFields, 10101, 2)));
        assertNull("field 1 is not Upload.videoId", findField(uploadFields, 1));
    }

    @Test
    public void timedOutFeedLookupCanReturnAValidPlaceholderVideo() {
        String videoId = "abcdefghijk";
        VideoData placeholder = VideoData.feedPlaceholder(videoId);

        assertNotNull(placeholder);
        assertEquals(videoId, placeholder.id);
        assertEquals(videoId, placeholder.title);
        assertEquals("YouTube", placeholder.author);
        assertEquals("", placeholder.channelId);

        List<JsonObject> videos = new ArrayList<JsonObject>();
        videos.add(placeholder.toYouTubeV3Json());
        List<Field> response = fields(YouTubeV3Proto.videos(videos));
        assertEquals("placeholder is still present in the batch response", 1, response.size());

        List<Field> item = fields(requireField(response, 1007, 2).payload);
        assertEquals(videoId, text(requireField(item, 101, 2)));
        List<Field> snippet = fields(requireField(item, 2, 2).payload);
        assertEquals(videoId, text(requireField(snippet, 4, 2)));
        assertEquals("YouTube", text(requireField(snippet, 7, 2)));
        assertNotNull("the status message must exist", findField(item, 4));
        assertNotNull("the statistics message must exist", findField(item, 5));
        assertNull(VideoData.feedPlaceholder("invalid"));
    }

    @Test
    public void videosUseLegacyClientResourceFieldsAndVarintStatistics() {
        JsonObject video = new JsonObject();
        video.addProperty("videoId", "abcdefghijk");
        video.addProperty("title", "Video title");
        video.addProperty("description", "Video description");
        video.addProperty("author", "Channel name");
        video.addProperty("published", "2026-11-20T12:00:00.000Z");
        video.addProperty("lengthSeconds", 123);
        video.addProperty("viewCount", 987654L);
        video.addProperty("likeCount", 1234L);

        List<JsonObject> source = new ArrayList<JsonObject>();
        source.add(video);
        List<Field> response = fields(YouTubeV3Proto.videos(source));
        List<Field> item = fields(requireField(response, 1007, 2).payload);

        List<Field> snippet = fields(requireField(item, 2, 2).payload);
        assertEquals("2026-11-20T12:00:00.000Z", text(requireField(snippet, 1, 2)));
        assertEquals("Video title", text(requireField(snippet, 4, 2)));
        assertEquals("Video description", text(requireField(snippet, 5, 2)));
        assertEquals("Channel name", text(requireField(snippet, 7, 2)));

        List<Field> contentDetails = fields(requireField(item, 3, 2).payload);
        assertEquals("PT123S", text(requireField(contentDetails, 1, 2)));

        // Client Video.d() is status (bp); Video.e() is statistics (bo).
        assertEquals("status message is present", 0,
            fields(requireField(item, 4, 2).payload).size());
        List<Field> statistics = fields(requireField(item, 5, 2).payload);
        assertEquals(987654L, requireField(statistics, 1, 0).scalar);
        assertEquals(1234L, requireField(statistics, 2, 0).scalar);
    }

    private static final class Field {
        final int number;
        final int wireType;
        final long scalar;
        final byte[] payload;

        Field(int number, int wireType, long scalar, byte[] payload) {
            this.number = number;
            this.wireType = wireType;
            this.scalar = scalar;
            this.payload = payload;
        }
    }

    private static List<Field> fields(byte[] data) {
        List<Field> result = new ArrayList<Field>();
        int[] offset = new int[] { 0 };
        while (offset[0] < data.length) {
            long tag = readVarint(data, offset);
            if (tag == 0) throw new AssertionError("Invalid protobuf tag 0");
            int number = (int) (tag >>> 3);
            int wireType = (int) (tag & 7);
            if (number == 0) throw new AssertionError("Invalid protobuf field number 0");

            if (wireType == 0) {
                result.add(new Field(number, wireType, readVarint(data, offset), null));
            } else if (wireType == 2) {
                long length = readVarint(data, offset);
                if (length < 0 || length > data.length - offset[0])
                    throw new AssertionError("Invalid protobuf length " + length);
                int end = offset[0] + (int) length;
                byte[] payload = new byte[(int) length];
                System.arraycopy(data, offset[0], payload, 0, payload.length);
                offset[0] = end;
                result.add(new Field(number, wireType, 0L, payload));
            } else if (wireType == 1 || wireType == 5) {
                int skip = wireType == 1 ? 8 : 4;
                if (data.length - offset[0] < skip)
                    throw new AssertionError("Truncated fixed-width protobuf field");
                offset[0] += skip;
                result.add(new Field(number, wireType, 0L, null));
            } else {
                throw new AssertionError("Unsupported protobuf wire type " + wireType);
            }
        }
        return result;
    }

    private static long readVarint(byte[] data, int[] offset) {
        long value = 0L;
        int shift = 0;
        while (shift < 64) {
            if (offset[0] >= data.length)
                throw new AssertionError("Truncated protobuf varint");
            int current = data[offset[0]++] & 0xff;
            value |= (long) (current & 0x7f) << shift;
            if ((current & 0x80) == 0) return value;
            shift += 7;
        }
        throw new AssertionError("Protobuf varint is too long");
    }

    private static Field findField(List<Field> fields, int number) {
        for (Field field : fields) {
            if (field.number == number) return field;
        }
        return null;
    }

    private static Field requireField(List<Field> fields, int number, int wireType) {
        Field field = findField(fields, number);
        assertNotNull("missing protobuf field " + number, field);
        assertEquals("wire type for field " + number, wireType, field.wireType);
        return field;
    }

    private static String text(Field field) {
        return new String(field.payload, StandardCharsets.UTF_8);
    }
}
