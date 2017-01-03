package service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import model.ElementCredentials;

public class Utils {

	public static ElementCredentials ReadINI() throws Exception {
		ElementCredentials creds;
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(new File("example.ini")));
			String CONSUMER_KEY = props.getProperty("CONSUMER_KEY");
			String CONSUMER_SECRET = props.getProperty("CONSUMER_SECRET");
			String USER = props.getProperty("USER");
			String USER_PASS = props.getProperty("USER_PASS");
			String ACCESS_TOKEN = props.getProperty("ACCESS_TOKEN");
			String ACCESS_TOKEN_SECRET = props
					.getProperty("ACCESS_TOKEN_SECRET");

			creds = new ElementCredentials(CONSUMER_KEY, CONSUMER_SECRET, USER,
					USER_PASS, ACCESS_TOKEN, ACCESS_TOKEN_SECRET);
		} catch (Exception e) {
			throw new Exception("ERROR ReaderIni : ", e);
		}
		return creds;
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
					conn.getInputStream()));
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

}
