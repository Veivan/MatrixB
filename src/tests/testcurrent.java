package tests;

import java.net.Proxy;

public class testcurrent {

	public static void main(String[] args) {
		String ProxyHost = "SOCKS192.7.1.56";
		String prop = "SOCKS";
		if (ProxyHost.contains(prop)){
			ProxyHost = ProxyHost.replace(prop, "");
		}
		System.out.println(ProxyHost);
		Proxy.Type prType = Proxy.Type.HTTP;
		System.out.println(prType.toString());
	}

}
