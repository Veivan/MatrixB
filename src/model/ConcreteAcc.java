package model;

import java.util.List;

import service.Constants;
import jobs.Homeworks;
import jobs.JobAtom;
import inrtfs.IAccount;
import main.Timing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConcreteAcc implements IAccount {
	private long AccID;
	private String email;
	private String pass;    
	private String name; 
	private String phone;
	private String mailpass;
	private String screenname; 
	private long twitter_id = -1;
	
	private final String cTimeZone = "GMT+3";
	private static final long late = 300000l; // ms = 5min

	private Regimen regim = new Regimen();

	private List<IAccount> FolwrsList;
	private List<IAccount> FolwngList;
	private List<IAccount> UnFolwdList;

	private Timing timing;

	static Logger logger = LoggerFactory.getLogger(ConcreteAcc.class);

	public ConcreteAcc(long AccID) {
		this.AccID = AccID;
		this.regim = new Regimen();
		this.timing = new Timing(this.cTimeZone, this.regim);
	}

	/**
	 * Используется для импорта акков
	 */
	public ConcreteAcc(long user_id, String email, String pass, String name) {
		super();
		this.AccID = user_id;
		this.email = email;
		this.pass = pass;
		this.name = name;
	}

	/**
	 * Используется для импорта акков
	 */
	public ConcreteAcc(long user_id, String email, String pass, String name, String phone, String mailpass) {
		super();
		this.AccID = user_id;
		this.email = email;
		this.pass = pass;
		this.name = name;
		this.phone = phone;
		this.mailpass = mailpass;
	}
	
	public void RebuldAccTiming(Homeworks homeworks) {
		this.timing.RebuildTiming(homeworks);
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
			logger.debug("id={} job : {}, moment : {}", this.AccID,
					Constants.dfm.format(job.timestamp),
					Constants.dfm.format(moment));
			if (job.timestamp <= moment && !job.IsFinished) {
				job.IsFinished = true;
				if ((moment - job.timestamp) > late) {
					logger.info(
							"ConcreteAcc id={} found job too late : {}, moment : {}",
							this.AccID, Constants.dfm.format(job.timestamp),
							Constants.dfm.format(moment));
				} else {
					logger.info(
							"ConcreteAcc id={} found job : {}, moment : {}",
							this.AccID, Constants.dfm.format(job.timestamp),
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

	public String getScreenname() {
		return screenname;
	}

	public void setScreenname(String screenname) {
		this.screenname = screenname;
	}

	public long getTwitter_id() {
		return twitter_id;
	}

	public void setTwitter_id(long twitter_id) {
		this.twitter_id = twitter_id;
	}
}
