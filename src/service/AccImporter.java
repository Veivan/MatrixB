package service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Выполняет действия: - Импортит в БД акки из текста
 */
public class AccImporter extends Thread {

	ArrayList<CompletableFuture<Void>> futures = new ArrayList<CompletableFuture<Void>>();

	// Настройка вручную
	private ImpSingleAcc.cDatatype Datatype = ImpSingleAcc.cDatatype.NPHEM;
	private final int group_id = 8;
	
	private MemoProxy memoProxy;

	public AccImporter(MemoProxy memoProxy) {
		this.memoProxy = memoProxy;
	}

	@Override
	public void run() {
		try {
			DoImportAccsFromFile();
		} catch (FileNotFoundException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void DoImportAccsFromFile() throws FileNotFoundException,
			InterruptedException {
		ExecutorService cachedPool = Executors.newCachedThreadPool();
		Scanner in = new Scanner(new FileReader("accs.txt"));
		while (in.hasNext()) {
			String data = in.next();
			// System.out.println (in.next());
			// Future<?> runnableFuture = cachedPool.submit(new
			// ImpSingleAcc(data, Datatype, group_id));
			final CompletableFuture<Void> runnableFuture = CompletableFuture
					.runAsync(new ImpSingleAcc(data, Datatype, group_id),
							cachedPool);
			futures.add(runnableFuture);
			Thread.sleep(1000); // Задержка, чтобы выбиралмсь разные прокси
		}
		in.close();
		cachedPool.shutdown();

		int finished = 0;
		int all = futures.size();
		String message;
		while (finished < all) {
			finished = 0;
			for (CompletableFuture<Void> future : futures) {
				if (future.isDone()) {
					finished++;
					message = "Imported " + finished + " from " + all;
					memoProxy.println(message);
				}
			}
			message = "Imported " + finished + " from " + all;
			System.out.println(message);
			Thread.sleep(3000);
		}
		System.out.println("Finita");
	}

	/*
	 * public static void main(String[] args) throws FileNotFoundException {
	 * AccImporter pimp = new AccImporter(); pimp.run(); }
	 */

}
