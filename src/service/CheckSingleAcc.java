package service;

import jobs.JobAtom;
import model.ConcreteAcc;
import model.ElementProxy;
import model.MatrixAct;
import network.ProxyGetter;
import network.T4jClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Выполняет действия: - Проверяет один акк на доступность
 */
public class CheckSingleAcc extends Thread {
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
		if (dbproxy == null) {
			logger.error("AccImporter cant get proxy");
		} else {
			String jobtp = "CHECKENABLED";
			JobAtom job = new JobAtom(job_id, jobtp, "");
			MatrixAct theact = new MatrixAct(job, acc);
			T4jClient t4wclient = new T4jClient(theact, dbproxy);
			t4wclient.Execute();
		}
	}

}
