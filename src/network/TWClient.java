package network;

import inrtfs.IAccount;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketTimeoutException;

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
import org.apache.http.conn.ConnectTimeoutException;
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

			//dbproxy = new ElementProxy("213.144.144.57", 45554, ProxyType.SOCKS);
			
			dbproxy = new ElementProxy("82.204.180.43", 38572, ProxyType.SOCKS);
			 
			//dbproxy = new ElementProxy("213.171.46.186", 3128, ProxyType.HTTP);


			// good
			// TWClient client = new TWClient("120.52.73.97", 80,
			// ProxyType.HTTP);
			// bad
			// TWClient client = new TWClient("82.195.17.129", 8080,
			// ProxyType.HTTP);

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

		final RequestConfig requestConfig = RequestConfig.custom()
				.setConnectTimeout(CONNTECTION_TIMEOUT_MS)
				.setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT_MS)
				.setSocketTimeout(SOCKET_TIMEOUT_MS).build();

		if (this.proxyType == ProxyType.SOCKS) {
			// make SOCKS proxy

			Registry<ConnectionSocketFactory> reg = RegistryBuilder
					.<ConnectionSocketFactory> create()
					.register("http", PlainConnectionSocketFactory.INSTANCE)
					.register(
							"https",
							new MyConnectionSocketFactory(SSLContexts
									.createSystemDefault())).build();

			/*
			 * Registry<ConnectionSocketFactory> reg =
			 * RegistryBuilder.<ConnectionSocketFactory> create()
			 * .register("http", new MyConnectionSocketFactory()).build();
			 */
			PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(
					reg);
			this.httpclient = HttpClients.custom().setConnectionManager(cm).build();
		} else
			this.httpclient = HttpClients.custom()
					.setDefaultRequestConfig(requestConfig).build();

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

			/*
			 * HttpClient httpclient2 = new DefaultHttpClient();
			 * 
			 * HttpClient httpclient = new HttpClient(); HttpResponse response;
			 * HttpPost httpget = new HttpPost(uricust.getUri()); try { response
			 * = httpclient.execute(httpget);
			 * System.out.println(response.getStatusLine().toString()); }
			 * finally { httpget.releaseConnection(); }
			 */

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
		//		"https://www.verisign.com/");
		// "https://publish.twitter.com/#");
		// "https://publish.twitter.com/oembed?url=https%3A%2F%2Ftwitter.com%2FInterior%2Fstatus%2F507185938620219395");

		JobAtom job = new JobAtom(5L, "TWIT", "Hi_people");

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
	    public Socket createSocket(final HttpContext context) throws IOException {
	        InetSocketAddress socksaddr = (InetSocketAddress) context.getAttribute("socks.address");
	        Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksaddr);
	        return new Socket(proxy);
	    }

	    /*public Socket createSocket(final HttpContext context)
				throws IOException {
			InetSocketAddress socksaddr = (InetSocketAddress) context
					.getAttribute("socks.address");
			Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksaddr);
			return new Socket(proxy);
		}

		public Socket connectSocket(final int connectTimeout,
				final Socket socket, final HttpHost host,
				final InetSocketAddress remoteAddress,
				final InetSocketAddress localAddress, final HttpContext context)
				throws IOException, ConnectTimeoutException {
			Socket sock;
			if (socket != null) {
				sock = socket;
			} else {
				sock = createSocket(context);
			}
			if (localAddress != null) {
				sock.bind(localAddress);
			}
			try {
				sock.connect(remoteAddress, connectTimeout);
			} catch (SocketTimeoutException ex) {
				throw new ConnectTimeoutException(ex, host,
						remoteAddress.getAddress());
			}
			return sock;
		}*/

	}

	private void SetAuth() {
		this.ConsumerKey = "YEgJkngnkDR7Ql3Uz5ZKkYgBU";
		this.ConsumerSecret = "CsCz7WmytpUoWqIUp9qQPRS99kMk4w9QoSH3GcStnpPc4mf1Ai";
		this.AccessToken = "2936887497-voH4VfwhWGAMt2ur46ejogsY1wimD9k4qUGpMMp";
		this.AccessSecret = "QYJS2HsxMcLbUcAipeaMhWc4EsxdYjEQH65ciG63U9fIX";

		/* was read-only access
		this.AccessToken = "2936887497-j19YUO9hyhwNREQyfABs10wdt2XlfcXwuCVFYj0";
		this.AccessSecret = "w0JscngvMK7FwgYvDreZjGkkULl5hNizV4oTJlRas5cRq";
		*/

		/*
		 * ReaderIni keys = new ReaderIni(); OAuthConsumer consumer = new
		 * CommonsHttpOAuthConsumer(keys.cConsumerKey, keys.cConsumerSecret);
		 * consumer.setTokenWithSecret(keys.cAccessToken, keys.cAccessSecret);
		 */
	}

}
