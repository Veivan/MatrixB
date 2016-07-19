package inrtfs;

import java.util.List;

import jobs.JobAtom;
import jobs.JobList;

public interface IAccount {
	int getAccID();
	long getLastActivity();
	boolean IsActive(long moment);
	
	/** Метод - получает из тайминга задание, соответствующее по времени <b>moment</b>.*/
	JobAtom getTimedJob(long moment);

	boolean Auth();
	// Profile
	void SetAva();
	void SetBackgrnd();
	// Action
	void Tweet(String mess);
	void ReTweet(int twID, String mess);
	void Like(int twID);
	void Answer(int twID);
	// Lists
	void Follow(int userID);
	void UnFollow(int userID);
	void RebuldAccTiming(List<JobList> homeworks);
}
