package main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import network.TWClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import service.Constants;
import inrtfs.IAccount;
import inrtfs.Observable;
import inrtfs.Observer;
import jobs.JobAtom;

/**
 * Класс служит для чтения таймингов из перечня аккаунтов <b>accounts</b>. При
 * наличии задания в тайминге добавляет его во внутренний список. По окончании
 * обхода перечня аккаунтов передаёт внутренний список обсерверу.
 */
public class Engine implements Observable {
	private List<IAccount> accounts = new ArrayList<IAccount>();
	private List<MatrixAct> MatrixActList = new ArrayList<MatrixAct>();
	ExecutorService cachedPool = Executors.newCachedThreadPool();

	private List<Observer> observers;

	static Logger logger = LoggerFactory.getLogger(Engine.class);

	public Engine() {
		this.observers = new ArrayList<Observer>();
	}

	@Override
	public void registerObserver(Observer o) {
		observers.add(o);
	}

	@Override
	public void removeObserver(Observer o) {
		observers.remove(o);
	}

	@Override
	public void notifyObservers() {
		printMList();
		for (Observer observer : observers)
			observer.update(MatrixActList);
	}

	public void ReadTimings(long moment) {
		MatrixActList.clear();
		logger.debug("Engine Read Timings");
		for (IAccount acc : accounts) {
			JobAtom job = acc.getTimedJob(moment);
			if (job != null) {
				MatrixAct act = new MatrixAct(job, acc);
				MatrixActList.add(act);
			}
		}
		// if (!MatrixActList.isEmpty()) notifyObservers();
	}

	public void Execute() {
		for (MatrixAct act : MatrixActList) {
			cachedPool.submit(new TWClient(act));
		}
	}

	public void setAccounts(List<IAccount> accounts) {
		this.accounts = accounts;
		logger.debug("Engine Accounts setting");
	}

	// for debug only
	public void setUserAction(int user, String actiontxt) {
		MatrixActList.clear();
		MatrixAct act = new MatrixAct(0, "qq");
		MatrixActList.add(act);
		notifyObservers();
	}

	private void printMList() {
		for (MatrixAct act : MatrixActList) {
			logger.info("Act at : {}",
					Constants.dfm.format(act.getJob().timestamp));
		}
	}
}
