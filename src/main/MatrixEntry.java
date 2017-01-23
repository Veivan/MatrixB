package main;

import inrtfs.IAccount;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dbaware.DbConnectSingle;
import service.Constants;
import jobs.Homeworks;

public class MatrixEntry extends Thread{

	static Logger logger = LoggerFactory.getLogger(MatrixEntry.class);
	private static final long tick = 5000l; // ms
	private volatile boolean mIsStopped = false;
	
	private Engine engine;

	@Override
	public void run() {
		mIsStopped = false;
		logger.info("MatrixEntry starting");

		Homeworks homeworks = new Homeworks();
		Brain brain = new Brain(homeworks);
		engine = new Engine();

		//@SuppressWarnings("unused")
		//ActionsObserver currentDisplay = new ActionsObserver(engine);
		DbConnectSingle dbConnector = DbConnectSingle.getInstance();

		// If read Accounts in cycle then need to refresh their timings 
		List<IAccount> accounts = dbConnector.getAccounts();
		brain.setAccounts(accounts);
		engine.setAccounts(accounts);

		/*
		 * engine.setUserAction(1, "Like", ""); engine.setUserAction(2, "act2", "");
		 * engine.setUserAction(1, "act3", "");
		 */
	
		try {
			while (!mIsStopped) {
				long moment = System.currentTimeMillis();
				logger.debug("MatrixEntry tick : {}",
						Constants.dfm.format(moment));
				/*/ Accounts refreshing
				List<IAccount> accounts = dbConnector.getAccounts();
				brain.setAccounts(accounts);
				engine.setAccounts(accounts); */

				// Homeworks refreshing
				Homeworks newschedule = dbConnector.getHomeworks(moment);
				boolean ischanged = homeworks.IsDifferent(newschedule);
				if (ischanged) {
					homeworks.ReplaceWith(newschedule);
					// Запустить формирование тайминга
					homeworks.notifyObservers();
				}

				engine.ReadTimings(moment);
				engine.Execute();

				Thread.sleep(tick);
			}
		} catch (InterruptedException e) {
			logger.error("MatrixEntry cycle exception :", e);
		}
		logger.info("MatrixEntry finishing");
	}
	
	public void stopThis() {
		mIsStopped = true;
		this.interrupt();
		engine.stop(); 
	}

	/*public static void main(String[] args) {
		MatrixEntry matrix = new MatrixEntry();
		matrix.run();
	}*/

}
