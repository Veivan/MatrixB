package service;

import java.io.File;
import java.io.FileInputStream;
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

			creds = new ElementCredentials(CONSUMER_KEY, CONSUMER_SECRET,
					USER, USER_PASS, ACCESS_TOKEN, ACCESS_TOKEN_SECRET);
		} catch (Exception e) {
			throw new Exception("ERROR ReaderIni : ", e);
		}
		return creds;
	}

}
