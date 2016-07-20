package main;

import inrtfs.IAggregate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;

import service.Constants;
import service.InnerListIteratior;
import jobs.JobAtom;
import jobs.JobList;

/**
 * Класс описывает расписание выполнения задний.
 *
 */
public class Timing implements IAggregate{
	private String timeZone;
	private Regimen regim ;
    private int index = -1; // Points to innerTiming position

	private ArrayList<JobAtom> innerTiming = new ArrayList<JobAtom>();
	
	public Timing(String timeZone, Regimen regim)
	{
		this.timeZone = timeZone;
		this.regim = regim;
	}

	// Считать остаток рабочего времени (за минусом обеда и ужина)
	// Считать число оставшихся заданий.
	// Равномерно распределить задания по оставшемуся времени.
	// Приоритет заданий не учитывается
	public void RebuildTiming(List<JobList> HomeworksList) {
		// Формируем плоский список заданий
		for (JobList jobList : HomeworksList) {
			InnerListIteratior iterator = new InnerListIteratior(jobList);
			JobAtom job = (JobAtom)iterator.First();
			do {
				if (job != null) {
					System.out.printf("job %s \n", job.JobID);
				}
				job = (JobAtom)iterator.next();			
			} while (iterator.hasNext());		
		}
		
		Random random = new Random();
		Set<Integer> intset = new HashSet<Integer>();
		while (intset.size() < 6) {
			int h = random.nextInt(regim.ActiveH) + regim.WakeHour;
			if (h == regim.Lounch || h == regim.Supper)
				continue;
			intset.add(h);
		}
		Integer[] myArray = {};
		myArray = intset.toArray(new Integer[intset.size()]);
		Arrays.sort(myArray);
		// Randomize time inside the hour
		GregorianCalendar date = new GregorianCalendar(
				TimeZone.getTimeZone(timeZone));
		for (int i = 0; i < myArray.length; i++) {
			int h = myArray[i];
			// System.out.printf("Value: %s \n", String.valueOf(h));
			int m = random.nextInt(58) + 1;
			date.set(Calendar.HOUR_OF_DAY, h);
			date.set(Calendar.MINUTE, m);
			date.set(Calendar.SECOND, random.nextInt(58) + 1);
//			innerTiming.add(date.getTimeInMillis());
		}
	}

	public void printTiming() {
		for (JobAtom job : innerTiming) {
			Date d = new Date(job.timestamp);
			System.out.printf("%s \n", Constants.dfm.format(d));
		}
	}

	@Override
	public int Count() {
		return innerTiming.size();
	}

	@Override
	public Object Element(int index) {
        if (index >= 0 && index < innerTiming.size())
        {
    		return innerTiming.get(index);
        }
		return null;
	}
}
