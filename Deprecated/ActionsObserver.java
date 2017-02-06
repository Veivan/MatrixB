package main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import model.MatrixAct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import service.Constants;
import jobs.Homeworks;
import inrtfs.Observer;

/**
 * Класс наблюдает за классом <b>Engine</b>. При получении списка заданий на
 * выполнение запускает каждое задание в отдельном потоке. Для управления
 * потоками импользует пул потоков.
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
		for (MatrixAct act : MatrixActList) {
			logger.info("Action Act at : {}", Constants.dfm.format(act.getJob().timestamp));
		}
		for (MatrixAct act : actionlist) {
			logger.debug("ActionsObserver execute act");
			logger.info("Job at : {} {}",				Constants.dfm.format(act.getJob().timestamp),				act.getJob().timestamp);
			execute(act);
		}
		MatrixActList.clear();
	}

	public void execute(MatrixAct act) {
		//logger.info("Job at : {} {}",				Constants.dfm.format(act.getJob().timestamp),				act.getJob().timestamp);
		cachedPool.submit(new TWClient(act));
	}

	@Override
	public void perform(Homeworks homeworks) {
		// not used
	}

}
