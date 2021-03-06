package model;

import java.util.ArrayList;
import java.util.List;

import service.Constants;
import service.GenderChecker.Gender;
import jobs.Homeworks;
import jobs.JobAtom;
import inrtfs.IAccount;
import main.Timing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dbaware.DbConnector;

public class ConcreteAcc implements IAccount {
	private long AccID;
	private String email;
	private String pass;    
	private String screenname; // Login
	private String phone;
	private String mailpass;
	private String name; 
	private long twitter_id = -1;
	private Gender gender = Gender.NEUTRAL;
	
	private final String cTimeZone = "GMT+3";
	private static final long late = 300000l; // ms = 5min

	private Regimen regim;

	private List<IAccount> FolwrsList;
	private List<IAccount> FriendsList;
	private List<IAccount> UnFolwdList;

	DbConnector dbConnector = DbConnector.getInstance();

	/**
	 * @return the folwrsList
	 */
	public List<IAccount> getFolwrsList() {
		return FolwrsList;
	}

	/**
	 * @param folwrsList the folwrsList to set
	 */
	public void setFolwrsList(List<IAccount> folwrsList) {
		FolwrsList = folwrsList;
	}

	/**
	 * @return the folwngList
	 */
	public List<IAccount> getFolwngList() {
		return FriendsList;
	}

	/**
	 * @return the unFolwdList
	 */
	public List<IAccount> getUnFolwdList() {
		return UnFolwdList;
	}


	/**
	 * Перечень групп, к которым относится акк
	 */
	private List<Integer> GroupIDs = new ArrayList<Integer>();

	private Timing timing;

	static Logger logger = LoggerFactory.getLogger(ConcreteAcc.class);

	public ConcreteAcc(long AccID, int intgender) {
		this.AccID = AccID;
		//GroupIDs.add(0); // Для выбора заданий, относящихся ко всем группам
		// Акк будет относиться только к одной группе
		GroupIDs.addAll(dbConnector.getAccountGroupIDs(AccID));
		if(GroupIDs.size() > 0)
			this.regim = dbConnector.selectRegimByGroupID(GroupIDs.get(0));
		this.timing = new Timing(this.cTimeZone, this.regim, AccID);
		if (intgender > -1)
			this.gender = Gender.values()[intgender];
	}

	/**
	 * @param email the email to set
	 */
	public final void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @param pass the pass to set
	 */
	public final void setPass(String pass) {
		this.pass = pass;
	}

	/**
	 * @param screenname the screenname to set
	 */
	public final void setScreenname(String screenname) {
		this.screenname = screenname;
	}

	/**
	 * @param phone the phone to set
	 */
	public final void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @param mailpass the mailpass to set
	 */
	public final void setMailpass(String mailpass) {
		this.mailpass = mailpass;
	}

	/**
	 * Используется для импорта акков
	 */
	public ConcreteAcc(long user_id, String email, String pass, String name) {
		super();
		this.AccID = user_id;
		this.email = email;
		this.pass = pass;
		this.screenname = name;
	}

	/**
	 * Используется для импорта акков
	 */
	public ConcreteAcc(long user_id, String email, String pass, String name, String phone, String mailpass) {
		super();
		this.AccID = user_id;
		this.email = email;
		this.pass = pass;
		this.screenname = name;
		this.phone = phone;
		this.mailpass = mailpass;
	}
	
	/**
	 * Используется для импорта акков
	 */
	public ConcreteAcc(long user_id, String email, String pass, String name, String mailpass) {
		super();
		this.AccID = user_id;
		this.email = email;
		this.pass = pass;
		this.screenname = name;
		this.mailpass = mailpass;
	}
	
	public void RebuldAccTiming(Homeworks homeworks) {
		long moment = System.currentTimeMillis();
		this.timing.RebuildTiming(homeworks, this.GroupIDs, moment);
		//logger.debug("Rebuilded Accid={}", this.AccID); 
		this.timing.StoreTiming(this.AccID);
		//logger.debug("Stored Accid={}", this.AccID); 
	}

	public void printTiming() {
		this.timing.printTiming();
	}

	@Override
	public long getAccID() {
		return AccID;
	}

	/**
	 * @param accID the accID to set
	 */
	public void setAccID(long accID) {
		AccID = accID;
	}

	@Override
	public JobAtom getTimedJob(long moment) {
		timing.First();
		for (JobAtom job : timing) {
			/*logger.debug("id={} job : {}, moment : {}", this.AccID,
					Constants.dfm.format(job.timestamp),
					Constants.dfm.format(moment)); */
			if (job.timestamp <= moment && !job.IsFinished) {
				job.IsFinished = true;
				if ((moment - job.timestamp) > late) {
					logger.info(
							"ConcreteAcc id={} found job {} too late : {}, moment : {}",
							this.AccID, job.Type.name(), Constants.dfm.format(job.timestamp),
							Constants.dfm.format(moment));
				} else {
					logger.info(
							"ConcreteAcc id={} found job {} : {}, moment : {}",
							this.AccID, job.Type.name(), Constants.dfm.format(job.timestamp),
							Constants.dfm.format(moment));
					return job;
				}
			}
		}
		return null;
	}

	/** Метод - удаляет задание из тайминга. */
	public void RemoveJob(int JobID) {
		// TODO for (Long tm : Timing) {
		// }
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

	public String getScreenname() {
		return screenname;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public long getTwitter_id() {
		return twitter_id;
	}

	public void setTwitter_id(long twitter_id) {
		this.twitter_id = twitter_id;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}
}
