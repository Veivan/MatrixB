package model;

/**
 * Используется для импорта акков
 */
public class AccIdent {
	private long user_id;
	private String email;
	private String pass;    
	private String name; 

	public AccIdent(long user_id, String email, String pass, String name) {
		super();
		this.user_id = user_id;
		this.email = email;
		this.pass = pass;
		this.name = name;
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

}
