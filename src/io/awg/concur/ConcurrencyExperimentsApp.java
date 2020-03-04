package io.awg.concur;


public class ConcurrencyExperimentsApp {

	public static void main(String[] args) {
		System.out.println("Running concur app");
		
		ConsumerProducerWithSemaphore semaphoreExperiment = new ConsumerProducerWithSemaphore();
		//semaphoreExperiment.runExecution();
		
		CacheWithReadWriteLock cacheExperiment = new CacheWithReadWriteLock();
		cacheExperiment.runExperiment();
		
		
		

	}

}
