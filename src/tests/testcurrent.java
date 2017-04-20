package tests;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import service.GenderChecker.Gender;


public class testcurrent {

	private class TestThread extends Thread {
		private int index;

		public TestThread(int j) {
			index = j;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("" + index + " finished");
		}
	}

	public static void main(String[] args) {
		testcurrent x = new testcurrent();
		//ExecutorService cachedPool = Executors.newCachedThreadPool();
		
		
		System.out.println("" + Gender.values()[2]);
	/*	ExecutorService service = Executors.newFixedThreadPool(10);

		for (int j = 0; j < 20; j++) {
			service.submit(x.new TestThread(j));
		}
		service.shutdown(); */
	}

}
