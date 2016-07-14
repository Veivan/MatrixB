package inrtfs;

public interface IUser {
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
}
