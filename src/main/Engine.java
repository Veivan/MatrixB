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
	private List<IAccount> accounts = new ArrayList<IAccount>();
	private List<MatrixAct> MatrixActList = new ArrayList<MatrixAct>();

	private List<Observer> observers;

	public Engine() {
		this.observers = new ArrayList<Observer>();
	}

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
			observer.update(MatrixActList);
	}

	public void ReadTimings(long moment) {
		MatrixActList.clear();
		for (IAccount acc : accounts) {
			JobAtom job = acc.getTimedJob(moment);
			if (job != null) {
				MatrixAct act = new MatrixAct(job, acc);
				MatrixActList.add(act);
				notifyObservers();
			}
		}
	}

	public void setAccounts(List<IAccount> accounts) {
		this.accounts.clear();
		this.accounts.addAll(accounts);
	}
	
	public void setUserAction(int user, String actiontxt) {
		MatrixActList.clear();
		MatrixAct act = new MatrixAct(0, "qq");
		MatrixActList.add(act);
		notifyObservers();
	}
}
