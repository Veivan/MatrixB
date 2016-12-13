package network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import service.Constants;
import jobs.JobAtom;
import main.MatrixAct;
import inrtfs.IAccount;
import inrtfs.IJobExecutor;

public class T4jClient implements IJobExecutor {

	private long ID;
	private JobAtom job;
	private IAccount acc;

	private String ip;
	private int port;
	private Constants.ProxyType proxyType;

	public T4jClient(MatrixAct theact, ElementProxy dbproxy) {
		this.ID = theact.getSelfID();
		this.job = theact.getJob();
		this.acc = theact.getAcc();
		this.ip = dbproxy.getIp();
		this.port = dbproxy.getPort();
		this.proxyType = dbproxy.getProxyType();
	}

	static Logger logger = LoggerFactory.getLogger(T4jClient.class);

	@Override
	public void Execute() {
		// TODO Auto-generated method stub

	}

}
