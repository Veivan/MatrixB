package main;

import inrtfs.IAccount;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dbaware.DbConnector;
import service.MemoProxy;
import jobs.Homeworks;

public class MatrixEntry extends Thread{

	static Logger logger = LoggerFactory.getLogger(MatrixEntry.class);
	private static final long tick = 5000l; // ms
	private volatile boolean mIsStopped = false;
	
	private Engine engine;
	private MemoProxy memoProxy;
	
	public MatrixEntry(MemoProxy memoProxy) {
		this.memoProxy = memoProxy;
	} 

	@Override
	public void run() {
		mIsStopped = false;
		String mess = "MatrixEntry starting";
		logger.info(mess);
		memoProxy.println(mess);
		
		Homeworks homeworks = new Homeworks();
		Brain brain = new Brain(homeworks);
		engine = new Engine();

		DbConnector dbConnector = DbConnector.getInstance();

		// If read Accounts in cycle then need to refresh their timings 
		List<IAccount> accounts = dbConnector.getAccounts();
		brain.setAccounts(accounts);
		engine.setAccounts(accounts);

		/*
		 * engine.setUserAction(1, "Like", ""); engine.setUserAction(2, "act2", "");
		 * engine.setUserAction(1, "act3", "");
		 */
	
		// Homeworks refreshing
		Homeworks newschedule = dbConnector.getHomeworks(System.currentTimeMillis());
		boolean ischanged = homeworks.IsDifferent(newschedule);
		if (ischanged) {
			homeworks.ReplaceWith(newschedule);
			// Запустить формирование тайминга
			//homeworks.notifyObservers();
			brain.perform(homeworks);
		} 
		logger.info("MatrixEntry finished perform homeworks");

		try {
			while (!mIsStopped) {
				long moment = System.currentTimeMillis();
				//logger.debug("MatrixEntry tick : {}",	Constants.dfm.format(moment));
				/*/ Accounts refreshing
				List<IAccount> accounts = dbConnector.getAccounts();
				brain.setAccounts(accounts);
				engine.setAccounts(accounts); */

				// Homeworks refreshing
				// Достаточно сделать один раз перед циклом, чтобы не грузить систему.
				// Если требуется изменить список задач, то надо перезапускать программу
				/*Homeworks newschedule = dbConnector.getHomeworks(moment);
				boolean ischanged = homeworks.IsDifferent(newschedule);
				if (ischanged) {
					homeworks.ReplaceWith(newschedule);
					// Запустить формирование тайминга
					homeworks.notifyObservers();
				} */

				engine.ReadTimings(moment);
				engine.Execute();

				Thread.sleep(tick);
			}
		} catch (InterruptedException e) {
			logger.error("MatrixEntry cycle exception :", e);
		}
		mess = "MatrixEntry finishing";
		logger.info(mess);
		memoProxy.replacetext(mess);
	}
	
	public void stopThis() {
		mIsStopped = true;
		this.interrupt();
		engine.stop(); 
	}

}
