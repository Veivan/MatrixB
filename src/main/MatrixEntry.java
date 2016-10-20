package main;

import inrtfs.IAccount;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dbaware.DbConnectSingle;
import service.Constants;
import jobs.Homeworks;
import jobs.JobAtom;
import jobs.JobList;

public class MatrixEntry {

	static Logger logger = LoggerFactory.getLogger(MatrixEntry.class);
	private static final long tick = 5000l; // ms

	public static void main(String[] args) {
		
		logger.info("MatrixEntry starting");
		
		List<IAccount> accounts;
		/**/
		accounts = new ArrayList<IAccount>();
		ConcreteAcc acc1 = new ConcreteAcc(1);
		ConcreteAcc acc2 = new ConcreteAcc(2);
		accounts.add(acc1);
		accounts.add(acc2);

		// Формирование списков заданий
		Homeworks howmworks = new Homeworks();
		MakeHowmworks(howmworks);

		Brain brain = new Brain(howmworks);
		Engine engine = new Engine();

		brain.setAccounts(accounts);
		engine.setAccounts(accounts);
		
		// Запустить формирование тайминга
		howmworks.notifyObservers();

		@SuppressWarnings("unused")
		ActionsObserver currentDisplay = new ActionsObserver(engine);
		
		/*engine.setUserAction(1, "Like");
		
		engine.setUserAction(2, "act2");
		engine.setUserAction(1, "act3"); */
		
		DbConnectSingle dbConnector = DbConnectSingle.getInstance();

		try {
			while (true) {
				long moment = System.currentTimeMillis();
				logger.debug("MatrixEntry tick : {}", Constants.dfm.format(moment));
				// Accounts refreshing
				//List<IAccount> accounts = dbConnector.getAccounts();
				brain.setAccounts(accounts);
				engine.setAccounts(accounts);

				// Homeworks refreshing
				//dbConnector.getHomeworks();
							
				engine.ReadTimings(moment);

				Thread.sleep(tick);
			}
		} catch (InterruptedException e) {
			logger.error("MatrixEntry cycle exception :", e);
			logger.debug("MatrixEntry cycle exception :", e);
		}
		logger.info("MatrixEntry finishing");
	}

	private static void MakeHowmworks(Homeworks howmworks) {

		JobList ReTwitList = new JobList(Constants.ReTwit,
				Constants.JobType.ReTwit);
		JobList TwitList = new JobList(Constants.Twit,
				Constants.JobType.Twit);
		JobList SetAvaList = new JobList(Constants.SetAva,
				Constants.JobType.SetAva);

		for (int i = 0; i < 50; i++) {
			JobAtom job = new JobAtom(i, Constants.JobType.Like);
			TwitList.AddJob(job);
		}

		howmworks.AddList(ReTwitList);
		howmworks.AddList(TwitList);
		howmworks.AddList(SetAvaList);
	}
}
