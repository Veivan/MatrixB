package service;

import java.util.Comparator;

import twitter4j.Status;

public class StatusComparator {

	public static Comparator<Status> StatusComparatorByRT = new Comparator<Status>() {
		@Override
		public int compare(Status s1, Status s2) {
			int p1 = s1.getRetweetCount();
			int p2 = s2.getRetweetCount();
			return p2 > p1 ? 1 : p1 == p2 ? 0 : -1;
		}
	};

	public static Comparator<Status> StatusComparatorByFv = new Comparator<Status>() {
		@Override
		public int compare(Status s1, Status s2) {
			int p1 = s1.getFavoriteCount();
			int p2 = s2.getFavoriteCount();
			return p2 > p1 ? 1 : p1 == p2 ? 0 : -1;
		}
	};
}
