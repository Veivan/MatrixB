package main;

import inrtfs.IAccount;
import jobs.JobAtom;

public class MatrixAct {
	public int AccID;
	public String ActionTXT;

	public MatrixAct(int i, String string) {
		this.AccID = i;
		this.ActionTXT = string;
	}

	public MatrixAct(JobAtom job, IAccount acc) {
		this.AccID = acc.getAccID();
		this.ActionTXT = job.Type.name();
	}
}
