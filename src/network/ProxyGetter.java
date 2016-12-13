package network;

import model.ElementProxy;
import dbaware.DbConnectSingle;

public class ProxyGetter {
	//TODO Временно берём прокси напрямую из ДБ
	// Далее надо сделать обращение к сервису.  
	
	public static ElementProxy getProxy(long AccID)
	{
		DbConnectSingle dbConnector = DbConnectSingle.getInstance();
		return dbConnector.getProxy(AccID);
		
	}
}
