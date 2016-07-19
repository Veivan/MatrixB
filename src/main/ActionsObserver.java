package main;

import inrtfs.Observer;

public class ActionsObserver implements Observer {

	private Engine engine;
	private String actiontxt;

	public ActionsObserver(Engine engine) {
		this.engine = engine;
		this.engine.registerObserver(this);
	}

	@Override
	public void update(String actiontxt) {
		this.actiontxt = actiontxt;
		display();
	}

	@Override
	public void update() {
		// not used		
	}

	public void display() {
		System.out.printf("Action: %s \n", actiontxt);
	}

}
