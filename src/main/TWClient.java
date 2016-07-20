package main;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import service.ReaderIni;
import inrtfs.IEngine;

public class TWClient implements IEngine {

	private String str;

	public String getData() {
		return str;
	}

	public void setData(String str) {
		this.str = str;
	}

	public String addData(String input) {
		String add = str.concat(input);
		this.setData(add);
		return str;
	}

	@Override
	public boolean DoAuth() {
		ReaderIni keys = new ReaderIni();
		OAuthConsumer consumer = new CommonsHttpOAuthConsumer(keys.cConsumerKey,
				keys.cConsumerSecret);
		consumer.setTokenWithSecret(keys.cAccessToken, keys.cAccessSecret);
		return false;
	}

}
