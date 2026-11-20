package com.ytlegacy.proxy;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MetadataCacheTest {

    @Test
    public void expiresEntriesAfterTheConfiguredTtl() throws Exception {
        MetadataCache<String> cache = new MetadataCache<String>(4, 25L);
        cache.put("video", "metadata");
        assertEquals("metadata", cache.get("video"));

        Thread.sleep(50L);
        assertNull(cache.get("video"));
        assertEquals(0, cache.size());
    }

    @Test
    public void evictsLeastRecentlyUsedEntriesWhenCapacityIsReached() {
        MetadataCache<String> cache = new MetadataCache<String>(2, 60000L);
        cache.put("first", "1");
        cache.put("second", "2");
        assertEquals("1", cache.get("first"));
        cache.put("third", "3");

        assertNull(cache.get("second"));
        assertEquals("1", cache.get("first"));
        assertEquals("3", cache.get("third"));
        assertEquals(2, cache.size());
    }
}
