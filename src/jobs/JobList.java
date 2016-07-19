package jobs;

import java.util.ArrayList;
import java.util.List;

import settings.Constants;

/** Класс служит для хранения списка заданий.
*/
public class JobList {
    /** Свойство - приоритет */
	private int Priority;

	/** Свойство - тип */
	private Constants.JobType Type;

	private List<JobAtom> JobAtomList = new ArrayList<JobAtom>();

	 /** Создает новый объект для хранения списка заданий
     * @param priority - приоритет
     * @param type - тип заданий
    */	public JobList(int priority, Constants.JobType type) {
		this.Priority = priority;
		this.Type = type;
	}

	public int getPriority() {
		return Priority;
	}

	public Constants.JobType getType() {
		return Type;
	}

	public void AddJob(JobAtom job) {
		JobAtomList.add(job);		
	}

}
