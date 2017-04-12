package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.function.Predicate;

import model.Regimen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dbaware.DbConnector;
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
	static Logger logger = LoggerFactory.getLogger(Timing.class);

	private long AccID = 0;
	private String timeZone;
	private Regimen regim;

	private ArrayList<JobAtom> innerTiming = new ArrayList<JobAtom>();
	private int index = 0;

	DbConnector dbConnector = new DbConnector();

	/**
	 * Constructor for testing
	 */
	public Timing(long AccID) {
		this.timeZone = "GMT+3";
		this.regim = new Regimen();
		this.AccID = AccID;
	}

	/**
	 * Constructor for ConcreteAcc
	 */
	public Timing(String timeZone, Regimen regim, long AccID) {
		this.timeZone = timeZone;
		this.regim = regim;
		this.AccID = AccID;
	}

	/**
	 * Считать остаток рабочего времени (за минусом обеда и ужина) Считать число
	 * оставшихся заданий. Равномерно распределить задания по оставшемуся
	 * времени. Приоритет заданий не учитывается Задания сортируются не по
	 * спискам, а по порядку следования в БД
	 */
	public void RebuildTiming(Homeworks homeworks, List<Integer> GroupIDs,
			long moment) {
		innerTiming.clear();
		logger.info("Timing  rebuilding");
		// Формируем плоский список заданий.
		homeworks.First();
		for (JobList jobList : homeworks) {
			jobList.First();
			for (JobAtom job : jobList) {
				// В список выбирать задания только относящиеся к перечню групп.
				if (GroupIDs.contains(job.group_id)) {
					JobAtom jobcopy = new JobAtom(job);
					innerTiming.add(jobcopy);
				}
			}
		}
		// Сортировка задач по ID из БД
		//Collections.sort(innerTiming, JobAtom.JobAtomComparatorByID);
		// Задачи будут подаваться в случайном порядке
		Collections.shuffle(innerTiming);
		 
		Calendar rightNow = Calendar.getInstance();
		rightNow.setTimeInMillis(moment);

		int hour = rightNow.get(Calendar.HOUR_OF_DAY);
		int minute = rightNow.get(Calendar.MINUTE);
		int starttime = hour * 60 + minute;
		int ActiveInterval = regim.ActiveH * 60;

		// Флаг - инкрементировать дату?
		boolean doIncDay = (starttime > regim.BedHour * 60);
		// Флаг - делать проверку заданий на завершение?
		boolean doCheckFinished = false;

		if (starttime > regim.WakeHour * 60 && starttime < regim.BedHour * 60) {
			ActiveInterval = regim.BedHour * 60 - starttime;
			doCheckFinished = true;
		} else
			starttime = regim.WakeHour * 60;

		if (doCheckFinished && this.AccID > 0)
			CheckInnerTiming(rightNow);

		Random random = new Random();
		Set<Integer> intset = new HashSet<Integer>();
		while (intset.size() < innerTiming.size()) {
			int h = random.nextInt(ActiveInterval) + starttime;
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
		if (doIncDay)
			date.add(Calendar.DAY_OF_MONTH, 1);

		for (int i = 0; i < myArray.length; i++) {
			int t = myArray[i];
			// logger.debug("Value: {}", String.valueOf(t));
			int h = t / 60;
			int m = t % 60;
			date.set(Calendar.HOUR_OF_DAY, h);
			date.set(Calendar.MINUTE, m);
			date.set(Calendar.SECOND, random.nextInt(58) + 1);
			innerTiming.get(i).timestamp = date.getTimeInMillis();
		}

		printTiming();
	}

	/**
	 * Функция но основании данных из БД удаляет из списка innerTiming задания,
	 * выполненные акком в день rightNow.
	 */
	private void CheckInnerTiming(Calendar rightNow) {
		List<Long> listIds = dbConnector.getExecutionInfo(this.AccID,
				rightNow.getTimeInMillis());
		/*
		 * Consumer<Long> stylelistIds = (Long p) ->
		 * System.out.println("id:"+p.longValue());
		 * listIds.forEach(stylelistIds);
		 * 
		 * Consumer<JobAtom> style = (JobAtom p) ->
		 * System.out.println("id:"+p.JobID +", at : "+p.timestamp);
		 * System.out.println("---Before check---"); innerTiming.forEach(style);
		 */

		Predicate<JobAtom> JobAtomPredicate = p -> listIds.contains(p.JobID);
		innerTiming.removeIf(JobAtomPredicate);

		/*
		 * System.out.println("---After check---"); innerTiming.forEach(style);
		 */
	}

	public void StoreTiming(long AccID) {
		dbConnector.StoreTiming(AccID, innerTiming);
	}

	public void printTiming() {
		for (JobAtom job : innerTiming) {
			// for (int i = 0; i < 2; i++) {
			// JobAtom job = innerTiming.get(i);
			logger.info("Job ID = {} at : {} {}", job.JobID,
					Constants.dfm.format(job.timestamp), job.timestamp);
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

	public void First() {
		this.index = 0;
	}
}
