package twitter4j;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.HttpClientBase;
import twitter4j.HttpClientConfiguration;
import twitter4j.HttpParameter;
import twitter4j.HttpRequest;
import twitter4j.HttpResponse;
import twitter4j.HttpResponseImpl;
import twitter4j.RequestMethod;
import twitter4j.TwitterException;

/**
 * @author karafuter - veivan at yandex.ru
 */
public class AlternativeHttpClientImpl extends HttpClientBase {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory
			.getLogger(AlternativeHttpClientImpl.class);

	public AlternativeHttpClientImpl(HttpClientConfiguration conf) {
		super(conf);
	}

	@Override
	public HttpResponse get(String url) throws TwitterException {
		return request(new HttpRequest(RequestMethod.GET, url, null, null, null));
	}

	public HttpResponse post(String url, HttpParameter[] params)
			throws TwitterException {
		return request(new HttpRequest(RequestMethod.POST, url, params, null,
				null));
	}

	@Override
	public HttpResponse handleRequest(HttpRequest req) throws TwitterException {
		int retriedCount;
		int retry = CONF.getHttpRetryCount() + 1;
		HttpResponse res = null;
		for (retriedCount = 0; retriedCount < retry; retriedCount++) {
			int responseCode = -1;
			try {
				HttpURLConnection con;
				OutputStream os = null;
				try {
					con = getConnection(req.getURL());
					con.setDoInput(true);
					setHeaders(req, con);
					con.setRequestMethod(req.getMethod().name());
					if (req.getMethod() == RequestMethod.POST) {
						if (HttpParameter.containsFile(req.getParameters())) {
							String boundary = "----Twitter4J-upload"
									+ System.currentTimeMillis();
							con.setRequestProperty("Content-Type",
									"multipart/form-data; boundary=" + boundary);
							boundary = "--" + boundary;
							con.setDoOutput(true);
							os = con.getOutputStream();
							DataOutputStream out = new DataOutputStream(os);
							for (HttpParameter param : req.getParameters()) {
								if (param.isFile()) {
									write(out, boundary + "\r\n");
									write(out,
											"Content-Disposition: form-data; name=\""
													+ param.getName()
													+ "\"; filename=\""
													+ param.getFile().getName()
													+ "\"\r\n");
									write(out,
											"Content-Type: "
													+ param.getContentType()
													+ "\r\n\r\n");
									BufferedInputStream in = new BufferedInputStream(
											param.hasFileBody() ? param
													.getFileBody()
													: new FileInputStream(param
															.getFile()));
									byte[] buff = new byte[1024];
									int length;
									while ((length = in.read(buff)) != -1) {
										out.write(buff, 0, length);
									}
									write(out, "\r\n");
									in.close();
								} else {
									write(out, boundary + "\r\n");
									write(out,
											"Content-Disposition: form-data; name=\""
													+ param.getName()
													+ "\"\r\n");
									write(out,
											"Content-Type: text/plain; charset=UTF-8\r\n\r\n");
									logger.debug(param.getValue());
									out.write(param.getValue()
											.getBytes("UTF-8"));
									write(out, "\r\n");
								}
							}
							write(out, boundary + "--\r\n");
							write(out, "\r\n");

						} else {
							con.setRequestProperty("Content-Type",
									"application/x-www-form-urlencoded");
							String postParam = HttpParameter
									.encodeParameters(req.getParameters());
							logger.debug("Post Params: ", postParam);
							byte[] bytes = postParam.getBytes("UTF-8");
							con.setRequestProperty("Content-Length",
									Integer.toString(bytes.length));
							con.setDoOutput(true);
							os = con.getOutputStream();
							os.write(bytes);
						}
						os.flush();
						os.close();
					}
					res = new HttpResponseImpl(con, CONF);
					responseCode = con.getResponseCode();
					if (logger.isDebugEnabled()) {
						logger.debug("Response: ");
						Map<String, List<String>> responseHeaders = con
								.getHeaderFields();
						for (String key : responseHeaders.keySet()) {
							List<String> values = responseHeaders.get(key);
							for (String value : values) {
								if (key != null) {
									logger.debug(key + ": " + value);
								} else {
									logger.debug(value);
								}
							}
						}
					}
					if (responseCode < HttpResponseCode.OK
							|| (responseCode != HttpResponseCode.FOUND && HttpResponseCode.MULTIPLE_CHOICES <= responseCode)) {
						if (responseCode == HttpResponseCode.ENHANCE_YOUR_CLAIM
								|| responseCode == HttpResponseCode.BAD_REQUEST
								|| responseCode < HttpResponseCode.INTERNAL_SERVER_ERROR
								|| retriedCount == CONF.getHttpRetryCount()) {
							throw new TwitterException(res.asString(), res);
						}
						// will retry if the status code is
						// INTERNAL_SERVER_ERROR
					} else {
						break;
					}
				} finally {
					try {
						os.close();
					} catch (Exception ignore) {
					}
				}
			} catch (IOException ioe) {
				// connection timeout or read timeout
				if (retriedCount == CONF.getHttpRetryCount()) {
					throw new TwitterException(ioe.getMessage(), ioe,
							responseCode);
				}
			}
			try {
				if (logger.isDebugEnabled() && res != null) {
					res.asString();
				}
				logger.debug("Sleeping " + CONF.getHttpRetryIntervalSeconds()
						+ " seconds until the next retry.");
				Thread.sleep(CONF.getHttpRetryIntervalSeconds() * 1000);
			} catch (InterruptedException ignore) {
				// nothing to do
			}
		}
		return res;
	}

