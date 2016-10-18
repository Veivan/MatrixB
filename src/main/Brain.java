package main;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import inrtfs.IAccount;
import inrtfs.Observer;
import jobs.Homeworks;

/**
 * Класс наблюдает за списком списков заданий <b>howmworks</b> для перечня
 * аккаунтов <b>accounts</b>. При изменении <b>howmworks</b> производит пересчёт
 * таймингов для <b>accounts</b>.
 */
public class Brain implements Observer {
	private List<IAccount> accounts = new ArrayList<IAccount>();
	private Homeworks howmworks;

	static Logger logger = LoggerFactory.getLogger(Brain.class);

	public Brain(Homeworks howmworks) {
		this.howmworks = howmworks;
		this.howmworks.registerObserver(this);
	}

	public void setAccounts(List<IAccount> accounts) {
		logger.debug("Brain Accounts setting");
		this.accounts = accounts;
	}
	
	@Override
	public void perform(Homeworks homeworks) {
		for (IAccount acc : accounts) {
			acc.RebuldAccTiming(homeworks);
		}
	}

	@Override
	public void update(List<MatrixAct> actionlist) {
		// not used
	}

}
