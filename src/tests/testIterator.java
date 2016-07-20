package tests;

import jobs.JobAtom;
import jobs.JobList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import service.Constants;
import service.InnerListIteratior;

public class testIterator {
	JobList ReTweetList = new JobList(Constants.ReTweet,
			Constants.JobType.ReTweet);
	InnerListIteratior iterator;

	@Before
	public void setUp() throws Exception {
		for (int i = 0; i < 5; i++) {
			JobAtom job = new JobAtom(i, Constants.JobType.ReTweet);
			ReTweetList.AddJob(job);
		} 
		iterator = new InnerListIteratior(ReTweetList);
	}

	@Test
	public void testFirst() {
		Assert.assertEquals(iterator.First(), ReTweetList.Element(0));
	}

	@Test
	public void testNext() {
		iterator.First();
		Assert.assertEquals(iterator.next(), ReTweetList.Element(1));
	}

	@Test
	public void testIterate() {
		JobAtom job = (JobAtom)iterator.First();
		do {
			if (job != null) {
				System.out.printf("job %s \n", job.JobID);
			}
			job = (JobAtom)iterator.next();			
		} while (iterator.hasNext());
	}

}
