package tests;

import jobs.Homeworks;
import jobs.JobAtom;
import jobs.JobList;

import org.junit.Before;
import org.junit.Test;

import service.Constants;
import main.Timing;

public class testTiming {

	Homeworks howmworks = new Homeworks();
	Timing timing;

	@Before
	public void setUp() throws Exception {
		MakeHowmworks(howmworks);
		timing = new Timing();
	}

	@Test
	public void testRebuildTiming() {
		timing.RebuildTiming(howmworks);
		timing.printTiming();
	}

	private static void MakeHowmworks(Homeworks howmworks) {

		JobList ReTwitList = new JobList(Constants.JobType.RETWIT);
		JobList TwitList = new JobList(Constants.JobType.TWIT);
		JobList SetAvaList = new JobList(Constants.JobType.SETAVA);

		for (int i = 0; i < 5; i++) {
			JobAtom job = new JobAtom(i, Constants.JobType.TWIT);
			TwitList.AddJob(job);
		}

		// Добавлять в класс в порядке приоритета
		howmworks.AddList(ReTwitList);
		howmworks.AddList(TwitList);
		howmworks.AddList(SetAvaList);
	}

	public static void main(String[] args) {
		Homeworks howmworks = new Homeworks();
		MakeHowmworks(howmworks);
		for (JobList jobList : howmworks) {
			System.out.printf("Value: %s \n", String.valueOf(jobList.getPriority()));
		}
	}

}
