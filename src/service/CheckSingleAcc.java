package service;

import jobs.JobAtom;
import model.ConcreteAcc;
import model.ElementProxy;
import model.MatrixAct;
import network.ProxyGetter;
import network.T4jClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dbaware.DbConnector;

/**
 * Выполняет действия: - Проверяет один акк на доступность
 */
public class CheckSingleAcc extends Thread {
	private DbConnector dbConnector = new DbConnector();
	private long user_id;
	
	// Настройка вручную
	private long job_id = 9L;

	static Logger logger = LoggerFactory.getLogger(CheckSingleAcc.class);

	public CheckSingleAcc(long user_id) {
		this.user_id = user_id;
	}
	
	@Override
	public void run() {
		ConcreteAcc acc = new ConcreteAcc(user_id);
		System.out.println(user_id);
		ElementProxy dbproxy = ProxyGetter.getProxy(user_id);
		String jobtp = "CHECKENABLED";
		JobAtom job = new JobAtom(job_id, jobtp, "");
		MatrixAct theact = new MatrixAct(job, acc);
		if (dbproxy == null) {
			String failreason = "CheckSingleAcc cant get proxy";
			logger.error(failreason);
			dbConnector.StoreActResult(theact, false, failreason);
		} else {
			T4jClient t4wclient = new T4jClient(theact, dbproxy);
			t4wclient.Execute();
		}
	}

}
