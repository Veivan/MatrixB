package jobs;

import inrtfs.Observable;
import inrtfs.Observer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import service.Constants;
import service.JobListComparator;

/**
 * Класс - список списков заданий.
 */
public class Homeworks implements Observable, Iterable<JobList>,
		Iterator<JobList> {

	static Logger logger = LoggerFactory.getLogger(Homeworks.class);

	private List<Observer> observers = new ArrayList<Observer>();

	private List<JobList> HomeworksList = new ArrayList<JobList>();
	private int index = 0;

	private Date datevalid = new Date(System.currentTimeMillis());

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

	public void AddJob(JobAtom job) {
		JobList targetList = null;
		// Searching target list
		for (JobList jobList : HomeworksList) {
			if (job.Type == jobList.getType()) {
				targetList = jobList;
				break;
			}
		}
		// If not found then make new one
		if (targetList == null) {
			JobListComparator comparator = new JobListComparator();
			targetList = new JobList(job.Type);
			HomeworksList.add(targetList);
			// Добавлять в класс надо в порядке приоритета
			Collections.sort(HomeworksList, comparator);
		}
		targetList.AddJob(job);
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

	/**
	 * Сравнение двух расписаний
	 */
	public boolean IsDifferent(Homeworks newsched) {
		// Сравнение по дате валидности
		if (!IsDatesEqual(this.getDatevalid(), newsched.getDatevalid()))
		{
			logger.debug("IsDifferent : Dates not Equal");
			return true;
		}

		// Сравнение по числу списков
		if (this.JobListsCount() != newsched.JobListsCount())
		{
			logger.debug("IsDifferent : JobListsCount not Equal");
			return true;
		}

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

	/**
	 * Замена старого расписания новым
	 */
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

	/**
	 * Функция ищет в старом расписании работу с таким же ID Возвращает true,
	 * если работа найдена и завершена иначе возвращает false
	 */
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

	public Date getDatevalid() {
		return datevalid;
	}

	/**
	 * Сравнение двух дат на одинаковость (без времени)
	 */
	private boolean IsDatesEqual(Date date1, Date date2) {
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c1.setTime(date1);
		c2.setTime(date2);
		boolean res = (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR))
				&& (c1.get(Calendar.DAY_OF_YEAR) == c2
						.get(Calendar.DAY_OF_YEAR));
		return res;
	}

}
