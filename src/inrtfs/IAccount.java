package inrtfs;

import jobs.Homeworks;
import jobs.JobAtom;

public interface IAccount {
	int getAccID();

	/**
	 * Метод - получает из тайминга задание, соответствующее по времени
	 * <b>moment</b>.
	 */
	JobAtom getTimedJob(long moment);

	boolean Auth();

	// Profile
	void SetAva();

	void SetBackgrnd();

	// Action
	void Twit(String mess);

	void ReTwit(int twID, String mess);

	void Like(int twID);

	void Replay(int twID);

	void Direct(int twID);

	// Lists
	void Follow(int userID);

	void UnFollow(int userID);

	void RebuldAccTiming(Homeworks homeworks);
}
