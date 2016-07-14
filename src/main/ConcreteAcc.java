package main;

import java.util.List;

import inrtfs.IAccount;

public class ConcreteAcc implements IAccount {
	private int AccID;
    private int TimeZone;
    private int WakeHour = 8; // Begin activity
    private int BedHour = 23; // End activity
    
	private List<IAccount> FolwrsList;
    private List<IAccount> FolwngList;

    private long LastActivity;

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

}
