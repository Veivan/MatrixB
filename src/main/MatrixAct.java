package main;

import inrtfs.IAccount;
import jobs.JobAtom;

public class MatrixAct {

	private JobAtom job;
	private IAccount acc;

	private long AccID;
	private String ActionTXT;

	public MatrixAct(int i, String string) {
		this.AccID = i;
		this.setActionTXT(string);
	}

	public MatrixAct(JobAtom job, IAccount acc) {
		this.job = job;
		this.acc = acc;

		this.AccID = acc.getAccID();
		this.setActionTXT(job.Type.name());
	}

	public JobAtom getJob() {
		return job;
	}

	public String getActionTXT() {
		return ActionTXT;
	}

	private void setActionTXT(String actionTXT) {
		ActionTXT = actionTXT;
	}

}
