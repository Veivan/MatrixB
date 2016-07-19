package jobs;

import java.util.ArrayList;
import java.util.List;

/** Класс - список списков заданий.
 * Списки хранятся в классе в порядке убывания приоритета.
*/
public class Homeworks {

	private int HowmworksCount = 0;
	
	public List<JobList> HomeworksList = new ArrayList<JobList>();
	
	// TODO сделать сортировку списков по убыванию приоритета после добавления списка 
	
	public int GetHowmworksCount()
	{
		return HowmworksCount;
	}

}
