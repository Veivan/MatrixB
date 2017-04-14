package main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import model.ConcreteAcc;
import model.MatrixAct;
import network.ExecAssistant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import service.Constants;
import inrtfs.IAccount;
import jobs.JobAtom;

/**
 * Класс выполняет действия:
 * Читает тайминги из перечня аккаунтов <b>accounts</b>. 
 * При наличии задания в тайминге добавляет его во внутренний список заданий. 
 * Запускает задания на выполнение.
 */
public class Engine {
	private List<IAccount> accounts = new ArrayList<IAccount>();
	private List<MatrixAct> MatrixActList = new ArrayList<MatrixAct>();
	//ExecutorService cachedPool = Executors.newCachedThreadPool();
	ExecutorService cachedPool = Executors.newFixedThreadPool(16);

	static Logger logger = LoggerFactory.getLogger(Engine.class);

	public void ReadTimings(long moment) {
		MatrixActList.clear();
		logger.debug("Engine Read Timings");
		for (IAccount acc : accounts) {
			JobAtom job = acc.getTimedJob(moment);
			if (job != null) {
				MatrixAct act = new MatrixAct(job, acc);
				MatrixActList.add(act);
			}
		}
	}

	public void Execute() {
		for (MatrixAct act : MatrixActList) {
			cachedPool.submit(new ExecAssistant(act));
		}
	}

	public void setAccounts(List<IAccount> accounts) {
		this.accounts = accounts;
		logger.debug("Engine Accounts setting");
	}

	public void stop() {
		cachedPool.shutdown(); // shutdown the pool.
	}

	// for debug only
	public void setUserAction(int user, String actiontxt, String tcontent) {
		MatrixActList.clear();
		JobAtom job = new JobAtom(5L, actiontxt, tcontent);
		ConcreteAcc acc = new ConcreteAcc(1L);

		MatrixAct theact = new MatrixAct(job, acc);
		MatrixActList.add(theact);
		
		printMList();
	}

	private void printMList() {
		for (MatrixAct act : MatrixActList) {
			logger.info("Act at : {}",
					Constants.dfm.format(act.getJob().timestamp));
		}
	}
}
