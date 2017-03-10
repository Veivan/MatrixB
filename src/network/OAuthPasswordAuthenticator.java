package network;

import inrtfs.IAccount;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
import twitter4j.TwitterException;
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
	private IAccount acc;
	private String cookies;

	static Logger logger = LoggerFactory
			.getLogger(OAuthPasswordAuthenticator.class);

	public OAuthPasswordAuthenticator(final Twitter twitter,
			final ElementCredentials creds, IAccount acc) {
		this.twitter = twitter;
		this.creds = creds;
		this.acc = acc;
	}

	/**
	 * Getting token without PIN
	 * 
	 * @return
	 */
	public AccessToken getOAuthAccessTokenSilent() throws Exception {
		RequestToken requestToken = null;
		AccessToken accessToken = null;
		try {
			for (int i = 0; i < 2; i++) {
				String msg = String.format(
						"Get OAuthRequestToken shot %d ERROR : ", i + 1);
				try {
					requestToken = twitter
							.getOAuthRequestToken(Constants.DEFAULT_OAUTH_CALLBACK);
					if (requestToken != null)
						break;
				} catch (TwitterException te) {
					if (te.isCausedByNetworkIssue()) {
						logger.error(msg, te);
					} else {
						throw te;
					}
				}
			}
			if (requestToken == null)
				throw new ProxyException("Bad proxy when getOAuthRequestToken");

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
			if (page2.isEmpty() || page2.contains("login/error"))
				throw new AuthenticationException("It seems bad password.");
			if (page2.contains("RetypeEmail")
					|| page2.contains("RetypePhoneNumber")) {
				logger.debug("Retype challenge : " + page2);

				String urlChallenge = GetChallengeUrl(page2);
				List<NameValuePair> params = MakeChallengeParams(urlChallenge);
				String url = params.get(0).getValue();
				params.remove(0);

				if (page2.contains("RetypeEmail"))
					params.add(new BasicNameValuePair("challenge_response",
							((model.ConcreteAcc) this.acc).getEmail()));
				else
					// RetypePhoneNumber
					params.add(new BasicNameValuePair("challenge_response",
							((model.ConcreteAcc) this.acc).getPhone()));

				Random random = new Random();
				long delay = (1 + random.nextInt(30)) * 1000;
				Thread.sleep(delay); // Случайная задержка, имитация чела
				
				String page3 = sendPost(url, params);
				if (page3.contains("twitter.com/login/error"))
					throw new AuthenticationException(
							"Cannot get verifier - Retype.");
			}

			final String oauth_verifier = readOauthVerifier(page2);

			if (oauth_verifier == null || oauth_verifier.isEmpty())
				throw new AuthenticationException("Cannot get OAuth verifier.");

			for (int i = 0; i < 2; i++) {
				String msg = String.format(
						"Get getOAuthAccessToken shot %d ERROR : ", i + 1);
				try {
					accessToken = twitter.getOAuthAccessToken(requestToken,
							oauth_verifier);
					if (accessToken != null)
						break;
				} catch (TwitterException te) {
					if (te.isCausedByNetworkIssue()) {
						logger.error(msg, te);
					} else {
						throw te;
					}
				}
			}
			if (accessToken == null)
				throw new ProxyException("Bad proxy when getOAuthAccessToken");

			logger.debug("Got access token.");
			logger.debug("Access token: " + accessToken.getToken());
			logger.debug("Access token secret: " + accessToken.getTokenSecret());

			return accessToken;
		} catch (TwitterException te) {
			if (te.isCausedByNetworkIssue()) {
				throw new ProxyException(te);
			} else {
				throw te;
			}
		} catch (Exception e) {
			throw e;
		}
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

		if (postParams != null)
			post.setEntity(new UrlEncodedFormEntity(postParams, "UTF-8"));
		response = client.execute(post);
		int responseCode = response.getStatusLine().getStatusCode();

		logger.debug("\nSending 'POST' request to URL : " + url);
		logger.debug("Post parameters : " + postParams);
		logger.debug("Response Code : " + responseCode);

		result = ReadStream(response.getEntity().getContent());
		return result;
	}

	private List<NameValuePair> MakeChallengeParams(String url)
			throws Exception {
		List<NameValuePair> paramList = new ArrayList<NameValuePair>();
		int pos = url.indexOf("?");
		String[] sp = { url.substring(0, pos), url.substring(pos + 1) };
		paramList.add(new BasicNameValuePair("url", sp[0]));
		String[] sp2 = sp[1].split("&");
		for (String record : sp2) {
			String[] sp3 = record.split("=");
			paramList.add(new BasicNameValuePair(sp3[0], URLEncoder.encode(
					sp3[1], "UTF-8")));
		}
		return paramList;
	}

	private String GetChallengeUrl(String html) throws Exception {
		logger.debug("Extracting Challenge Url...");
		String result = "";
		Document doc = Jsoup.parse(html);
		Element mBody = doc.body();
		Elements urls = mBody.getElementsByTag("a");
		for (Element url : urls) {
			// ... и вытаскиваем их название...
			result = url.attr("href");
			logger.debug(result);
			break;
		}
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

	private static String readAuthenticityToken(String html) throws Exception {
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
			// logger.debug(result);
			return result;
		} catch (Exception e) {
			return null;
		}
	}
}
