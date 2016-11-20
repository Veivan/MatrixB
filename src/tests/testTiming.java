package tests;

import jobs.Homeworks;
import jobs.JobAtom;
import jobs.JobList;

import org.junit.Before;
import org.junit.Test;

import main.Timing;

public class testTiming {

	Homeworks homeworks = new Homeworks();
	Timing timing;

	@Before
	public void setUp() throws Exception {
		MakeHowmworks(homeworks);
		timing = new Timing();
	}

	@Test
	public void testRebuildTiming() {
		timing.RebuildTiming(homeworks);
		timing.printTiming();
	}

	private static void MakeHowmworks(Homeworks homeworks) {
		for (int i = 0; i < 5; i++) {
			JobAtom job = new JobAtom((long)i, "TWIT", "qq");
			homeworks.AddJob(job);
		}
	}

	public static void main(String[] args) {
		Homeworks homeworks = new Homeworks();
		MakeHowmworks(homeworks);
		for (JobList jobList : homeworks) {
			System.out.printf("Value: %s \n", String.valueOf(jobList.getPriority()));
		}
	}

}
