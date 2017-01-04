package model;

import service.Constants;

public class ElementProxy {
	private String ip;
	private int port;
	private Constants.ProxyType proxyType;
	private long ProxyID; // ID in DB
	
	public ElementProxy(String ip, int port, Constants.ProxyType proxyType, long ProxyID)
	{
		this.setIp(ip);
		this.setPort(port);
		this.setProxyType(proxyType);
		this.ProxyID = ProxyID;
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

	public long getProxyID() {
		return ProxyID;
	}

	public void setProxyID(long proxyID) {
		ProxyID = proxyID;
	}
}
