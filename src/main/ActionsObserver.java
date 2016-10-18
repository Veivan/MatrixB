package main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jobs.Homeworks;
import inrtfs.Observer;

/**
 * Класс наблюдает за классом <b>Engine</b>.
 * При получении списка заданий на выполнение запускает каждое задание в отдельном потоке.
 * Для управления потоками импользует пул потоков.
 */
public class ActionsObserver implements Observer {

	static Logger logger = LoggerFactory.getLogger(ActionsObserver.class);

	private Engine engine;

	List<MatrixAct> MatrixActList = new ArrayList<MatrixAct>();
	ExecutorService cachedPool = Executors.newCachedThreadPool();

	public ActionsObserver(Engine engine) {
		this.engine = engine;
		this.engine.registerObserver(this);
	}

	@Override
	public void update(List<MatrixAct> actionlist) {
		MatrixActList.addAll(actionlist);
		for (MatrixAct act : actionlist) {
			logger.debug("ActionsObserver execute act");
			execute(act);
		}
	}

	public void execute(MatrixAct act) {
		cachedPool.submit(new TWClient(act));
	}

	@Override
	public void perform(Homeworks homeworks) {
		// not used
	}

}
