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

	// Константы для SimpleVisitor
	public static final int CONNECTION_TIMEOUT_MS = 20;
	public static final int CONNECTION_REQUEST_TIMEOUT_MS = 20;
	public static final int SOCKET_TIMEOUT_MS = 20;
	
	public static final String USER_AGENT = "Mozilla/5.0";
	public static final String DEFAULT_OAUTH_CALLBACK = "http://www.ya.ru"; 

	public static final String URL_RANDOM_SERVLET = "http://localhost:8080/HelpChildren/getrandomid"; 
	//public static final String URL_RANDOM_SERVLET = "http://helpchildren.online/getrandomid";
	
	// Константы для проверки прокси
	public static final String testLink = "http://google.ru";
	public static final int prxchcktimeout = 5000; // время ожидания ответа (в милисекундах)


	public static final boolean IsDebugProxy = false;
	public static final boolean IsDebugCreds = false;

	private Constants() {
	} // make clacc abstract
}
