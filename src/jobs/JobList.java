package jobs;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import service.Constants;

/** Класс служит для хранения списка заданий. */
public class JobList implements Iterable<JobAtom>, Iterator<JobAtom> {
	/** Свойство - приоритет */
	private int Priority;

	/** Свойство - тип */
	private Constants.JobType Type;

	private List<JobAtom> JobAtomList = new ArrayList<JobAtom>();
	private int index = 0;

	/**
	 * Создает новый объект для хранения списка заданий
	 * 
	 * @param priority
	 *            - приоритет
	 * @param type
	 *            - тип заданий
	 */
	public JobList(Constants.JobType type) {
		this.Priority = Constants.GetPriority(type);
		this.Type = type;
	}

	public int getPriority() {
		return Priority;
	}

	public Constants.JobType getType() {
		return Type;
	}

	public int getSize() {
		return JobAtomList.size();
	}

	public String getHash() {
		String hash = "";
		for (JobAtom jobAtom : JobAtomList) {
			hash += "" + jobAtom.JobID + "_"; 	
		} 
		return hash;
	}

	public void AddJob(JobAtom job) {
		JobAtomList.add(job);
	}

	@Override
	public boolean hasNext() {
		return (index < JobAtomList.size());
	}

	@Override
	public JobAtom next() {
		index++;
		if (index >= 0 && index <= JobAtomList.size()) {
			return JobAtomList.get(index - 1);
		}
		return null;
	}

	@Override
	public Iterator<JobAtom> iterator() {
		return this;
	}
	
	public void First() {
		this.index = 0;
	}

	public static Comparator<JobList> JobListComparatorByPriority = new Comparator<JobList>() {
		@Override
		public int compare(JobList list1, JobList list2) {
			int p1 = list1.getPriority();
			int p2 = list2.getPriority();
			return p2 > p1 ? 1 : p1 == p2 ? 0 : -1;
		}
	};

}
