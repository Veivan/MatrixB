package tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import twitter4j.Status;

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

	@Test
	public void testshuffle() {
		String pname = "Иван Иванов";
		String ppage = "10 лет";
		String fr1 = "Требуется лечение.";
		String twcontent = String.format("%s %s.%n", pname, ppage);
		List<String> helps = Arrays.asList("Вы можете помочь.", "Помогите!", "Нужна помощь!", "Help!");
		String randomHelp = helps.get(new Random().nextInt(helps.size())); 
		String tags = "#ДобротаПодаритЖизнь #СпасиРебёнка";

		int id = 2367;
		String link = "http://helpchildren.online/?id=" + id;
		
		List<String> twits = Arrays.asList("random1", "random2", "random3", "random4");
		String randomTwit = twits.get(new Random().nextInt(twits.size())); 
		
		List<String> details = new ArrayList<String>(); 
		details.add(twcontent);
		details.add(fr1);
		details.add(randomHelp);
		details.add(link);
		details.add(tags);
		details.add(String.format("%n%s%n", randomTwit));
		
		Collections.shuffle(details);
		//for (String stat : details) System.out.println(stat);
		String listString = String.join(" ", details);	
		System.out.println(listString);

	}

/*	public static void main2(String[] args) {
		testcurrent x = new testcurrent();
		
		//ExecutorService cachedPool = Executors.newCachedThreadPool();
		ExecutorService service = Executors.newFixedThreadPool(10);
		for (int j = 0; j < 20; j++) {
			service.submit(x.new TestThread(j));
		}
		service.shutdown(); 
	}
	*/

}
