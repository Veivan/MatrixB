package main;

import inrtfs.IAccount;

import java.util.List;
import java.util.Random;

public class Brain {
	private List<IAccount> accounts;
	
	private static final long tick = 5000l; // ms
	private static final long delay = tick * 1l; // ms

	public Brain(List<IAccount> accounts) {
		this.accounts = accounts;
	}

	public MatrixAct getAction() {
		//MatrixAct act = new MatrixAct(1, "act1");
		MatrixAct act = ThinkAboutTweet();
		return act;
	};

	private MatrixAct ThinkAboutTweet() {
		MatrixAct act = null;
		long now =  System.currentTimeMillis();
		for (IAccount acc : accounts){
			if (now - acc.getLastActivity() > delay) {
				act = new MatrixAct(acc.getAccID(), "DoTweet");
			}
		}
		return act;
	};

	private long CalcNextActivity() {
		long NextActivity = 0l;
	    Random random = new Random();

	    int tickscnt = random.nextInt(3);

	    return NextActivity;
	}
}
