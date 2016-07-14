package main;

import java.util.Date;

public class MatrixEntry {

	public static void main(String[] args) {
		Brain brain = new Brain();
		Engine engine = new Engine();

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
