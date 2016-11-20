package main;

import java.util.Random;

import inrtfs.IAccount;
import jobs.JobAtom;

public class MatrixAct {

	private JobAtom job;
	private IAccount acc;

	private long AccID;
	private String ActionTXT;

	private long ID;

	public MatrixAct(JobAtom ajob, IAccount acc) {
		this.job = new JobAtom(ajob);
		this.acc = acc;

		this.AccID = acc.getAccID();
		this.setActionTXT(ajob.Type.name());

		Random random = new Random();
		this.ID = random.nextInt();
	}

	public JobAtom getJob() {
		return job;
	}

	public IAccount getAcc() {
		return acc;
	}

	public long getAccID() {
		return AccID;
	}

	public long getSelfID() {
		return this.ID;
	}

	public String getActionTXT() {
		return ActionTXT;
	}

	private void setActionTXT(String actionTXT) {
		ActionTXT = actionTXT;
	}

}
