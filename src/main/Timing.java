package main;

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

import settings.Constants;
import jobs.JobAtom;
import jobs.JobList;

/**
 * Класс описывает расписание выполнения задний.
 *
 */
public class Timing implements Iterator<JobAtom>{
	private String timeZone;
	private Regimen regim ;
    private int index = -1; // Points to innerTiming position

	private ArrayList<JobAtom> innerTiming = new ArrayList<JobAtom>();
	
	public Timing(String timeZone, Regimen regim)
	{
		this.timeZone = timeZone;
		this.regim = regim;
	}

	// Считать остаток рабочего времени (за минусом обеда)
	// Считать число оставшихся простых твитов и число заданий.
	// Равномерно распределить экшэны по оставшемуся времени.

	public void RebuildTiming(List<JobList> HomeworksList) {
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

	public JobAtom First() {
		if (innerTiming.size() > 0)
		{
			index = 0;
    		return innerTiming.get(index);			
		}
		return null;
	}

	@Override
	public boolean hasNext() {
		return (index > -1 || index < innerTiming.size());
	}

	@Override
	public JobAtom next() {
		index++;		
        if (index < innerTiming.size())
        {
    		return innerTiming.get(index);
        }
		return null;
	}

}
