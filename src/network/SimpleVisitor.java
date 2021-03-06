package network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.URI;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dbaware.DbConnector;
import service.Constants;
import service.Constants.ProxyType;
import model.ElementProxy;
import model.MatrixAct;
import jobs.JobAtom;
import inrtfs.IAccount;
import inrtfs.IJobExecutor;

public class SimpleVisitor implements IJobExecutor {

	private MatrixAct act;
	private long ID;
	private JobAtom job;
	private IAccount acc;

	private String ip;
	private int port;
	private Constants.ProxyType proxyType;
	private CloseableHttpClient httpclient;
	private String failreason = "";

	public SimpleVisitor(MatrixAct theact, ElementProxy dbproxy) {
		this.act = theact;
		this.ID = theact.getSelfID();
		this.job = theact.getJob();
		this.acc = theact.getAcc();
		this.ip = dbproxy.getIp();
		this.port = dbproxy.getPort();
		this.proxyType = dbproxy.getProxyType();
	}

	static Logger logger = LoggerFactory.getLogger(SimpleVisitor.class);
	DbConnector dbConnector = DbConnector.getInstance();

	@Override
	public void Execute() {
		boolean result = false;
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
			final RequestConfig requestConfig = RequestConfig
					.custom()
					.setConnectTimeout(Constants.CONNECTION_TIMEOUT_MS)
					.setConnectionRequestTimeout(
							Constants.CONNECTION_REQUEST_TIMEOUT_MS)
					.setSocketTimeout(Constants.SOCKET_TIMEOUT_MS).build();
			this.httpclient = HttpClients.custom()
					.setDefaultRequestConfig(requestConfig).build();
		}

		try {
			HttpClientContext context = HttpClientContext.create();
			URI uri = new URIBuilder(job.GetContentProperty("url")).build();
			HttpRequestBase request = new HttpGet(uri);

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
				String message = response.getStatusLine().toString();
				logger.info("ResponseStatus: {}", message);
				result = (response.getStatusLine().getStatusCode() == 200);
				if (!result)
					failreason = message;
				/*
				 * Not used// HttpEntity httpEntity = response.getEntity();
				 * BufferedReader br = new BufferedReader(new InputStreamReader(
				 * httpEntity.getContent())); String line; while ((line =
				 * br.readLine()) != null) { if (line.isEmpty()) continue;
				 * System.out.println(line); } EntityUtils.consume(httpEntity);
				 */
			} finally {
				response.close();
			}
		} catch (Exception e) {
			logger.error("SimpleVisitor run thrown exception", e);
			failreason = e.getMessage();
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				logger.error("An httpclient.close thrown exception :", e);
			}
		}
		dbConnector.StoreActResult(this.act, result, failreason);
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
