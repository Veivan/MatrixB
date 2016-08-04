package main;

import java.util.Date;
import java.util.List;

import service.Constants;
import jobs.Homeworks;
import jobs.JobAtom;
import inrtfs.IAccount;

public class ConcreteAcc implements IAccount {
	private long AccID;
	private final String cTimeZone = "GMT+3";

	private Regimen regim = new Regimen();

	private List<IAccount> FolwrsList;
	private List<IAccount> FolwngList;
	private List<IAccount> UnFolwdList;

	private Timing timing;

	public ConcreteAcc(long AccID) {
		this.AccID = AccID;
		this.regim = new Regimen();
		this.timing = new Timing(this.cTimeZone, this.regim);
	}

	public void RebuldAccTiming(Homeworks homeworks) {
		this.timing.RebuildTiming(homeworks);
	}

	@Override
	public long getAccID() {
		return AccID;
	}

	@Override
	public JobAtom getTimedJob(long moment) {
		for (JobAtom job : timing) {
			if (job.timestamp <= moment) {
				Date d = new Date(job.timestamp);
				System.out.printf("%s \n", Constants.dfm.format(d));
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
