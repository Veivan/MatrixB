package service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Выполняет действия: - Импортит в БД акки из текста
 */
public class AccImporter extends Thread {

	ArrayList<Future<?>> futures = new ArrayList<Future<?>>();

	// Настройка вручную
	private ImpSingleAcc.cDatatype Datatype = ImpSingleAcc.cDatatype.EPN;
	private int group_id = 4;

	@Override
	public void run() {
		try {
			DoImportAccsFromFile();
		} catch (FileNotFoundException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void DoImportAccsFromFile() throws FileNotFoundException, InterruptedException {
		ExecutorService cachedPool = Executors.newCachedThreadPool();
		Scanner in = new Scanner(new FileReader("accs.txt"));
		while (in.hasNext()) {
			String data = in.next();
			// System.out.println (in.next());
			Future<?> runnableFuture = cachedPool.submit(new ImpSingleAcc(data, Datatype, group_id));
			futures.add(runnableFuture);
		}
		in.close();
		cachedPool.shutdown();
		
		int finished = 0;
		int all = futures.size();
		String message = "Imported " + finished + " from " + all;
		while (finished < all) {
			finished = 0;
			for (Future<?> future : futures) {
				if (future.isDone())
					finished++;
			}
			message = "Imported " + finished + " from " + all;
			System.out.println(message);
			Thread.sleep(3000);
		} 
		System.out.println("Finita");
	}

	public static void main(String[] args) throws FileNotFoundException {
		AccImporter pimp = new AccImporter();
		pimp.run();
	}

}
