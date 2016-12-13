package network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import inrtfs.IJobExecutor;
import service.Constants;
import service.Constants.JobType;
import service.Constants.ProxyType;
import model.ElementProxy;
import model.MatrixAct;

public class ExecAssistant extends Thread {
	
	private MatrixAct theact;
	private IJobExecutor JobExecutor;
	private ElementProxy dbproxy;

	public ExecAssistant(MatrixAct theact) {
		this.theact = theact;
	}

	static Logger logger = LoggerFactory.getLogger(ExecAssistant.class);

	private boolean GetProxy(boolean IsDebug) {
		if (IsDebug) {

			dbproxy = new ElementProxy("47.88.30.164", 1080, ProxyType.SOCKS); // Socks5

			//dbproxy = new ElementProxy("185.101.236.83", 1080, ProxyType.SOCKS); 	// Socks4

			//dbproxy = new ElementProxy("85.174.236.106", 3128, ProxyType.HTTPS);

		} else { 
			dbproxy = ProxyGetter.getProxy(this.theact.getAcc().getAccID());
			if (dbproxy == null) {
				logger.error("TWClient cant get proxy");
				logger.debug("TWClient cant get proxy");
				return false;
			}
		}
		return true;
	}
	
	@Override
	public void run() {
		
		boolean IsDebug = Constants.IsDebugProxy;
		if (!GetProxy(IsDebug))
			return;
		logger.info("TWClient got proxy {} : accID = {} ID = {}",
				IsDebug ? "Debug" : "", this.theact.getAcc().getAccID(), this.theact.getSelfID());
		
		if (this.theact.getJob().Type == JobType.VISIT) 
		{
			this.JobExecutor = new SimpleVisitor(this.theact, this.dbproxy);
		}
		else
		{
			this.JobExecutor = new T4jClient(this.theact, this.dbproxy);			
		}	
		this.JobExecutor.Execute();		
	}
	
}
