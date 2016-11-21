package service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Constants declarations.
 */
public final class Constants {

	public enum JobType {
		SETAVA, SETBACKGROUND, TWIT, RETWIT, LIKE, REPLAY, DIRECT, FOLLOW, UNFOLLOW, VISIT
	}

	// Приоритеты списков задач. Перечислены по порядку членов перечисления
	// JobType
	private static int[] priority = { 20, 10, 50, 90, 80, 70, 60, 30, 40, 85 };

	public static int GetPriority(JobType jobType) {
		return priority[jobType.ordinal()];
	}

	public static DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public enum ProxyType {
		HTTP, SOCKS
	}

	public enum RequestType {
		GET, POST
	}

	private Constants() {
	} // make clacc abstract
}
