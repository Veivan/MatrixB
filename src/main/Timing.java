package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;

import service.Constants;
import jobs.Homeworks;
import jobs.JobAtom;
import jobs.JobList;

/**
 * Класс описывает расписание выполнения задний. Реализует интерфейс IAggregate
 * для возможности обхода внутреннего списка с помощью итератора.
 */
public class Timing implements Iterable<JobAtom>, Iterator<JobAtom> {
	private static final int delay = 3; // мин

	private String timeZone;
	private Regimen regim;

	private ArrayList<JobAtom> innerTiming = new ArrayList<JobAtom>();
	private int index = 0;

	public Timing(String timeZone, Regimen regim) {
		this.timeZone = timeZone;
		this.regim = regim;
	}

	public Timing() {
		this.timeZone = "GMT+3";
		this.regim = new Regimen();
	}

	// Считать остаток рабочего времени (за минусом обеда и ужина)
	// Считать число оставшихся заданий.
	// Равномерно распределить задания по оставшемуся времени.
	// Приоритет заданий не учитывается
	public void RebuildTiming(Homeworks homeworks) {
		// Формируем плоский список заданий
		for (JobList jobList : homeworks) {
			for (JobAtom job : jobList) {
				innerTiming.add(job);
			}
		}

		Random random = new Random();
		Set<Integer> intset = new HashSet<Integer>();
		while (intset.size() < innerTiming.size()) {
			int h = random.nextInt(regim.ActiveH * 60) + regim.WakeHour * 60;
			if ((h > regim.Lounch * 60 && h < (regim.Lounch + 1) * 60)
					|| (h > regim.Supper * 60 && h < (regim.Supper + 1) * 60))
				continue;
			intset.add(h);
		}
		// Сортировка по возрастанию времени
		Integer[] myArray = {};
		myArray = intset.toArray(new Integer[intset.size()]);
		Arrays.sort(myArray);
		// Проверка на наличие задержки между моментами времени
		for (int i = 0; i < myArray.length - 1; i++) {
			if (myArray[i + 1] - myArray[i] <= delay)
				myArray[i + 1] += delay;
		}

		GregorianCalendar date = new GregorianCalendar(
				TimeZone.getTimeZone(timeZone));
		for (int i = 0; i < myArray.length; i++) {
			int t = myArray[i];
			//System.out.printf("Value: %s \n", String.valueOf(t));
			int h = t / 60;
			int m = t % 60;
			date.set(Calendar.HOUR_OF_DAY, h);
			date.set(Calendar.MINUTE, m);
			date.set(Calendar.SECOND, random.nextInt(58) + 1);
			innerTiming.get(i).timestamp = date.getTimeInMillis();
		}
	}

	public void printTiming() {
		for (JobAtom job : innerTiming) {
			Date d = new Date(job.timestamp);
			System.out.printf("%s \n", Constants.dfm.format(d));
		}
	}

	@Override
	public boolean hasNext() {
		return (index < innerTiming.size());
	}

	@Override
	public JobAtom next() {
		index++;
		if (index >= 0 && index <= innerTiming.size()) {
			return innerTiming.get(index - 1);
		}
		return null;
	}

	@Override
	public Iterator<JobAtom> iterator() {
		return this;
	}
}