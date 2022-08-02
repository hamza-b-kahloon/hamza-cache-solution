package com.hamza.natwest.hamzacachesolution.cache;

import com.hamza.natwest.hamzacachesolution.repository.CacheObjectRepository;
import org.apache.commons.collections4.map.LRUMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InMemoryCacheImpl<K, T> implements InMemoryCache<K, T>{

    private LRUMap<K, CacheObject<T>> cacheMap;
    private CacheObjectRepository repository;

    @Autowired
    public InMemoryCacheImpl(CacheObjectRepository repository) {
        this.repository = repository;
    }

    public void setCacheLimit(int maxItems) {
        this.cacheMap = new LRUMap<>(maxItems);
    }

    public T get(K key) {
        synchronized (cacheMap) {
            CacheObject<T> cachedItem = cacheMap.get(key);
            if (cachedItem == null) {
                T dbObject = (T) loadFromDataBase(key);
                //Add the result from DB to cache
                cacheMap.put(key, new CacheObject<>(dbObject));
                return dbObject;
            }
            else {
                cachedItem.setLastAccessed(System.currentTimeMillis());
                return cachedItem.getValue();
            }
        }
    }

    public void put(K key, T value) {
        synchronized (cacheMap) {
            cacheMap.put(key, new CacheObject<>(value));
        }
    }

    //Method to get data from database.
    private Object loadFromDataBase(K key) {
        Optional value = repository.findById(key.toString());
        return value.get();
    }

    public int size() {
        synchronized (cacheMap) {
            return cacheMap.size();
        }
    }

}
