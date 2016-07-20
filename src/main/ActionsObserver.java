package main;

import java.util.ArrayList;
import java.util.List;

import jobs.Homeworks;
import inrtfs.Observer;

public class ActionsObserver implements Observer {

	private Engine engine;

	List<MatrixAct> MatrixActList = new ArrayList<MatrixAct>();

	public ActionsObserver(Engine engine) {
		this.engine = engine;
		this.engine.registerObserver(this);
	}

	@Override
	public void update(List<MatrixAct> actionlist) {
		MatrixActList.addAll(actionlist);
		// TODO Здесь надо в потоках запускать выполнение каждого MatrixAct
		for (MatrixAct act : actionlist) {
			execute(act);
		}
	}

	public void execute(MatrixAct act) {
		System.out.printf("Action: %s \n", act.ActionTXT);
		/*
		 * ConcreteAcc acc = (ConcreteAcc) accounts.get(0);// act.AccID
		 * acc.Tweet("ww");
		 */
	}

	@Override
	public void perform(Homeworks homeworks) {
		// not used
	}

}
