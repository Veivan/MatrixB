package main;

import inrtfs.IAccount;
import inrtfs.Observer;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import jobs.Homeworks;
import jobs.JobList;

/** Класс наблюдает за списком списков заданий
 * <b>howmworks</b> для перечня аккаунтов <b>accounts</b>.
 * При изменении <b>howmworks</b> производит пересчёт таймингов для <b>accounts</b>.
*/
public class Brain implements Observer{
	private List<IAccount> accounts;
	private Homeworks howmworks;
	private List<JobList> HomeworksList;

	private static final long tick = 5000l; // ms
	private static final long delay = tick * 1l; // ms

	public Brain(List<IAccount> accounts, Homeworks howmworks) {
		this.accounts = accounts;
		this.howmworks = howmworks;
		this.howmworks.registerObserver(this);
	}

	public MatrixAct getAction() {
		// MatrixAct act = new MatrixAct(1, "act1");
		MatrixAct act = ThinkAboutTweet();
		return act;
	};

	private MatrixAct ThinkAboutTweet() {
		MatrixAct act = null;
		long now = System.currentTimeMillis();
		for (IAccount acc : accounts) {
			if (now - acc.getLastActivity() > delay) {
				act = new MatrixAct(acc.getAccID(), "DoTweet");
			}
		}
		return act;
	};

	// Считать остаток рабочего времени (за минусом обеда)
	// Считать число оставшихся простых твитов и число заданий.
	// Равномерно распределить экшэны по оставшемуся времени.
	private long GetNextActivity() {

		
		long NextActivity = 0l;

		return NextActivity;
	}

	@Override
	public void perform(List<JobList> HomeworksList) {
		this.HomeworksList = HomeworksList;
	}

	@Override
	public void update(List<MatrixAct> actionlist) {
		// not used		
	}
}
