package jobs;

import inrtfs.Observable;
import inrtfs.Observer;

import java.util.ArrayList;
import java.util.List;

/** Класс - список списков заданий.
 * Списки хранятся в классе в порядке убывания приоритета.
*/
public class Homeworks implements Observable{

	private List<Observer> observers;

	private int HowmworksCount = 0;
	
	public List<JobList> HomeworksList = new ArrayList<JobList>();
	
	// TODO сделать сортировку списков по убыванию приоритета после добавления списка 
	
	public int GetHowmworksCount()
	{
		return HowmworksCount;
	}

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
			observer.update();
	}

}
