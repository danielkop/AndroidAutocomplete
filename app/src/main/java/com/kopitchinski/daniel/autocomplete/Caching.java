package com.kopitchinski.daniel.autocomplete;

public interface Caching<K, V> {
    V get(K key);
    void put(K key, V val);
}
