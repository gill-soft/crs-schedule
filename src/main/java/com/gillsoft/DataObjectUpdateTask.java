package com.gillsoft;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.gillsoft.cache.CacheHandler;
import com.gillsoft.cache.IOCacheException;
import com.gillsoft.cache.MemoryCacheHandler;

public class DataObjectUpdateTask implements Runnable, Serializable {
	
	private static final long serialVersionUID = -6962492144353223853L;
	
	public CacheHandler cache;
	private String key;
	private CacheObjectGetter getter;
	
	public DataObjectUpdateTask() {
		
	}

	public DataObjectUpdateTask(CacheHandler cache, String key, CacheObjectGetter getter) {
		this.cache = cache;
		this.key = key;
		this.getter = getter;
	}

	@Override
	public void run() {
		Map<String, Object> params = new HashMap<>();
		params.put(MemoryCacheHandler.OBJECT_NAME, key);
		params.put(MemoryCacheHandler.IGNORE_AGE, true);
		params.put(MemoryCacheHandler.UPDATE_DELAY, 60000l);
		try {
			Object dataObject = getter.forCache();
			if (dataObject == null) {
				dataObject = cache.read(params);
			}
			params.put(MemoryCacheHandler.UPDATE_TASK, this);
			cache.write(dataObject, params);
		} catch (IOCacheException e) {
		}
	}

}
