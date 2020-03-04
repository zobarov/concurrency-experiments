package io.awg.concur;


public class ConcurrencyExperimentsApp {

	public static void main(String[] args) {
		System.out.println("Running concur app");
		
		ConsumerProducerWithSemaphore service = new ConsumerProducerWithSemaphore();
		
		service.runExecution();

	}

}
