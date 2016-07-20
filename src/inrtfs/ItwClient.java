package inrtfs;

public interface ItwClient {
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


}
