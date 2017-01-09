package model;

/**
 * Используется для импорта акков
 */
public class AccIdent {
	private long user_id;
	private String email;
	private String pass;    
	private String name; 
	private String phone;
	private String mailpass;

	public AccIdent(long user_id, String email, String pass, String name) {
		super();
		this.user_id = user_id;
		this.email = email;
		this.pass = pass;
		this.name = name;
	}
	
	public AccIdent(long user_id, String email, String pass, String name, String phone, String mailpass) {
		super();
		this.user_id = user_id;
		this.email = email;
		this.pass = pass;
		this.name = name;
		this.phone = phone;
		this.mailpass = mailpass;
	}
	
	/**
	 * @return the user_id
	 */
	public long getUser_id() {
		return user_id;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @return the pass
	 */
	public String getPass() {
		return pass;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @return the email pass
	 */
	public String getMailpass() {
		return mailpass;
	}
}
