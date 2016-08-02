package com.kopitchinski.daniel.autocomplete;

import android.util.LruCache;

//Caching implementation based on an LRUCache
public class LruCaching<K, V> implements Caching<K, V> {
    private final LruCache<K,V> internalCache;
    public LruCaching(int cacheSize) {
        internalCache = new LruCache<>(cacheSize);
    }

    @Override
    public V get(K key) {
        return internalCache.get(key);
    }

    @Override
    public void put(K key, V val) {
        internalCache.put(key, val);
    }
}
