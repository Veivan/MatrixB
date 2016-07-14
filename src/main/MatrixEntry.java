package main;

public class MatrixEntry {

	public static void main(String[] args) {
		Engine engine = new Engine();

        @SuppressWarnings("unused")
		ActionsObserver currentDisplay = new ActionsObserver(engine);
        engine.setUserAction(1, "act1");
        engine.setUserAction(2, "act2");
        engine.setUserAction(1, "act3");
	}

}
