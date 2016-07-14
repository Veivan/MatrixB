package main;

import java.util.ArrayList;
import java.util.List;

import inrtfs.IAccount;
import inrtfs.Observable;
import inrtfs.Observer;

public class Engine implements Observable {
	private List<IAccount> accounts;

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

	public void update(MatrixAct act) {
		if (act != null) {
			this.actiontxt = act.ActionTXT;
			ConcreteAcc acc = (ConcreteAcc) accounts.get(0);//act.AccID
			acc.Tweet("ww");
			notifyObservers();
		}
	}

	public void setUserAction(int user, String actiontxt) {
		this.actiontxt = actiontxt;
		notifyObservers();
	}
}
