package main;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import service.ReaderIni;
import inrtfs.ItwClient;

public class TWClient implements ItwClient {

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
	public boolean Auth() {
		ReaderIni keys = new ReaderIni();
		OAuthConsumer consumer = new CommonsHttpOAuthConsumer(keys.cConsumerKey,
				keys.cConsumerSecret);
		consumer.setTokenWithSecret(keys.cAccessToken, keys.cAccessSecret);
		return false;
	}

	@Override
	public void SetAva() {
		// TODO Auto-generated method stub
	}

	@Override
	public void SetBackgrnd() {
		// TODO Auto-generated method stub
	}

	@Override
	public void Twit(String mess) {
		// TODO Auto-generated method stub
	}

	@Override
	public void ReTwit(int twID, String mess) {
		// TODO Auto-generated method stub
	}

	@Override
	public void Like(int twID) {
		// TODO Auto-generated method stub
	}

	@Override
	public void Replay(int twID) {
		// TODO Auto-generated method stub
	}

	@Override
	public void Direct(int twID) {
		// TODO Auto-generated method stub
	}

	@Override
	public void Follow(int userID) {
		// TODO Auto-generated method stub
	}

	@Override
	public void UnFollow(int userID) {
		// TODO Auto-generated method stub
	}


}
