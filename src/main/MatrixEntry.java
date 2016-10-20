package main;

import inrtfs.IAccount;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dbaware.DbConnectSingle;
import service.Constants;
import jobs.Homeworks;

public class MatrixEntry {

	static Logger logger = LoggerFactory.getLogger(MatrixEntry.class);
	private static final long tick = 5000l; // ms

	public static void main(String[] args) {

		logger.info("MatrixEntry starting");

		// Формирование списков заданий
		Homeworks howmworks = new Homeworks();

		Brain brain = new Brain(howmworks);
		Engine engine = new Engine();

		@SuppressWarnings("unused")
		ActionsObserver currentDisplay = new ActionsObserver(engine);

		/*
		 * engine.setUserAction(1, "Like"); engine.setUserAction(2, "act2");
		 * engine.setUserAction(1, "act3");
		 */

		DbConnectSingle dbConnector = DbConnectSingle.getInstance();

		try {
			while (true) {
				long moment = System.currentTimeMillis();
				logger.debug("MatrixEntry tick : {}",
						Constants.dfm.format(moment));
				// Accounts refreshing
				List<IAccount> accounts = dbConnector.getAccounts();
				brain.setAccounts(accounts);
				engine.setAccounts(accounts);

				// Homeworks refreshing
				Homeworks newschedule = dbConnector.getHomeworks();
				boolean ischanged = howmworks.CompareWith(newschedule);
				if (ischanged) {
					howmworks.ReplaceWith(newschedule);
					// Запустить формирование тайминга
					howmworks.notifyObservers();
				}

				engine.ReadTimings(moment);

				Thread.sleep(tick);
			}
		} catch (InterruptedException e) {
			logger.error("MatrixEntry cycle exception :", e);
			logger.debug("MatrixEntry cycle exception :", e);
		}
		logger.info("MatrixEntry finishing");
	}

}
