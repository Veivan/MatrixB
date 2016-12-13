package inrtfs;

import java.util.List;

import jobs.Homeworks;
import model.MatrixAct;

public interface Observer {

	/** Используется в обсервере ActionsObserver - выполняет запуск заданий */
	void update(List<MatrixAct> actionlist);
    
	/** Используется в обсервере Brain - выполняет переформирование таймингов при изменении списков заданий*/
	void perform(Homeworks homeworks);
    
}
