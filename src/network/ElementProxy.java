package network;

import service.Constants;

public class ElementProxy {
	private String ip;
	private int port;
	private Constants.ProxyType proxyType;
	
	public ElementProxy(String ip, int port, Constants.ProxyType proxyType)
	{
		this.setIp(ip);
		this.setPort(port);
		this.setProxyType(proxyType);
	}

	public String getIp() {
		return ip;
	}

	private void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	private void setPort(int port) {
		this.port = port;
	}

	public Constants.ProxyType getProxyType() {
		return proxyType;
	}

	private void setProxyType(Constants.ProxyType proxyType) {
		this.proxyType = proxyType;
	}
}
