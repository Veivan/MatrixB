package service;

import inrtfs.IAccount;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dbaware.DbConnector;

/**
 * Выполняет действия: - Берёт BRUTED акки из БД и проверяет, можно ли их использовать по сроку последнего использования
 */
public class AccChecker extends Thread {
	ArrayList<Future<?>> futures = new ArrayList<Future<?>>();

	private static DbConnector dbConnector = new DbConnector();
	static Logger logger = LoggerFactory.getLogger(AccChecker.class);

	// Настройка вручную
	private int group_id = 4;

	@Override
	public void run() {
		try {
			DoCheckAccs();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void DoCheckAccs() throws InterruptedException {
		List<IAccount> accounts = dbConnector.getAccounts();
		ExecutorService cachedPool = Executors.newCachedThreadPool();
		for (IAccount acc : accounts) {
			cachedPool.submit(new CheckSingleAcc(acc.getAccID()));
		}
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

	public static void main(String[] args) {
		AccChecker pimp = new AccChecker();
		pimp.run();
	}
}
