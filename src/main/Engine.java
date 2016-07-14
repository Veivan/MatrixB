package main;

import java.util.ArrayList;
import java.util.List;

import inrtfs.Observable;
import inrtfs.Observer;

public class Engine implements Observable {

    private List<Observer> observers;
    private String actiontxt;

    public Engine() {
        observers = new ArrayList<Observer>();
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

	  public void update(MatrixAct act )
	  {
	        this.actiontxt = act.ActionTXT;
	        notifyObservers();
	  }

	  public void setUserAction(int user, String actiontxt) {
        this.actiontxt = actiontxt;
        notifyObservers();
    }
}
