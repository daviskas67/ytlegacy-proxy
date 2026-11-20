package com.ytlegacy.proxy;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/** Small synchronized TTL/LRU cache suitable for the API-8 Android client. */
final class MetadataCache<V> {
    private static final class Entry<V> {
        final V value;
        final long createdAt;

        Entry(V value, long createdAt) {
            this.value = value;
            this.createdAt = createdAt;
        }
    }

    private final int maxEntries;
    private final long ttlMs;
    private final LinkedHashMap<String, Entry<V>> entries =
        new LinkedHashMap<String, Entry<V>>(16, 0.75f, true);

    MetadataCache(int maxEntries, long ttlMs) {
        if (maxEntries < 1) throw new IllegalArgumentException("maxEntries must be positive");
        if (ttlMs < 1L) throw new IllegalArgumentException("ttlMs must be positive");
        this.maxEntries = maxEntries;
        this.ttlMs = ttlMs;
    }

    synchronized V get(String key) {
        if (key == null) return null;
        Entry<V> entry = entries.get(key);
        if (entry == null) return null;
        if (System.currentTimeMillis() - entry.createdAt >= ttlMs) {
            entries.remove(key);
            return null;
        }
        return entry.value;
    }

    synchronized void put(String key, V value) {
        if (key == null || value == null) return;
        entries.put(key, new Entry<V>(value, System.currentTimeMillis()));
        trimExpired();
        while (entries.size() > maxEntries) {
            Iterator<Map.Entry<String, Entry<V>>> iterator =
                entries.entrySet().iterator();
            if (!iterator.hasNext()) break;
            iterator.next();
            iterator.remove();
        }
    }

    synchronized void clear() {
        entries.clear();
    }

    synchronized int size() {
        trimExpired();
        return entries.size();
    }

    private void trimExpired() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<String, Entry<V>>> iterator =
            entries.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<V> entry = iterator.next().getValue();
            if (now - entry.createdAt >= ttlMs) iterator.remove();
        }
    }
}
