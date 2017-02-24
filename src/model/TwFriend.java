package model;

/**
 * Server to Follow operations
 *
 */
public class TwFriend {
	private String ScreenName;
	private long twitter_id;
	
	public TwFriend(String screenName, long twitter_id) {
		ScreenName = screenName;
		this.twitter_id = twitter_id;
	}

	/**
	 * @return the screenName
	 */
	public String getScreenName() {
		return ScreenName;
	}

	/**
	 * @return the twitter_id
	 */
	public long getTwitter_id() {
		return twitter_id;
	}
}
