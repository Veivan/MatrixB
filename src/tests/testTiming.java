package tests;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import jobs.Homeworks;

import org.junit.Before;
import org.junit.Test;

import dbaware.DbConnector;
import main.Timing;

public class testTiming {

	static DbConnector dbConnector = new DbConnector();
	//Homeworks homeworks = new Homeworks();
	Timing timing;
	Homeworks homeworks;
	private List<Integer> GroupIDs = new ArrayList<Integer>();

	@Before
	public void setUp() throws Exception {
		//MakeHowmworks(homeworks);
		long moment = System.currentTimeMillis();
		GroupIDs.add(0); // Для выбора заданий, относящихся ко всем группам
		homeworks = dbConnector.getHomeworks(moment);
		timing = new Timing();
		timing.RebuildTiming(homeworks, GroupIDs);
	}

/*	@Test
	public void testRebuildTiming() {
		timing.RebuildTiming(homeworks, GroupIDs);
		//timing.printTiming();
	} */
	
	@Test
	public void testReReadTiming() throws ParseException {
		DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd");
		String time = "2017-01-21";
		long newmoment = dfm.parse(time).getTime();
		Homeworks newschedule = dbConnector.getHomeworks(newmoment);
		boolean ischanged = homeworks.IsDifferent(newschedule);
		if (ischanged) {
			homeworks.ReplaceWith(newschedule);
			timing.RebuildTiming(homeworks, GroupIDs);
		}
		//timing.printTiming();
	}
	

/*	private static void MakeHowmworks(Homeworks homeworks) {
		for (int i = 0; i < 5; i++) {
			JobAtom job = new JobAtom((long)i, "TWIT", "qq");
			homeworks.AddJob(job);
		}
	} */

}
