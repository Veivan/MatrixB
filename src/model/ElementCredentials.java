package model;

public class ElementCredentials {
	private String CONSUMER_KEY;
	private String CONSUMER_SECRET;
	private String USER;
	private String USER_PASS;

	private String ACCESS_TOKEN;
	private String ACCESS_TOKEN_SECRET;
	
	private long user_id;
	private long id_app;

	public ElementCredentials(String ConsumerKey, String ConsumerSecret,
			String User, String UserPass, String AccessToken,
			String AccessTokenSecret, long user_id, long id_app) {
		this.setCONSUMER_KEY(ConsumerKey);
		this.setCONSUMER_SECRET(ConsumerSecret);
		this.setUSER(User);
		this.setUSER_PASS(UserPass);
		this.setACCESS_TOKEN(AccessToken);
		this.setACCESS_TOKEN_SECRET(AccessTokenSecret);
		this.setUser_id(user_id);
		this.setId_app(id_app);
	}

	public String getCONSUMER_KEY() {
		return CONSUMER_KEY;
	}

	public void setCONSUMER_KEY(String cONSUMER_KEY) {
		CONSUMER_KEY = cONSUMER_KEY;
	}

	public String getCONSUMER_SECRET() {
		return CONSUMER_SECRET;
	}

	public void setCONSUMER_SECRET(String cONSUMER_SECRET) {
		CONSUMER_SECRET = cONSUMER_SECRET;
	}

	public String getUSER() {
		return USER;
	}

	public void setUSER(String uSER) {
		USER = uSER;
	}

	public String getUSER_PASS() {
		return USER_PASS;
	}

	public void setUSER_PASS(String uSER_PASS) {
		USER_PASS = uSER_PASS;
	}

	public String getACCESS_TOKEN() {
		return ACCESS_TOKEN;
	}

	public void setACCESS_TOKEN(String aCCESS_TOKEN) {
		ACCESS_TOKEN = aCCESS_TOKEN;
	}

	public String getACCESS_TOKEN_SECRET() {
		return ACCESS_TOKEN_SECRET;
	}

	public void setACCESS_TOKEN_SECRET(String aCCESS_TOKEN_SECRET) {
		ACCESS_TOKEN_SECRET = aCCESS_TOKEN_SECRET;
	}

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public long getId_app() {
		return id_app;
	}

	public void setId_app(long id_app) {
		this.id_app = id_app;
	}

}
