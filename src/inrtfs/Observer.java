package inrtfs;

public interface Observer {

	/** Используется в обсервере ActionsObserver */
	void update(String actiontxt);
    
	/** Используется в обсервере Brain */
    void update();
}
