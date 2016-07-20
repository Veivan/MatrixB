package jobs;

import inrtfs.IAggregate;
import inrtfs.Observable;
import inrtfs.Observer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import service.InnerListIteratior;

/**
 * Класс - список списков заданий. Списки хранятся в классе в порядке убывания
 * приоритета.
 */
public class Homeworks implements Observable, Iterable<JobList>, Iterator {

	private List<Observer> observers = new ArrayList<Observer>();

	private List<JobList> HomeworksList = new ArrayList<JobList>();
	private int index = 0;

	// TODO сделать сортировку списков по убыванию приоритета после добавления
	// списка

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
			observer.perform(HomeworksList);
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
	public Object next() {
		index++;
		if (index >= 0 && index <= HomeworksList.size()) {
			return HomeworksList.get(index-1);
		}
		return null;
	}

	/*
	 * @Override public int Count() { return HomeworksList.size(); }
	 * 
	 * @Override public Object Element(int index) { if (index >= 0 && index <
	 * HomeworksList.size()) { return HomeworksList.get(index); } return null; }
	 */

}
