package main;

import java.util.ArrayList;
import java.util.List;

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
	private List<IAccount> accounts;

	public List<MatrixAct> MatrixActList = new ArrayList<MatrixAct>();

	private List<Observer> observers;
	private String actiontxt;

	public Engine(List<IAccount> accounts) {
		this.accounts = accounts;
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
		for (Observer observer : observers)
			observer.update(actiontxt);
	}

	public void ReadTimings() {
		MatrixActList.clear();
		for (IAccount acc : accounts) {
			long moment = System.currentTimeMillis();
			JobAtom job = acc.getTimedJob(moment);
			if (job != null) {
				MatrixAct act = new MatrixAct(job, acc);
			}
		}
	}

	public void update(MatrixAct act) {
		if (act != null) {
			this.actiontxt = act.ActionTXT;
			ConcreteAcc acc = (ConcreteAcc) accounts.get(0);// act.AccID
			acc.Tweet("ww");
			notifyObservers();
		}
	}

	public void setUserAction(int user, String actiontxt) {
		this.actiontxt = actiontxt;
		notifyObservers();
	}
}
