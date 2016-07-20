package main;

import java.util.List;

import inrtfs.IAccount;
import inrtfs.Observer;

import jobs.Homeworks;
import jobs.JobList;

/**
 * Класс наблюдает за списком списков заданий <b>howmworks</b> для перечня
 * аккаунтов <b>accounts</b>. При изменении <b>howmworks</b> производит пересчёт
 * таймингов для <b>accounts</b>.
 */
public class Brain implements Observer {
	private List<IAccount> accounts;
	private Homeworks howmworks;

	public Brain(List<IAccount> accounts, Homeworks howmworks) {
		this.accounts = accounts;
		this.howmworks = howmworks;
		this.howmworks.registerObserver(this);
	}

	@Override
	public void perform(List<JobList> homeworks) {
		for (IAccount acc : accounts) {
			acc.RebuldAccTiming(homeworks);
		}
	}

	@Override
	public void update(List<MatrixAct> actionlist) {
		// not used
	}

}
