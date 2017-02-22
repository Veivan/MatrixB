package service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;

import microsoft.sql.DateTimeOffset;
import model.ElementCredentials;

public class Utils {
	private final static String inifile = "matrixb.ini";

	public static ElementCredentials ReadINI() throws Exception {
		ElementCredentials creds;
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(new File(inifile)));
			String CONSUMER_KEY = props.getProperty("CONSUMER_KEY");
			String CONSUMER_SECRET = props.getProperty("CONSUMER_SECRET");
			String USER = props.getProperty("USER");
			String USER_PASS = props.getProperty("USER_PASS");
			String ACCESS_TOKEN = props.getProperty("ACCESS_TOKEN");
			String ACCESS_TOKEN_SECRET = props
					.getProperty("ACCESS_TOKEN_SECRET");

			creds = new ElementCredentials(CONSUMER_KEY, CONSUMER_SECRET, USER,
					USER_PASS, ACCESS_TOKEN, ACCESS_TOKEN_SECRET, 0, 0);
		} catch (Exception e) {
			throw new Exception("ERROR ReaderIni : ", e);
		}
		return creds;
	}

	public static String ReadConnStrINI() throws Exception {
		String connstring = null;
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(new File(inifile)));
			connstring = props.getProperty("CONNECT_STRING");
		} catch (Exception e) {
			throw new Exception("ERROR ReaderIni : ", e);
		}
		return connstring;
	}

	/**
	 * Чтение содержимого страницы
	 * 
	 * @return String
	 */
	public static String GetPageContent(String url_string) throws Exception {
		StringBuffer result = new StringBuffer();
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) new URL(url_string).openConnection();

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "UTF-8"));
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			return result.toString();
		} finally {
			if (conn != null)
				conn.disconnect();
		}
	}

	public static boolean empty(final String s) {
		// Null-safe, short-circuit evaluation.
		return s == null || s.trim().isEmpty();
	}

	public static byte[] readBytesFromFile(String filePath) {
		FileInputStream fileInputStream = null;
		byte[] bytesArray = null;
		try {
			File file = new File(filePath);
			bytesArray = new byte[(int) file.length()];
			// read file into bytes[]
			fileInputStream = new FileInputStream(file);
			fileInputStream.read(bytesArray);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return bytesArray;
	}

	/**
	 * Get a diff between two dates
	 * 
	 * @param date1
	 *            the oldest date
	 * @param date2
	 *            the newest date
	 * @param timeUnit
	 *            the unit in which you want the diff
	 * @return the diff value, in the provided unit
	 */
	public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
		long diffInMillies = date2.getTime() - date1.getTime();
		return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
	}

	public static long getDateDiff(long date1, long date2, TimeUnit timeUnit) {
		long diffInMillies = date2 - date1;
		return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
	}

	/**
	 * Преобразование java.util.Date в DateTimeOffset
	 * 
	 * @return DateTimeOffset
	 */
	public static DateTimeOffset getDateTimeOffset(Date date) {
		DateTime jDate = new DateTime(date); 		
		TimeZone tz = TimeZone.getTimeZone(jDate.getZone().getID());
		Calendar calendar = Calendar.getInstance(); 
		calendar.setTimeZone(tz);
		Timestamp ts = new Timestamp(jDate.getMillis());
		DateTimeOffset dto = DateTimeOffset.valueOf(ts, calendar);
		return dto;
	}


}
