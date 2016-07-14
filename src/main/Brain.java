package main;

import inrtfs.IAccount;
import java.util.List;

public class Brain {
	private List<IAccount> accounts;
	
	private static final long delay = 10000l; // ms

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

}
