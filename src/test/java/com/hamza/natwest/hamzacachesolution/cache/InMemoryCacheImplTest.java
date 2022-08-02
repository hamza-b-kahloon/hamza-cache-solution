package com.hamza.natwest.hamzacachesolution.cache;

import com.hamza.natwest.hamzacachesolution.repository.CacheObjectRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InMemoryCacheImplTest {

    private InMemoryCacheImpl<String, Integer> inMemoryCache;
    @Mock
    private CacheObjectRepository cacheObjectRepository;

    @Before
    public void setUp() throws Exception {
        inMemoryCache = new InMemoryCacheImpl<String, Integer>(cacheObjectRepository);

        // cacheLimit = 6
        inMemoryCache.setCacheLimit(6);
        inMemoryCache.put("eBay", 100);
        inMemoryCache.put("Paypal", 200);
        inMemoryCache.put("Google", 300);
        inMemoryCache.put("Microsoft", 400);
        inMemoryCache.put("Apple", 500);
        inMemoryCache.put("Facebook", 600);
    }

    @Test
    public void verifyInMemoryCacheStoresItemsSuccessFullyTest() {
        //Should have 6  items
        assertEquals(6, inMemoryCache.size());

        //get price of key eBay
        assertEquals(100, (int) inMemoryCache.get("eBay"));

    }

    @Test
    public void whenCacheExceedsMaxItems_thenRemoveLeastRecentlyRequestedItem() throws InterruptedException {
        //cache reached max capacity of 6
        assertEquals(6, inMemoryCache.size());

        //request 5 out 6 items, leaving one (Facebook) to be removed later automatically
        inMemoryCache.get("eBay");
        inMemoryCache.get("Paypal");
        inMemoryCache.get("Google");
        inMemoryCache.get("Microsoft");
        inMemoryCache.get("Apple");

        //add another item that exceeds max limit
        inMemoryCache.put("Amazon", 1000);

        // size is still 6
        assertEquals(6, inMemoryCache.size());

        //all recently requested Items are still there
        assertEquals(100, (int) inMemoryCache.get("eBay"));
        assertEquals(200, (int) inMemoryCache.get("Paypal"));
        assertEquals(300, (int) inMemoryCache.get("Google"));
        assertEquals(400, (int) inMemoryCache.get("Microsoft"));
        assertEquals(500, (int) inMemoryCache.get("Apple"));

        //new added item is also present, and Facebook is removed
        assertEquals(1000, (int) inMemoryCache.get("Amazon"));
    }

    @Test
    public void whenRequestedItemIsNotPresentInCache_thenLoadFromDB() {
        //setting up new cache
        inMemoryCache = new InMemoryCacheImpl<String, Integer>(cacheObjectRepository);

        inMemoryCache.setCacheLimit(6);
        inMemoryCache.put("eBay", 100);
        inMemoryCache.put("Paypal", 200);

        //cache size is 2
        assertEquals(2, inMemoryCache.size());

        Optional<Integer> item = Optional.of(300);
        when(cacheObjectRepository.findById("Uber")).thenReturn(item);

        //Try a new key that is not in cache and verify price
        Integer uberPrice = inMemoryCache.get("Uber");
        assertEquals(300, (int) uberPrice);

        //new cache size is 3 as item is loaded from DB
        assertEquals(3, inMemoryCache.size());
    }

}