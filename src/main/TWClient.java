package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class TWClient extends Thread {

	static String AccessToken = "2936887497-j19YUO9hyhwNREQyfABs10wdt2XlfcXwuCVFYj0";
	static String AccessSecret = "w0JscngvMK7FwgYvDreZjGkkULl5hNizV4oTJlRas5cRq";
	static String ConsumerKey = "YEgJkngnkDR7Ql3Uz5ZKkYgBU";
	static String ConsumerSecret = "CsCz7WmytpUoWqIUp9qQPRS99kMk4w9QoSH3GcStnpPc4mf1Ai";

	private MatrixAct act;

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

	public TWClient() {
	}

	public TWClient(MatrixAct act) {
		this.act = act;
	}

	@Override
	public void run() {
//		System.out.printf("Action: %s \n", act.getActionTXT());

		OAuthConsumer consumer = new CommonsHttpOAuthConsumer(ConsumerKey,
				ConsumerSecret);
		consumer.setTokenWithSecret(AccessToken, AccessSecret);

		CloseableHttpClient httpclient = HttpClients.custom().build();
		try {
			URI uri = new URIBuilder(
					"https://stream.twitter.com/1.1/statuses/filter.json")
					//.addParameter("track", "москва")
					 .addParameter("track", "питер")
					// .addParameters(listparams)
					// .setParameters(listparams)
					.build();
			HttpGet request = new HttpGet(uri);
			consumer.sign(request);
			CloseableHttpResponse response = httpclient.execute(request);
			try {
				// System.out.println("----------------------------------------");
				// System.out.println(response.getStatusLine());
				String message = response.getStatusLine().toString();
				System.out.printf("ResponseStatus: %s \n", message);

				HttpEntity httpEntity = response.getEntity();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						httpEntity.getContent()));
				String line;
				while ((line = br.readLine()) != null) {
					if (line.isEmpty())
						continue;
					System.out.println(line);
				}
				EntityUtils.consume(httpEntity);
			} finally {
				response.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static void main(String[] args) {

		TWClient client = new TWClient();
		try {
			client.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * @Override public boolean Auth() { ReaderIni keys = new ReaderIni();
	 * OAuthConsumer consumer = new CommonsHttpOAuthConsumer(keys.cConsumerKey,
	 * keys.cConsumerSecret); consumer.setTokenWithSecret(keys.cAccessToken,
	 * keys.cAccessSecret); return false; }
	 * 
	 * @Override public void SetAva() { // TODO Auto-generated method stub }
	 * 
	 * @Override public void SetBackgrnd() { // TODO Auto-generated method stub
	 * }
	 * 
	 * @Override public void Twit(String mess) { // TODO Auto-generated method
	 * stub }
	 * 
	 * @Override public void ReTwit(int twID, String mess) { // TODO
	 * Auto-generated method stub }
	 * 
	 * @Override public void Like(int twID) { // TODO Auto-generated method stub
	 * }
	 * 
	 * @Override public void Replay(int twID) { // TODO Auto-generated method
	 * stub }
	 * 
	 * @Override public void Direct(int twID) { // TODO Auto-generated method
	 * stub }
	 * 
	 * @Override public void Follow(int userID) { // TODO Auto-generated method
	 * stub }
	 * 
	 * @Override public void UnFollow(int userID) { // TODO Auto-generated
	 * method stub }
	 */
}
