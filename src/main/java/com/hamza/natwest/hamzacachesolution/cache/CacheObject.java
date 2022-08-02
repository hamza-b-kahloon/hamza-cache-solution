package com.hamza.natwest.hamzacachesolution.cache;

import org.springframework.data.annotation.Id;

public class CacheObject<T> {

    @Id
    private String id;
    private long lastAccessed;
    private final T value;

    public CacheObject(T value) {
        this.lastAccessed = System.currentTimeMillis();
        this.value = value;
    }

    public void setLastAccessed(long lastAccessed) {
        this.lastAccessed = lastAccessed;
    }

    public T getValue() {
        return value;
    }
}
