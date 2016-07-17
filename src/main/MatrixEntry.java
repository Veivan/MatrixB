package main;

import inrtfs.IAccount;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MatrixEntry {

	public static void main(String[] args) {
		List<IAccount> accounts = new ArrayList<IAccount>();
		ConcreteAcc acc1 = new ConcreteAcc(1);
		//ConcreteAcc acc2 = new ConcreteAcc(2);
		accounts.add(acc1);
		//accounts.add(acc2);

		Homeworks howmworks = new Homeworks();
		Brain brain = new Brain(accounts, howmworks);
		Engine engine = new Engine(accounts);

		@SuppressWarnings("unused")
		ActionsObserver currentDisplay = new ActionsObserver(engine);
		engine.setUserAction(1, "act1");
		engine.setUserAction(2, "act2");
		engine.setUserAction(1, "act3");
		try {
			while (true) {
				Date ndate = new Date();
				System.out.println(ndate);
				engine.update(brain.getAction());
				Thread.sleep(5 * 1000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
