package network;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.Proxy;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import model.ElementCredentials;
import network.T4jClient.AuthenticationException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import service.Constants;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;

public class OAuthPasswordAuthenticator {

	private HttpClient client = HttpClientBuilder.create().setUserAgent(Constants.USER_AGENT).build();
	private Twitter twitter = new TwitterFactory().getInstance();
	private final Proxy proxy; 
	private ElementCredentials creds;
	private String cookies;

	static Logger logger = LoggerFactory.getLogger(OAuthPasswordAuthenticator.class);

	public OAuthPasswordAuthenticator(final Proxy proxy, final ElementCredentials creds) { 
		this.proxy = proxy; 
		this.creds = creds; 
	} 
	 
	 /**
	 * Getting token without PIN
	 * 
	 * @return
	 */
	public AccessToken getOAuthAccessTokenSilent() throws Exception {
			twitter.setOAuthConsumer(this.creds.getCONSUMER_KEY(), this.creds.getCONSUMER_SECRET());
			final RequestToken requestToken = twitter.getOAuthRequestToken(Constants.DEFAULT_OAUTH_CALLBACK);
			final String oauth_token = requestToken.getToken();
			
			System.out.println("Got request token.");
			System.out.println("Request token: " + oauth_token);
			System.out.println("Request token secret: "	+ requestToken.getTokenSecret());
							
			// make sure cookies is turn on
			CookieHandler.setDefault(new CookieManager());

			String page = GetPageContent(requestToken.getAuthorizationURL());
//			List<NameValuePair> postParams = Utils.getFormParams(page, USER, USER_PASS);

			String authenticity_token = readAuthenticityToken(page);
			if (authenticity_token.isEmpty())
				throw new AuthenticationException(
						"Cannot get authenticity_token.");

			final Configuration conf = twitter.getConfiguration();
			System.out.println("OAuthAuthorizationURL : " + conf.getOAuthAuthorizationURL());
		
			List<NameValuePair> paramList = new ArrayList<NameValuePair>();

			paramList.add(new BasicNameValuePair("oauth_token", URLEncoder.encode(oauth_token, "UTF-8")));
			paramList.add(new BasicNameValuePair("session[username_or_email]", URLEncoder.encode(this.creds.getUSER(), "UTF-8")));
			paramList.add(new BasicNameValuePair("session[password]", URLEncoder.encode(this.creds.getUSER_PASS(), "UTF-8")));
			paramList.add(new BasicNameValuePair("authenticity_token", URLEncoder.encode(authenticity_token, "UTF-8")));

			String page2 = sendPost(conf.getOAuthAuthorizationURL().toString(), paramList);

			final String oauth_verifier = Utils.readOauthVerifier(page2);
			// parseParameters(callback_url.substring(callback_url.indexOf("?")
			// + 1)).get(OAUTH_VERIFIER);

			if (oauth_verifier.isEmpty())
				throw new AuthenticationException("Cannot get OAuth verifier.");

			AccessToken accessToken = twitter.getOAuthAccessToken(requestToken,
						oauth_verifier);

			System.out.println("Got access token.");
			System.out.println("Access token: " + accessToken.getToken());
			System.out.println("Access token secret: "
					+ accessToken.getTokenSecret());
			
			return accessToken;
	}

	private String GetPageContent(String url) throws Exception {
		String result = "";
		HttpGet request = new HttpGet(url);

		//request.setHeader("User-Agent", Constants.USER_AGENT);
		request.setHeader("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		request.setHeader("Accept-Language",
				"ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4,bg;q=0.2");

		HttpResponse response = client.execute(request);
		int responseCode = response.getStatusLine().getStatusCode();

		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		// set cookies
		setCookies(response.getFirstHeader("set-cookie") == null ? ""
				: collectCookiesresponse(response.getHeaders("set-cookie")));

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

	public static String readAuthenticityToken(String html)
			throws UnsupportedEncodingException {
		System.out.println("Extracting authenticity_token...");
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
}
