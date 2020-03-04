package io.awg.concur;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CacheWithReadWriteLock {
	
	private Map<Long, String> cache = new HashMap<>();
	
	
	public void put(Long key, String val) {
		this.cache.put(key, val);
	}
	
	public void get(Long key) {
		this.cache.get(key);
	}

	public void runExperiment() {
		System.out.println("Running CacheWithReadWriteLock experiment with not thread-safe hashmap...");
		
		ExecutorService execService = Executors.newFixedThreadPool(4);
		
		try {
			for(int i = 0; i < 4; i++) {
				execService.submit(new CacheProducer());
			}
		} finally {
			execService.shutdown();
		}
	}
	
	class CacheProducer implements Callable<String> {
		private Random rand = new Random();

		@Override
		public String call() throws Exception {
			while(true) {
				long nextKey = rand.nextInt(1_000);
				cache.put(nextKey, Long.toString(nextKey));
				if(cache.get(nextKey) == null) {
					System.out.println("Key " + nextKey + " has not been placed to cache.");
				}
				if(nextKey == 0) {
					break;
				}
			}
			return "Produced all to the cache.";
		}
		
	}
}
