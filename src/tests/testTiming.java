package tests;

import java.util.ArrayList;
import java.util.List;

import jobs.Homeworks;

import org.junit.Before;
import org.junit.Test;

import dbaware.DbConnectSingle;
import main.Timing;

public class testTiming {

	static DbConnectSingle dbConnector = DbConnectSingle.getInstance();
	//Homeworks homeworks = new Homeworks();
	Timing timing;
	Homeworks homeworks;
	private List<Integer> GroupIDs = new ArrayList<Integer>();

	@Before
	public void setUp() throws Exception {
		//MakeHowmworks(homeworks);
		GroupIDs.add(0); // Для выбора заданий, относящихся ко всем группам
		homeworks = dbConnector.getHomeworks();
		timing = new Timing();
	}

	@Test
	public void testRebuildTiming() {
		timing.RebuildTiming(homeworks, GroupIDs);
		//timing.printTiming();
	}

/*	private static void MakeHowmworks(Homeworks homeworks) {
		for (int i = 0; i < 5; i++) {
			JobAtom job = new JobAtom((long)i, "TWIT", "qq");
			homeworks.AddJob(job);
		}
	} */

}
