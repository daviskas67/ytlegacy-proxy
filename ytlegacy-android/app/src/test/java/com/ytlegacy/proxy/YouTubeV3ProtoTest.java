package com.ytlegacy.proxy;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
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
        JsonArray videos = new JsonParser().parse(
            "[{\\"videoId\\":\\""+videoId+"\\"," +
            "\\"authorId\\":\\""+channelId+"\\"}]"
        ).getAsJsonArray();

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
