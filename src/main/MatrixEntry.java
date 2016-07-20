package main;

import inrtfs.IAccount;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import service.Constants;
import jobs.Homeworks;
import jobs.JobAtom;
import jobs.JobList;

public class MatrixEntry {

	private static final long tick = 5000l; // ms

	public static void main(String[] args) {
		List<IAccount> accounts = new ArrayList<IAccount>();
		ConcreteAcc acc1 = new ConcreteAcc(1);
		// ConcreteAcc acc2 = new ConcreteAcc(2);
		accounts.add(acc1);
		// accounts.add(acc2);

		// Формирование списков заданий
		Homeworks howmworks = new Homeworks();
		MakeHowmworks(howmworks);

		@SuppressWarnings("unused")
		Brain brain = new Brain(accounts, howmworks);
		Engine engine = new Engine(accounts);

		// Запустить формирование тайминга
		howmworks.notifyObservers();

		@SuppressWarnings("unused")
		ActionsObserver currentDisplay = new ActionsObserver(engine);
		engine.setUserAction(1, "act1");
		engine.setUserAction(2, "act2");
		engine.setUserAction(1, "act3");
		try {
			while (true) {
				Date ndate = new Date();
				System.out.println(ndate);
				long moment = System.currentTimeMillis();
				engine.ReadTimings(moment);

				Thread.sleep(tick);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void MakeHowmworks(Homeworks howmworks) {

		JobList ReTwitList = new JobList(Constants.ReTwit,
				Constants.JobType.ReTwit);
		JobList TwitList = new JobList(Constants.Twit,
				Constants.JobType.Twit);
		JobList SetAvaList = new JobList(Constants.SetAva,
				Constants.JobType.SetAva);

		for (int i = 0; i < 5; i++) {
			JobAtom job = new JobAtom(i, Constants.JobType.Twit);
			TwitList.AddJob(job);
		}

		howmworks.AddList(ReTwitList);
		howmworks.AddList(TwitList);
		howmworks.AddList(SetAvaList);
	}
}
