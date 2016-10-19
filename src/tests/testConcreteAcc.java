package tests;

import static org.junit.Assert.*;
import jobs.Homeworks;
import jobs.JobAtom;
import jobs.JobList;
import main.ConcreteAcc;

import org.junit.Before;
import org.junit.Test;

import service.Constants;

public class testConcreteAcc {
	ConcreteAcc acc;

	@Before
	public void setUp() throws Exception {
		acc = new ConcreteAcc(1);
	}

	@Test
	public void testGetTimedJob() {
		acc.getTimedJob(System.currentTimeMillis());
	}

	public static void main(String[] args) {
		ConcreteAcc acc = new ConcreteAcc(1);
		Homeworks howmworks = new Homeworks();
		MakeHowmworks(howmworks);
		acc.RebuldAccTiming(howmworks);
		long moment = System.currentTimeMillis();
		while (true) {
			acc.getTimedJob(moment);
		}
	}

	private static void MakeHowmworks(Homeworks howmworks) {

		JobList TwitList = new JobList(Constants.Twit, Constants.JobType.Twit);

		for (int i = 0; i < 50; i++) {
			JobAtom job = new JobAtom(i, Constants.JobType.Twit);
			TwitList.AddJob(job);
		}

		// Добавлять в класс в порядке приоритета
		howmworks.AddList(TwitList);
	}
}
