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
	//DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd");
	DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final long tick = 5000l; // ms

	static DbConnector dbConnector = DbConnector.getInstance();
	//Homeworks homeworks = new Homeworks();
	Timing timing;
	Homeworks homeworks;
	private List<Integer> GroupIDs = new ArrayList<Integer>();
	long AccID = 130L;

	@Before
	public void setUp() throws Exception {
		//MakeHowmworks(homeworks);
		//String time = "2017-02-27 00:00:01";
		String time = "2017-03-02 13:30:00";
		long moment = dfm.parse(time).getTime();
		//long moment = System.currentTimeMillis();
		
		GroupIDs.add(0); // Для выбора заданий, относящихся ко всем группам
		homeworks = dbConnector.getHomeworks(moment);
		timing = new Timing(AccID);
		timing.RebuildTiming(homeworks, GroupIDs, moment);
	}

	@Test
	public void testRebuildTiming() {

	}
	
	//@Test
	public void testCheckHomework() {
		//String time = "2017-02-28";
		try {
			while (true) {
				//long moment = dfm.parse(time).getTime();
				long moment = System.currentTimeMillis();
				
				// Homeworks refreshing
				Homeworks newschedule = dbConnector.getHomeworks(moment);
				boolean ischanged = homeworks.IsDifferent(newschedule);
				if (ischanged) {
					homeworks.ReplaceWith(newschedule);
				}

				Thread.sleep(tick);
			}
		} catch (Exception e) {
		}
	} 
	
	//@Test
	public void testReReadTiming() throws ParseException {
		String time = "2017-02-28";
		long newmoment = dfm.parse(time).getTime();
		Homeworks newschedule = dbConnector.getHomeworks(newmoment);
		boolean ischanged = homeworks.IsDifferent(newschedule);
		if (ischanged) {
			homeworks.ReplaceWith(newschedule);
			timing.RebuildTiming(homeworks, GroupIDs, newmoment);
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
