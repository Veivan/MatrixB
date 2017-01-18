package network;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.Twitter;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import model.ElementCredentials;
import service.CustExeptions.AuthenticationException;
import service.CustExeptions.ProxyException;
import service.Constants;

public class OAuthPasswordAuthenticator {

	private HttpClient client = HttpClientBuilder.create()
			.setUserAgent(Constants.USER_AGENT).build();
	private Twitter twitter; // = new TwitterFactory().getInstance();
	private ElementCredentials creds;
	private String cookies;

	static Logger logger = LoggerFactory
			.getLogger(OAuthPasswordAuthenticator.class);

	public OAuthPasswordAuthenticator(final Twitter twitter, final ElementCredentials creds) {
		this.twitter = twitter;
		this.creds = creds;
	}

	/**
	 * Getting token without PIN
	 * 
	 * @return
	 */
	public AccessToken getOAuthAccessTokenSilent() throws Exception {
		RequestToken requestToken = null;
		try {
			requestToken = twitter
					.getOAuthRequestToken(Constants.DEFAULT_OAUTH_CALLBACK);
		} catch (Exception e) {
			throw new ProxyException(e);
		}
		try {
			final String oauth_token = requestToken.getToken();

			logger.debug("Got request token.");
			logger.debug("Request token: " + oauth_token);
			logger.debug("Request token secret: "
					+ requestToken.getTokenSecret());

			// make sure cookies is turn on
			CookieHandler.setDefault(new CookieManager());

			String page = GetPageContent(requestToken.getAuthorizationURL());

			String authenticity_token = readAuthenticityToken(page);
			if (authenticity_token.isEmpty())
				throw new AuthenticationException(
						"Cannot get authenticity_token.");

			final Configuration conf = twitter.getConfiguration();
			logger.debug("OAuthAuthorizationURL : "
					+ conf.getOAuthAuthorizationURL());

			List<NameValuePair> paramList = new ArrayList<NameValuePair>();

			paramList.add(new BasicNameValuePair("oauth_token", URLEncoder
					.encode(oauth_token, "UTF-8")));
			paramList.add(new BasicNameValuePair("session[username_or_email]",
					URLEncoder.encode(this.creds.getUSER(), "UTF-8")));
			paramList.add(new BasicNameValuePair("session[password]",
					URLEncoder.encode(this.creds.getUSER_PASS(), "UTF-8")));
			paramList.add(new BasicNameValuePair("authenticity_token",
					URLEncoder.encode(authenticity_token, "UTF-8")));

			String page2 = sendPost(conf.getOAuthAuthorizationURL().toString(),
					paramList);

			final String oauth_verifier = readOauthVerifier(page2);

			if (oauth_verifier == null || oauth_verifier.isEmpty())
				throw new AuthenticationException("Cannot get OAuth verifier.");

			AccessToken accessToken = twitter.getOAuthAccessToken(requestToken,
					oauth_verifier);

			logger.debug("Got access token.");
			logger.debug("Access token: " + accessToken.getToken());
			logger.debug("Access token secret: "
					+ accessToken.getTokenSecret());

			return accessToken;
		} catch (Exception e) {
			throw new AuthenticationException(e);
		}
		// TODO Сделать обработку кодов 401 - 405, при этом не банить прокси
		/*catch (TwitterException te) {
                        if (401 == te.getStatusCode()) {
                            System.out.println("Unable to get the access token.");
                        } else {
                            te.printStackTrace();
                        }*/
	}

	private String GetPageContent(String url) throws Exception {
		String result = "";
		HttpGet request = new HttpGet(url);

		request.setHeader("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		request.setHeader("Accept-Language",
				"ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4,bg;q=0.2");

		HttpResponse response = client.execute(request);
		int responseCode = response.getStatusLine().getStatusCode();

		logger.debug("\nSending 'GET' request to URL : " + url);
		logger.debug("Response Code : " + responseCode);

		// set cookies
		setCookies(response.getFirstHeader("set-cookie") == null ? ""
				: collectCookiesresponse(response.getHeaders("set-cookie")));

		result = ReadStream(response.getEntity().getContent());
		return result;
	}

	private String sendPost(String url, List<NameValuePair> postParams)
			throws Exception {

		HttpResponse response = null;
		String result = "";

		HttpPost post = new HttpPost(url);

		// add header
		post.setHeader("Host", "twitter.com");
		post.setHeader("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		post.setHeader("Accept-Language",
				"ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4,bg;q=0.2");
		post.setHeader("Cookie", getCookies());
		post.setHeader("Connection", "keep-alive");
		post.setHeader("Referer", "https://twitter.com");
		post.setHeader("Content-Type", "application/x-www-form-urlencoded");

		post.setEntity(new UrlEncodedFormEntity(postParams, "UTF-8"));
		response = client.execute(post);
		int responseCode = response.getStatusLine().getStatusCode();

		logger.debug("\nSending 'POST' request to URL : " + url);
		logger.debug("Post parameters : " + postParams);
		logger.debug("Response Code : " + responseCode);

		result = ReadStream(response.getEntity().getContent());
		return result;
	}

	private static String collectCookiesresponse(Header[] headers) {
		StringBuilder result = new StringBuilder();
		for (Header header : headers) {
			if (result.length() == 0) {
				result.append(header.toString());
			} else {
				result.append(";" + header.getValue());
			}
		}
		return result.toString();
	}

	public String getCookies() {
		return cookies;
	}

	public void setCookies(String cookies) {
		this.cookies = cookies;
	}

	private String ReadStream(InputStream inputStream) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(inputStream);
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		int result = bis.read();
		while (result != -1) {
			buf.write((byte) result);
			result = bis.read();
		}
		return buf.toString();
	}

	private static String readAuthenticityToken(String html)
			throws Exception {
		logger.debug("Extracting authenticity_token...");
		Document doc = Jsoup.parse(html);
		String result = "";
		// Login form id
		Element loginform = doc.getElementById("oauth_form");
		Elements inputElements = loginform.getElementsByTag("input");

		for (Element inputElement : inputElements) {
			String key = inputElement.attr("name");
			String value = inputElement.attr("value");
			if (key.equals("authenticity_token")) {
				result = value;
				break;
			}
		}
		return result;
	}
	
	private static String readOauthVerifier(String html) {
		Document document = Jsoup.parse(html);
		String result = "";
		Elements metalinks = document.select("meta[http-equiv=refresh]");
		try {
			String content = metalinks.attr("content").split(";")[1];
			Pattern pattern = Pattern.compile(".*oauth_verifier=?(.*)$",
					Pattern.CASE_INSENSITIVE);
			Matcher m = pattern.matcher(content);
			result = m.matches() ? m.group(1) : null;
			//logger.debug(result);
			return result;
		} catch (Exception e) {
			return null;
		}
	}
}
