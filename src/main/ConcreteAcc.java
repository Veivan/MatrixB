package main;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;

import inrtfs.IAccount;

public class ConcreteAcc implements IAccount {
	private int AccID;
	private static final String cTimeZone = "GMT+3";
	private int WakeHour = 9; // Begin activity
	private int BedHour = 23; // End activity
	private int Lounch = 12; // No activity
	private int Supper = 19; // No activity

	private int ActiveH = BedHour - WakeHour; // Number of active hours

	private List<IAccount> FolwrsList;
	private List<IAccount> FolwngList;

	// Unix time of last action. Set by class methods.
	private long LastActivity;
	// Unix time of next action. Set by Brain.
	public long NextActivity;

	public int Tweets2Send = 6; // TODO make count random
	public ArrayList<Long> Timing = new ArrayList<Long>();
	
	public ConcreteAcc(int AccID) {
		this.AccID = AccID;
		this.LastActivity = System.currentTimeMillis();
		MakeTiming();
	}

	private void MakeTiming() {
		Random random = new Random();
		Set<Integer> intset = new HashSet<Integer>();
		while (intset.size() < Tweets2Send) {
			int h = random.nextInt(ActiveH) + WakeHour;
			if (h == Lounch || h == Supper)
				continue;
			intset.add(h);
		}
		Integer[] myArray = {};
		myArray = intset.toArray(new Integer[intset.size()]);
		Arrays.sort(myArray);
		// Randomize time inside the hour
		GregorianCalendar date = new GregorianCalendar(TimeZone.getTimeZone(cTimeZone));
		for (int i = 0; i < myArray.length; i++) {
			int h = myArray[i];
			//System.out.printf("Value: %s \n", String.valueOf(h));
			int m = random.nextInt(58) + 1;
			date.set(Calendar.HOUR_OF_DAY, h); 
			date.set(Calendar.MINUTE, m);
			date.set(Calendar.SECOND, random.nextInt(58) + 1);
			Timing.add(date.getTimeInMillis());
		}
	}
	
	public void printTiming() {
	    DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		for (Long tm : Timing) {
			Date d = new Date(tm);
			System.out.printf("%s \n", dfm.format(d));			
		}
	}

	@Override
	public int getAccID() {
		return AccID;
	}

	@Override
	public boolean Auth() {
		// TODO Auto-generated method stub
		LastActivity = System.currentTimeMillis();
		return true;
	}

	@Override
	public void SetAva() {
		// TODO Auto-generated method stub

		LastActivity = System.currentTimeMillis();
	}

	@Override
	public void SetBackgrnd() {
		// TODO Auto-generated method stub

		LastActivity = System.currentTimeMillis();
	}

	@Override
	public void Tweet(String mess) {
		// TODO Auto-generated method stub

		LastActivity = System.currentTimeMillis();
	}

	@Override
	public void ReTweet(int twID, String mess) {
		// TODO Auto-generated method stub

		LastActivity = System.currentTimeMillis();
	}

	@Override
	public void Like(int twID) {
		// TODO Auto-generated method stub

		LastActivity = System.currentTimeMillis();
	}

	@Override
	public void Answer(int twID) {
		// TODO Auto-generated method stub

		LastActivity = System.currentTimeMillis();
	}

	@Override
	public void Follow(int userID) {
		// TODO Auto-generated method stub

		LastActivity = System.currentTimeMillis();
	}

	@Override
	public void UnFollow(int userID) {
		// TODO Auto-generated method stub

		LastActivity = System.currentTimeMillis();
	}

	@Override
	public long getLastActivity() {
		return LastActivity;
	}

	@Override
	public boolean IsActive(long moment) {
		// TODO Auto-generated method stub
		return false;
	}

}
