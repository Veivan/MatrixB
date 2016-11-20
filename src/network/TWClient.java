package network;

import inrtfs.IAccount;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;

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
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import service.Constants;
import service.Constants.JobType;
import service.Constants.ProxyType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TWClient extends Thread {

	static String AccessToken = "2936887497-j19YUO9hyhwNREQyfABs10wdt2XlfcXwuCVFYj0";
	static String AccessSecret = "w0JscngvMK7FwgYvDreZjGkkULl5hNizV4oTJlRas5cRq";
	static String ConsumerKey = "YEgJkngnkDR7Ql3Uz5ZKkYgBU";
	static String ConsumerSecret = "CsCz7WmytpUoWqIUp9qQPRS99kMk4w9QoSH3GcStnpPc4mf1Ai";

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
		
			dbproxy = new ElementProxy("195.46.163.139", 8080, ProxyType.HTTP);
			// TWClient client = new TWClient("212.174.226.105", 48111,
			// ProxyType.SOCKS);

			// TWClient client = new TWClient("217.15.206.240", 48111,
			// ProxyType.SOCKS); // RU
			// TWClient client = new TWClient("213.79.120.82", 48111,
			// ProxyType.SOCKS); // RU

			// good
			// TWClient client = new TWClient("120.52.73.97", 80, ProxyType.HTTP);
			// bad
			//TWClient client = new TWClient("82.195.17.129", 8080, ProxyType.HTTP);

			
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

		boolean IsDebug = false;
		if (!GetProxy(IsDebug))
			return;

		logger.info("TWClient got proxy : accID = {} ID = {}",
				this.acc.getAccID(), this.ID);

		OAuthConsumer consumer = new CommonsHttpOAuthConsumer(ConsumerKey,
				ConsumerSecret);
		consumer.setTokenWithSecret(AccessToken, AccessSecret);

		if (this.proxyType == ProxyType.SOCKS) {
			// make SOCKS proxy
			Registry<ConnectionSocketFactory> reg = RegistryBuilder
					.<ConnectionSocketFactory> create()
					.register("http", new MyConnectionSocketFactory()).build();
			PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(
					reg);
			this.httpclient = HttpClients.custom().setConnectionManager(cm)
					.build();
		} else
			this.httpclient = HttpClients.custom().build();

		try {
			HttpClientContext context = HttpClientContext.create();

			URI uri = MakeURI(this.job);
			if (uri != null) {
				HttpGet request = new HttpGet(uri);

				if (this.proxyType == ProxyType.SOCKS) {
					InetSocketAddress socksaddr = new InetSocketAddress(
							this.ip, this.port);
					context.setAttribute("socks.address", socksaddr);
				} else {
					// make HTTP proxy
					HttpHost proxy = new HttpHost(this.ip, this.port, "http");
					RequestConfig config = RequestConfig.custom()
							.setProxy(proxy).build();
					request.setConfig(config);
				}
				request.setHeader("User-Agent", "MySuperUserAgent");

				// При обращении к сайту авторизация не обязательна
				if (this.job.Type != JobType.VISIT)
					consumer.sign(request);

				CloseableHttpResponse response = httpclient.execute(request,
						context);
				try {
					// System.out.println("----------------------------------------");
					String message = response.getStatusLine().toString();
					logger.info("ResponseStatus: {}", message);

					HttpEntity httpEntity = response.getEntity();
					BufferedReader br = new BufferedReader(
							new InputStreamReader(httpEntity.getContent()));
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
		JobAtom job = new JobAtom(5L, "VISIT", "http://geokot.com/reqwinfo/getreqwinfo?");
		ConcreteAcc acc = new ConcreteAcc(1L);
		MatrixAct theact = new MatrixAct(job, acc);

		TWClient client = new TWClient(theact);

		logger.info("TWClient debug main");
		client.run();
	}

	private URI MakeURI(JobAtom job) {
		URI uri = null;
		Constants.JobType jobType = job.Type;
		try {
			switch (jobType) {
			case VISIT:
				uri = new URIBuilder(job.TContent).build();

				// uri = new
				// URIBuilder("http://geokot.com/reqwinfo/getreqwinfo?")
				// uri = new URIBuilder("http://veivan.ucoz.ru").build();
				break;
			case SETAVA:
				/*
				 * uri = new URIBuilder(
				 * "https://stream.twitter.com/1.1/statuses/filter.json")
				 * .addParameter("track", "допинг").build();
				 */
				break;
			case TWIT:
				uri = new URIBuilder(
						"https://api.twitter.com/1.1/statuses/update.json")
						.addParameter("track", "допинг").build();
				break;
			case DIRECT:
				break;
			case LIKE:
				break;
			case RETWIT:
				break;
			case REPLAY:
				break;
			case SETBACKGROUND:
				break;
			case FOLLOW:
				break;
			case UNFOLLOW:
				break;
			default:
				break;
			}
		} catch (URISyntaxException e) {
			logger.error("MakeURI exception", e);
			logger.debug("MakeURI exception", e);
		}
		return uri;
	}

	static class MyConnectionSocketFactory implements ConnectionSocketFactory {

		public Socket createSocket(final HttpContext context)
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
		}

	}
	/*
	 * @Override public boolean Auth() { ReaderIni keys = new ReaderIni();
	 * OAuthConsumer consumer = new CommonsHttpOAuthConsumer(keys.cConsumerKey,
	 * keys.cConsumerSecret); consumer.setTokenWithSecret(keys.cAccessToken,
	 * keys.cAccessSecret); return false; }
	 */
}
