package service;

import java.util.Comparator;

import jobs.JobList;

public class JobListComparator implements Comparator<JobList> {

	@Override
	public int compare(JobList list1, JobList list2) {
		int p1 = list1.getPriority();
		int p2 = list2.getPriority();
		return p2 > p1 ? 1 : p1 == p2 ? 0 : -1;
	}

}
