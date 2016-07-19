package main;

import inrtfs.IAccount;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import settings.Constants;
import jobs.Homeworks;
import jobs.JobList;

public class MatrixEntry {

	public static void main(String[] args) {
		List<IAccount> accounts = new ArrayList<IAccount>();
		ConcreteAcc acc1 = new ConcreteAcc(1);
		//ConcreteAcc acc2 = new ConcreteAcc(2);
		accounts.add(acc1);
		//accounts.add(acc2);

		// Формирование списков заданий
		Homeworks howmworks = new Homeworks();
		MakeHowmworks(howmworks);
		
		Brain brain = new Brain(accounts, howmworks);
		Engine engine = new Engine(accounts);

		@SuppressWarnings("unused")
		ActionsObserver currentDisplay = new ActionsObserver(engine);
		engine.setUserAction(1, "act1");
		engine.setUserAction(2, "act2");
		engine.setUserAction(1, "act3");
		try {
			while (true) {
				Date ndate = new Date();
				System.out.println(ndate);
				
				engine.update(brain.getAction());
				
				Thread.sleep(5 * 1000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private static void MakeHowmworks(Homeworks howmworks)
	{
		JobList ReTweetList = new JobList(Constants.ReTweet, Constants.JobType.ReTweet);
		JobList TweetList = new JobList(Constants.Tweet, Constants.JobType.Tweet);
		JobList SetAvaList = new JobList(Constants.SetAva, Constants.JobType.SetAva);

		// Добавлять в класс в порядке приоритета
		howmworks.HomeworksList.add(ReTweetList);
		howmworks.HomeworksList.add(TweetList);
		howmworks.HomeworksList.add(SetAvaList);
	}
}