	/**
	 * sets HTTP headers
	 *
	 * @param req
	 *            The request
	 * @param connection
	 *            HttpURLConnection
	 */
	private void setHeaders(HttpRequest req, HttpURLConnection connection) {
		if (logger.isDebugEnabled()) {
			logger.debug("Request: ");
			logger.debug(req.getMethod().name() + " ", req.getURL());
		}

		String authorizationHeader;
		if (req.getAuthorization() != null
				&& (authorizationHeader = req.getAuthorization()
						.getAuthorizationHeader(req)) != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Authorization: ",
						authorizationHeader.replaceAll(".", "*"));
			}
			connection.addRequestProperty("Authorization", authorizationHeader);
		}
		if (req.getRequestHeaders() != null) {
			for (String key : req.getRequestHeaders().keySet()) {
				connection.addRequestProperty(key,
						req.getRequestHeaders().get(key));
				logger.debug(key + ": " + req.getRequestHeaders().get(key));
			}
		}
	}

	HttpURLConnection getConnection(String url) throws IOException {
		HttpURLConnection con;
		if (isProxyConfigured()) {
			if (CONF.getHttpProxyUser() != null
					&& !CONF.getHttpProxyUser().equals("")) {
				if (logger.isDebugEnabled()) {
					logger.debug("Proxy AuthUser: " + CONF.getHttpProxyUser());
					logger.debug("Proxy AuthPassword: "
							+ CONF.getHttpProxyPassword().replaceAll(".", "*"));
				}
				Authenticator.setDefault(new Authenticator() {
					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						// respond only to proxy auth requests
						if (getRequestorType().equals(RequestorType.PROXY)) {
							return new PasswordAuthentication(CONF
									.getHttpProxyUser(), CONF
									.getHttpProxyPassword().toCharArray());
						} else {
							return null;
						}
					}
				});
			}

			Proxy.Type prType = CONF.getHttpProxyHost().contains("SOCKS") ? Proxy.Type.SOCKS
					: Proxy.Type.HTTP;

			final Proxy proxy = new Proxy(prType,
					InetSocketAddress.createUnresolved(CONF.getHttpProxyHost(),
							CONF.getHttpProxyPort()));
			if (logger.isDebugEnabled()) {
				logger.debug("Opening proxied connection("
						+ CONF.getHttpProxyHost() + ":"
						+ CONF.getHttpProxyPort() + ")");
			}
			con = (HttpURLConnection) new URL(url).openConnection(proxy);
		} else {
			con = (HttpURLConnection) new URL(url).openConnection();
		}
		if (CONF.getHttpConnectionTimeout() > 0) {
			con.setConnectTimeout(CONF.getHttpConnectionTimeout());
		}
		if (CONF.getHttpReadTimeout() > 0) {
			con.setReadTimeout(CONF.getHttpReadTimeout());
		}
		con.setInstanceFollowRedirects(false);
		return con;
	}

}
