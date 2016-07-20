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
		timing.RebuildTiming(howmworks.HomeworksList);
		timing.printTiming();
	}

	private static void MakeHowmworks(Homeworks howmworks) {

		JobList ReTweetList = new JobList(Constants.ReTweet,
				Constants.JobType.ReTweet);
		JobList TweetList = new JobList(Constants.Tweet,
				Constants.JobType.Tweet);
		JobList SetAvaList = new JobList(Constants.SetAva,
				Constants.JobType.SetAva);

		for (int i = 0; i < 5; i++) {
			JobAtom job = new JobAtom(i, Constants.JobType.Tweet);
			TweetList.AddJob(job);
		}

		// Добавлять в класс в порядке приоритета
		howmworks.AddList(ReTweetList);
		howmworks.AddList(TweetList);
		howmworks.AddList(SetAvaList);
	}
}
