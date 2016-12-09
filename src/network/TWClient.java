package network;

import inrtfs.IAccount;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import javax.net.ssl.SSLContext;

import jobs.JobAtom;
import main.ConcreteAcc;
import main.MatrixAct;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import service.Constants;
import service.Constants.JobType;
import service.Constants.ProxyType;
import service.Constants.RequestType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TWClient extends Thread {

	final int CONNTECTION_TIMEOUT_MS = 20;
	final int CONNECTION_REQUEST_TIMEOUT_MS = 20;
	final int SOCKET_TIMEOUT_MS = 20;

	private String AccessToken;
	private String AccessSecret;
	private String ConsumerKey;
	private String ConsumerSecret;

	private long ID;
	private JobAtom job;
	private IAccount acc;

	private CloseableHttpClient httpclient;
	private String ip;
	private int port;
	private Constants.ProxyType proxyType;

	public TWClient(MatrixAct theact) {
		this.ID = theact.getSelfID();
		this.job = theact.getJob();
		this.acc = theact.getAcc();
	}

	static Logger logger = LoggerFactory.getLogger(TWClient.class);

	public boolean GetProxy(boolean IsDebug) {
		ElementProxy dbproxy = null;
		if (IsDebug) {

			dbproxy = new ElementProxy("47.88.30.164", 1080, ProxyType.SOCKS); // Socks5

			//dbproxy = new ElementProxy("185.101.236.83", 1080, ProxyType.SOCKS); 	// Socks4

			//dbproxy = new ElementProxy("85.174.236.106", 3128, ProxyType.HTTPS);

		} else {
			dbproxy = ProxyGetter.getProxy(this.acc.getAccID());
			if (dbproxy == null) {
				logger.error("TWClient cant get proxy");
				logger.debug("TWClient cant get proxy");
				return false;
			}
		}

		this.ip = dbproxy.getIp();
		this.port = dbproxy.getPort();
		this.proxyType = dbproxy.getProxyType();
		return true;
	}

	@Override
	public void run() {

		// print internal state
		// LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		// StatusPrinter.print(lc);

		logger.info("TWClient run Action : {} {} accID = {} ID = {}",
				this.job.Type.name(), Constants.dfm.format(this.job.timestamp),
				this.acc.getAccID(), this.ID);

		boolean IsDebug = true;
		if (!GetProxy(IsDebug))
			return;

		logger.info("TWClient got proxy {} : accID = {} ID = {}",
				IsDebug ? "Debug" : "", this.acc.getAccID(), this.ID);

		if (this.proxyType == ProxyType.SOCKS) {
			// make SOCKS proxy
			Registry<ConnectionSocketFactory> reg = RegistryBuilder
					.<ConnectionSocketFactory> create()
					.register("http", PlainConnectionSocketFactory.INSTANCE)
					.register(
							"https",
							new MyConnectionSocketFactory(SSLContexts
									.createSystemDefault())).build();
			PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(
					reg);
			this.httpclient = HttpClients.custom().setConnectionManager(cm)
					.build();
		} else {
			final RequestConfig requestConfig = RequestConfig.custom()
					.setConnectTimeout(CONNTECTION_TIMEOUT_MS)
					.setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT_MS)
					.setSocketTimeout(SOCKET_TIMEOUT_MS).build();
			this.httpclient = HttpClients.custom()
					.setDefaultRequestConfig(requestConfig).build();
		}

		try {
			HttpClientContext context = HttpClientContext.create();

			TypedURI uricust = new TypedURI(this.job);
			HttpRequestBase request;
			if (uricust.getType() == RequestType.GET)
				request = new HttpGet(uricust.getUri());
			else
				request = new HttpPost(uricust.getUri());

			if (this.proxyType == ProxyType.SOCKS) {
				InetSocketAddress socksaddr = new InetSocketAddress(this.ip,
						this.port);
				context.setAttribute("socks.address", socksaddr);
			} else {
				// make HTTP proxy
				HttpHost proxy = new HttpHost(this.ip, this.port, "http");
				RequestConfig config = RequestConfig.custom().setProxy(proxy)
						.build();
				request.setConfig(config);
			}
			request.setHeader("User-Agent", "MySuperUserAgent");

			// При обращении к сайту авторизация не обязательна
			if (this.job.Type != JobType.VISIT) {
				SetAuth();
				OAuthConsumer consumer = new CommonsHttpOAuthConsumer(
						ConsumerKey, ConsumerSecret);
				consumer.setTokenWithSecret(AccessToken, AccessSecret);
				consumer.sign(request);
			}

			CloseableHttpResponse response = httpclient.execute(request,
					context);
			try {
				// System.out.println("----------------------------------------");
				String message = response.getStatusLine().toString();
				logger.info("ResponseStatus: {}", message);

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
			logger.error("TWClient run thrown exception", e);
			logger.debug("TWClient run thrown exception", e);
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				logger.error("An httpclient.close thrown exception :", e);
				logger.debug("An httpclient.close thrown exception :", e);
			}
		}

	}

	// DEBUG
	public static void main(String[] args) {

		//JobAtom job = new JobAtom(5L, "VISIT",
		// "http://geokot.com/reqwinfo/getreqwinfo?");
		// "http://veivan.ucoz.ru");
		// "https://www.verisign.com/");
		//		"https://publish.twitter.com/#");
		// "https://publish.twitter.com/oembed?url=https%3A%2F%2Ftwitter.com%2FInterior%2Fstatus%2F507185938620219395");

		JobAtom job = new JobAtom(5L, "TWIT", "its_very_cold");

		ConcreteAcc acc = new ConcreteAcc(1L);
		MatrixAct theact = new MatrixAct(job, acc);

		TWClient client = new TWClient(theact);

		logger.info("TWClient debug main");
		client.run();
	}

	static class MyConnectionSocketFactory extends SSLConnectionSocketFactory {

		public MyConnectionSocketFactory(final SSLContext sslContext) {
			super(sslContext);
		}

		@Override
		public Socket createSocket(final HttpContext context)
				throws IOException {
			InetSocketAddress socksaddr = (InetSocketAddress) context
					.getAttribute("socks.address");
			Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksaddr);
			return new Socket(proxy);
		}
	}

	private void SetAuth() {

		/* karafuter with udm206 keys*/
		this.ConsumerKey = "XNZFUzPAzVyFTynumqFc7ZiwF";
		this.ConsumerSecret = "aJGrMxyCv8y8D1UPWN4Gs1Ym4dx3pBMSsxCjMnDAdMWc56DOjb";
		this.AccessToken = "2936887497-zvzhnnLETN9xuH9zzDhBbPneFIH2ipfo73aTAfJ";
		this.AccessSecret = "HjRhO4Nj52UXJvjbazgkjVBaMMIw6VFmB6rhSMOu2cxcq";

		/* udm206 
		this.ConsumerKey = "XNZFUzPAzVyFTynumqFc7ZiwF";
		this.ConsumerSecret = "aJGrMxyCv8y8D1UPWN4Gs1Ym4dx3pBMSsxCjMnDAdMWc56DOjb";
		this.AccessToken = "753234721631502337-qY75MD9v1CZOsIvoQOr9aEnCnHigFt3";
		this.AccessSecret = "ahfBWwTEmqS98jagxQhRYk28ZWT95vx3a20wwfwRNGVpC";
		*/
		
		/* karafuter
		this.ConsumerKey = "YEgJkngnkDR7Ql3Uz5ZKkYgBU";
		this.ConsumerSecret = "CsCz7WmytpUoWqIUp9qQPRS99kMk4w9QoSH3GcStnpPc4mf1Ai";
		this.AccessToken = "2936887497-5nk4yK5mRMpIVVZvzyAoog50EgQhr1rfFQLrpFG";
		this.AccessSecret = "CQZdBCyLmqVRIMxj1sVHDvQsHcj8Nr2WVfPSggp7yi9sX";
		*/
		
		/*
		 * was read-only access this.AccessToken =
		 * "2936887497-j19YUO9hyhwNREQyfABs10wdt2XlfcXwuCVFYj0";
		 * this.AccessSecret = "w0JscngvMK7FwgYvDreZjGkkULl5hNizV4oTJlRas5cRq";
		 */

		/*
		 * ReaderIni keys = new ReaderIni(); OAuthConsumer consumer = new
		 * CommonsHttpOAuthConsumer(keys.cConsumerKey, keys.cConsumerSecret);
		 * consumer.setTokenWithSecret(keys.cAccessToken, keys.cAccessSecret);
		 */
	}
}
