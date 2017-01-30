package service;

import inrtfs.IAccount;

import java.io.FileReader;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dbaware.DbConnectSingle;
import jobs.JobAtom;
import model.ConcreteAcc;
import model.ElementProxy;
import model.MatrixAct;
import network.ProxyGetter;
import network.T4jClient;

/**
 * Выполняет действия: - Берёт BRUTED акки из БД и проверяет, можно ли их использовать по сроку последнего использования
 */
public class AccChecker extends Thread {
	private long user_id;

	private static DbConnectSingle dbConnector = DbConnectSingle.getInstance();
	static Logger logger = LoggerFactory.getLogger(AccChecker.class);

	public AccChecker(long user_id) {
		this.user_id = user_id;
	}
	
	@Override
	public void run() {
		ConcreteAcc acc = new ConcreteAcc(user_id);
		System.out.println(user_id);
		ElementProxy dbproxy = ProxyGetter.getProxy(user_id);
		if (dbproxy == null) {
			logger.error("AccImporter cant get proxy");
		} else {
			String jobtp = "CHECKENABLED";
			JobAtom job = new JobAtom(5L, jobtp, "");
			MatrixAct theact = new MatrixAct(job, acc);
			T4jClient t4wclient = new T4jClient(theact, dbproxy);
			t4wclient.Execute();
		}
	}

	private static void DoCheckAccs() {
		List<IAccount> accounts = dbConnector.getAccounts();
		ExecutorService cachedPool = Executors.newCachedThreadPool();
		for (IAccount acc : accounts) {
			cachedPool.submit(new AccChecker(acc.getAccID()));
		}
		cachedPool.shutdown();
	}

	public static void main(String[] args) {
		DoCheckAccs();
	}
}
