package service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReaderIni {
	public String cAccessToken = "2936887497-j19YUO9hyhwNREQyfABs10wdt2XlfcXwuCVFYj0";
	public String cAccessSecret = "w0JscngvMK7FwgYvDreZjGkkULl5hNizV4oTJlRas5cRq";
	public String cConsumerKey = "YEgJkngnkDR7Ql3Uz5ZKkYgBU";
	public String cConsumerSecret = "CsCz7WmytpUoWqIUp9qQPRS99kMk4w9QoSH3GcStnpPc4mf1Ai";

	static Logger logger = LoggerFactory.getLogger(ReaderIni.class);

	public ReaderIni(){
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(new File("twclient.ini")));

			cAccessToken = props.getProperty("AccessToken", cAccessToken);
			cAccessSecret = props.getProperty("AccessSecret", cAccessSecret);
			cConsumerKey = props.getProperty("ConsumerKey", cConsumerKey);
			cConsumerSecret = props.getProperty("ConsumerSecret", cConsumerSecret);
		} catch (FileNotFoundException e) {
			logger.error("ReaderIni FileNotFoundException : ", e);
			logger.debug("ReaderIni FileNotFoundException : ", e);
		} catch (IOException e) {
			logger.error("ReaderIni IOException : ", e);
			logger.debug("ReaderIni IOException : ", e);
		}
	}

}
