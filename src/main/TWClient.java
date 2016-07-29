package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;

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

public class TWClient extends Thread {

	static String AccessToken = "2936887497-j19YUO9hyhwNREQyfABs10wdt2XlfcXwuCVFYj0";
	static String AccessSecret = "w0JscngvMK7FwgYvDreZjGkkULl5hNizV4oTJlRas5cRq";
	static String ConsumerKey = "YEgJkngnkDR7Ql3Uz5ZKkYgBU";
	static String ConsumerSecret = "CsCz7WmytpUoWqIUp9qQPRS99kMk4w9QoSH3GcStnpPc4mf1Ai";

	private MatrixAct act;
	private Constants.JobType jobType;

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
		this.jobType = Constants.JobType.Like;
	}

	public TWClient(MatrixAct act) {
		this.act = act;
		this.jobType = act.getJob().Type;
	}

	@Override
	public void run() {
		// System.out.printf("Action: %s \n", act.getActionTXT());

		OAuthConsumer consumer = new CommonsHttpOAuthConsumer(ConsumerKey,
				ConsumerSecret);
		consumer.setTokenWithSecret(AccessToken, AccessSecret);

/* make SOCKS proxy
         Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", new MyConnectionSocketFactory())
                .build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(reg);
        CloseableHttpClient httpclient = HttpClients.custom()
                .setConnectionManager(cm)
                .build(); */
		
		CloseableHttpClient httpclient = HttpClients.custom().build();
		
		try {
          /*  InetAddress sockshost = InetAddress.getByName("212.174.226.105");
            //sockshost = InetAddress.getByAddress(new byte[] {(byte)212, (byte)174, (byte)226, (byte)105});
            InetSocketAddress socksaddr = new InetSocketAddress(sockshost, 48111 ); */ 
            
      /*      InetSocketAddress socksaddr = new InetSocketAddress("212.174.226.105", 48111 ); */
            HttpClientContext context = HttpClientContext.create();
//            context.setAttribute("socks.address", socksaddr); 

            URI uri = MakeURI(this.jobType);
			if (uri != null) {
				HttpGet request = new HttpGet(uri);

				// make HTTP proxy
	            HttpHost proxy = new HttpHost("37.187.115.112", 80, "http"); 
	            RequestConfig config = RequestConfig.custom()
	                    .setProxy(proxy)
	                    .build();
	            request.setConfig(config);

				consumer.sign(request);
				CloseableHttpResponse response = httpclient.execute(request, context);
				//CloseableHttpResponse response = httpclient.execute(request);
				try {
					// System.out.println("----------------------------------------");
					// System.out.println(response.getStatusLine());
					String message = response.getStatusLine().toString();
					System.out.printf("ResponseStatus: %s \n", message);

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

	private URI MakeURI(Constants.JobType jobType) {
		URI uri = null;
		try {
			switch (jobType) {
			case SetAva:
				uri = new URIBuilder(
						"https://stream.twitter.com/1.1/statuses/filter.json")
						.addParameter("track", "допинг").build();
				break;
			case Twit:
				uri = new URIBuilder(
						"https://api.twitter.com/1.1/statuses/update.json")
						.addParameter("track", "допинг").build();
				break;
			case Direct:
				break;
			case Follow:
				break;
			case Like:
				uri = new URIBuilder(
						"http://geokot.com/reqwinfo/getreqwinfo?").build();
				break;
			case ReTwit:
				break;
			case Replay:
				break;
			case SetBackgrnd:
				break;
			case UnFollow:
				break;
			default:
				break;
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return uri;
	}

    static class MyConnectionSocketFactory implements ConnectionSocketFactory {

        public Socket createSocket(final HttpContext context) throws IOException {
            InetSocketAddress socksaddr = (InetSocketAddress) context.getAttribute("socks.address");
            Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksaddr);
            return new Socket(proxy);
        }

        public Socket connectSocket(
                final int connectTimeout,
                final Socket socket,
                final HttpHost host,
                final InetSocketAddress remoteAddress,
                final InetSocketAddress localAddress,
                final HttpContext context) throws IOException, ConnectTimeoutException {
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
                throw new ConnectTimeoutException(ex, host, remoteAddress.getAddress());
            }
            return sock;
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
