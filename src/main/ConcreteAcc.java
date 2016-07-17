package main;

import java.util.List;

import inrtfs.IAccount;

public class ConcreteAcc implements IAccount {
	private int AccID;
    private int TimeZone;
    private int WakeHour = 8; // Begin activity
    private int BedHour = 23; // End activity
    private int Lounch = 12; // No activity
    private int Supper = 19; // No activity
    
    private List<IAccount> FolwrsList;
    private List<IAccount> FolwngList;

    // Unix time of last action. Set by class methods.
    private long LastActivity;
    // Unix time of next action. Set by Brain.   
    public long NextActivity;

    public int Tweets2Send = 5; 

    public ConcreteAcc(int AccID) {
    	this.AccID = AccID;
    	this.LastActivity = System.currentTimeMillis();
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
