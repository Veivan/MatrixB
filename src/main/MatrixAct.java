package main;

import java.util.Random;

import inrtfs.IAccount;
import jobs.JobAtom;

public class MatrixAct {

	private JobAtom job;
	private IAccount acc;

	private long ID;

	public MatrixAct(JobAtom ajob, IAccount acc) {
		this.job = new JobAtom(ajob);
		this.acc = acc;

		Random random = new Random();
		this.ID = random.nextInt();
	}

	public JobAtom getJob() {
		return job;
	}

	public IAccount getAcc() {
		return acc;
	}

	public long getSelfID() {
		return this.ID;
	}

}
