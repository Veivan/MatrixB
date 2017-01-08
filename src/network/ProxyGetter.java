package network;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;

import service.Constants;
import model.ElementProxy;
import dbaware.DbConnectSingle;

public class ProxyGetter {
	
	/**
	 * Первичное получение прокси для акка из БД
	 */
	public static ElementProxy getProxy(long AccID)
	{
		DbConnectSingle dbConnector = DbConnectSingle.getInstance();
		ElementProxy accproxy = dbConnector.getProxy4Acc(AccID);
		if (!CheckProxy(accproxy))
		{
			if (accproxy != null) {
				dbConnector.setProxyIsAlive(accproxy.getProxyID(), false);
				accproxy = null;
			}

			List<ElementProxy> proxylist = dbConnector.getFreeProxies();
			
			for (ElementProxy proxy : proxylist) {
				if (CheckProxy(proxy))
				{
					accproxy = proxy;
					break;
				}	
				else
					dbConnector.setProxyIsAlive(proxy.getProxyID(), false);
			}
			
			// Refresh proxy 4 account
			dbConnector.setProxy4Acc(AccID, accproxy);			
		}
		
		return accproxy;		
	}

	/**
	 * Получение другого прокси для акка из БД, в случае если использование существующего приводит к ошибке
	 */
	public static ElementProxy getAnotherProxy(long AccID)
	{
		DbConnectSingle dbConnector = DbConnectSingle.getInstance();
		ElementProxy accproxy = dbConnector.getProxy4Acc(AccID);
		// Баним старый прокси
		if (accproxy != null) {
			dbConnector.setProxyIsAlive(accproxy.getProxyID(), false);
			accproxy = null;
		}
		// Ищем другой свободный прокси
		List<ElementProxy> proxylist = dbConnector.getFreeProxies();
		for (ElementProxy proxy : proxylist) {
			if (CheckProxy(proxy)) {
				accproxy = proxy;
				break;
			} else
				dbConnector.setProxyIsAlive(proxy.getProxyID(), false);
		}

		// Refresh proxy 4 account
		dbConnector.setProxy4Acc(AccID, accproxy);
		
		return accproxy;		
	}

	private static boolean CheckProxy(ElementProxy proxy) {
		if (proxy == null) return false;
		
		String pHost = proxy.getIp();
		int pPort = proxy.getPort();
		SocketAddress addr = new InetSocketAddress(pHost, pPort);
		Proxy.Type _pType = ( proxy.getProxyType() == Constants.ProxyType.HTTP ? Proxy.Type.HTTP
				: Proxy.Type.SOCKS);
		Proxy httpProxy = new Proxy(_pType, addr);
		HttpURLConnection urlConn = null;
		URL url;
		try {
			url = new URL(Constants.testLink);
			urlConn = (HttpURLConnection) url.openConnection(httpProxy);
			urlConn.setConnectTimeout(Constants.prxchcktimeout);
			urlConn.connect();
			return (urlConn.getResponseCode() == 200);
		} catch (SocketException e) {
			return false;
		} catch (SocketTimeoutException e) {
			return false;
		} catch (Exception e) {
			System.out.print("Error: " + e);
			return false;
		}
	}
}
