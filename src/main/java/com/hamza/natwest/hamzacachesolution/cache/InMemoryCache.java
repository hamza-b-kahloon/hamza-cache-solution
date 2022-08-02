package com.hamza.natwest.hamzacachesolution.cache;

public interface InMemoryCache<K,T> {

    T get(K key);

    void put(K key, T value);

    void setCacheLimit(int maxItems);

    int size();

}
