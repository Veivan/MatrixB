package network;

import inrtfs.IJobExecutor;
import service.Constants.JobType;
import main.MatrixAct;

public class ExecAssistant extends Thread {
	
	private MatrixAct theact;
	private IJobExecutor JobExecutor;

	public ExecAssistant(MatrixAct theact) {
		this.theact = theact;
	}

	@Override
	public void run() {
		if (this.theact.getJob().Type == JobType.VISIT) 
		{
			this.JobExecutor = new SimpleVisitor();
		}
		else
		{
			this.JobExecutor = new T4jClient();			
		}	
		this.JobExecutor.Execute();		
	}
	
}
