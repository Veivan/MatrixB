package jobs;

import inrtfs.Observable;
import inrtfs.Observer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Класс - список списков заданий. 
 */
public class Homeworks implements Observable, Iterable<JobList>, Iterator<JobList> {

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
			return HomeworksList.get(index-1);
		}
		return null;
	}
	
	public void First() {
		this.index = 0;
	}

	// Сравнение двух расписаний
	public boolean CompareWith(Homeworks newsched) {
		//this.index = 0;
		return true;
	}

	// Сравнение двух расписаний
	public void ReplaceWith(Homeworks newsched) {
		HomeworksList.clear();	
		newsched.First();
		for (JobList jobList : newsched) {
			HomeworksList.add(jobList);
		}
	}
	
}
