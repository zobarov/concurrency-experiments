package io.awg.concur;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConsumerProducerWithSemaphore {

	private List<Integer> buffer = new ArrayList<>();
	private Lock lock = new ReentrantLock();

	private Condition isEmpty = lock.newCondition();
	private Condition isFull = lock.newCondition();

	public void runExecution() {
		System.out.println("Running ConsumerProducer with Semaphores...");
		
		List<Producer> producers = new ArrayList<>(4);
		for(int i = 0; i < 4; i++) {
			producers.add(new Producer());
		}
		
		List<Consumer> consumers = new ArrayList<>(4);
		for(int i = 0; i < 4; i++) {
			consumers.add(new Consumer());
		}
		System.out.println("Launching producers and consumers");
		
		List<Callable<String>> producersAndConsumers = new ArrayList<>();
		producersAndConsumers.addAll(producers);
		producersAndConsumers.addAll(consumers);
		
		ExecutorService execService = Executors.newFixedThreadPool(8);
		
		try {
			List<Future<String>> futures = execService.invokeAll(producersAndConsumers);
			
			futures.forEach(future -> {
				try {
					System.out.println(future.get());
				} catch (InterruptedException | ExecutionException inte) {
					System.out.println("Exception inner: " + inte);
					//inte.printStackTrace();
				}
			});
		} catch (InterruptedException e) {
			System.out.println("Exception  outer: " + e);
		} finally {
			execService.shutdown();
			System.out.println("Execution Service is shut down.");
		}
		
	}

	class Consumer implements Callable<String> {

		@Override
		public String call() throws InterruptedException, TimeoutException {
			int count = 0;

			while (count++ < 50) {
				try {
					lock.lock();
					
					while (isEmpty(buffer)) {
						if(!isEmpty.await(10, TimeUnit.MILLISECONDS)) {
							throw new TimeoutException("Timeout in consuming");
						}
					}
					buffer.remove(buffer.size() - 1);
					isFull.signalAll();

					// signal;
				} finally {
					lock.unlock();
				}
			}
			return "Consumed " + (count - 1);
		}

	}

	class Producer implements Callable<String> {

		@Override
		public String call() throws Exception {
			int count = 0;

			while (count++ < 50) {
				try {
					lock.lock();
					int err = 10/0;
					while (isFull(buffer)) {
						isFull.await();
					}
					buffer.add(1);
					isEmpty.signalAll();

					// signal;
				} finally {
					lock.unlock();
				}
			}

			return "Produced " + (count - 1);
		}

	}
	
	private boolean isEmpty(List<Integer> buffer) {
		return buffer.size() == 0;
	}
	
	private boolean isFull(List<Integer> buffer) {
		return buffer.size() == 50;
	}

}
