package jobs;

import inrtfs.Observable;
import inrtfs.Observer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import service.Constants;

/**
 * Класс - список списков заданий.
 */
public class Homeworks implements Observable, Iterable<JobList>,
		Iterator<JobList> {

	static Logger logger = LoggerFactory.getLogger(Homeworks.class);

	private List<Observer> observers = new ArrayList<Observer>();

	private List<JobList> HomeworksList = new ArrayList<JobList>();
	private int index = 0;

	@Override
	public void registerObserver(Observer o) {
		observers.add(o);
	}

	@Override
	public void removeObserver(Observer o) {
		observers.remove(o);
	}

	@Override
	public void notifyObservers() {
		for (Observer observer : observers)
			observer.perform(this);
	}

	public void AddList(JobList list) {
		HomeworksList.add(list);
	}

	@Override
	public Iterator<JobList> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		return (index < HomeworksList.size());
	}

	@Override
	public JobList next() {
		index++;
		if (index >= 0 && index <= HomeworksList.size()) {
			return HomeworksList.get(index - 1);
		}
		return null;
	}

	public void First() {
		this.index = 0;
	}

	// Сравнение двух расписаний
	public boolean IsDifferent(Homeworks newsched) {
		// Сравнение по числу списков
		if (this.JobListsCount() != newsched.JobListsCount())
			return true;

		// Сравнение по типам списков, числу элементов и по эдементам списков
		this.First();
		for (JobList jobList : this) {
			Constants.JobType thisType = jobList.getType();
			int thissize = jobList.getSize();
			String thishash = jobList.getHash();

			boolean found = false;
			newsched.First();
			for (JobList jobListnew : newsched) {
				if (thisType == jobListnew.getType()) {
					found = true;
					// Размер совпадает?
					if (thissize != jobListnew.getSize())
						return true;
					// Хэш совпадает?
					String newhash = jobListnew.getHash();
					logger.debug("thishash : {}, newhash : {}, equal {}",
							thishash, newhash, thishash.equals(newhash));
					if (!thishash.equals(newhash))
						return true;
					break;
				}
			}
			// Список нашёлся?
			if (!found)
				return true;
		}

		return false;
	}

	// Замена старого расписания новым
	public void ReplaceWith(Homeworks newsched) {
		// Помечаем в новом расписании уже выполненные задания
		newsched.First();
		for (JobList jobList : newsched) {
			jobList.First();
			for (JobAtom job : jobList) {
				job.IsFinished = AlreadyFinished(job);
			}
		}
		// Замена старого расписания новым
		HomeworksList.clear();
		newsched.First();
		for (JobList jobList : newsched) {
			HomeworksList.add(jobList);
		}
	}

	// Функция ищет в старом расписании работу с таким же ID
	// Возвращает true, если работа найдена и завершена
	// иначе возвращает false
	private boolean AlreadyFinished(JobAtom ajob) {
		for (JobList jobList : HomeworksList) {
			if (jobList.getType() == ajob.Type) {
				jobList.First();
				for (JobAtom job : jobList) {
					if (job.JobID == ajob.JobID && job.IsFinished) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public int JobListsCount() {
		return this.HomeworksList.size();
	}
}
