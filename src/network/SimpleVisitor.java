package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;

import javax.net.ssl.SSLContext;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import service.Constants;
import service.Constants.ProxyType;
import service.Constants.RequestType;
import main.MatrixAct;
import jobs.JobAtom;
import inrtfs.IAccount;
import inrtfs.IJobExecutor;

public class SimpleVisitor implements IJobExecutor {

	private long ID;
	private JobAtom job;
	private IAccount acc;

	private String ip;
	private int port;
	private Constants.ProxyType proxyType;
	private CloseableHttpClient httpclient;

	public SimpleVisitor(MatrixAct theact, ElementProxy dbproxy) {
		this.ID = theact.getSelfID();
		this.job = theact.getJob();
		this.acc = theact.getAcc();
		this.ip = dbproxy.getIp();
		this.port = dbproxy.getPort();
		this.proxyType = dbproxy.getProxyType();
	}

	static Logger logger = LoggerFactory.getLogger(SimpleVisitor.class);

	@Override
	public void Execute() {
		logger.info("SimpleVisitor run Action : {} {} accID = {} ID = {}",
				this.job.Type.name(), Constants.dfm.format(this.job.timestamp),
				this.acc.getAccID(), this.ID);

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
					.setConnectTimeout(Constants.CONNECTION_TIMEOUT_MS)
					.setConnectionRequestTimeout(Constants.CONNECTION_REQUEST_TIMEOUT_MS)
					.setSocketTimeout(Constants.SOCKET_TIMEOUT_MS).build();
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
			request.setHeader("User-Agent", Constants.USER_AGENT);

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


}
