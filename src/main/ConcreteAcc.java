package main;

import java.util.Date;
import java.util.List;

import service.Constants;
import service.InnerListIteratior;
import jobs.JobAtom;
import jobs.JobList;
import inrtfs.IAccount;

public class ConcreteAcc implements IAccount {
	private int AccID;
	private final String cTimeZone = "GMT+3";
	
	private Regimen regim = new Regimen();

	private List<IAccount> FolwrsList;
	private List<IAccount> FolwngList;
	private List<IAccount> UnFolwdList;

	// Unix time of last action. Set by class methods.
	private long LastActivity;
	// Unix time of next action. Set by Brain.
	public long NextActivity;


	private Timing timing;


	public ConcreteAcc(int AccID) {
		this.AccID = AccID;
		this.LastActivity = System.currentTimeMillis();

		this.regim = new Regimen();
		this.timing = new Timing(this.cTimeZone, this.regim);
	}

	public void RebuldAccTiming(List<JobList> homeworks) {	
		this.timing.RebuildTiming(homeworks);		
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
	public void Replay(int twID) {
		// TODO Auto-generated method stub

		LastActivity = System.currentTimeMillis();
	}

	@Override
	public void Direct(int twID) {
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

	@Override
	public JobAtom getTimedJob(long moment) {
		InnerListIteratior iterator = new InnerListIteratior(timing);
		JobAtom job = (JobAtom) iterator.First();
		do {
			if (job != null && job.timestamp <= moment) {
				Date d = new Date(job.timestamp);
				System.out.printf("%s \n", Constants.dfm.format(d));
				return job;
			}
			job = (JobAtom)iterator.next();			
		} while (iterator.hasNext());	
		return null;
	}

	/** Метод - удаляет задание из тайминга. */
	public void RemoveJob(int JobID) {
	// TODO	for (Long tm : Timing) {
		//}
	}

}
