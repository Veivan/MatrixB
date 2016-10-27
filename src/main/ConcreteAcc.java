package main;

import java.util.List;

import service.Constants;
import jobs.Homeworks;
import jobs.JobAtom;
import inrtfs.IAccount;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConcreteAcc implements IAccount {
	private long AccID;
	private final String cTimeZone = "GMT+3";

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

	public void RebuldAccTiming(Homeworks homeworks) {
		this.timing.RebuildTiming(homeworks);
	}
	
	public void printTiming()
	{
		this.timing.printTiming();
	}

	@Override
	public long getAccID() {
		return AccID;
	}

	@Override
	public JobAtom getTimedJob(long moment) {
		timing.First();
		for (JobAtom job : timing) {
			logger.debug("id={} job : {}, moment : {}", this.AccID, Constants.dfm.format(job.timestamp), Constants.dfm.format(moment));
			if (job.timestamp <= moment && !job.IsFinished) {
				job.IsFinished = true;
				logger.info("ConcreteAcc id={} found job : {}, moment : {}", this.AccID, Constants.dfm.format(job.timestamp), Constants.dfm.format(moment));
				return job;
			}
		}
		return null;
	}

	/** Метод - удаляет задание из тайминга. */
	public void RemoveJob(int JobID) {
		// TODO for (Long tm : Timing) {
		// }
	}

}
