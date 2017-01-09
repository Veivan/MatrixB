package service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jobs.JobAtom;
import network.ProxyGetter;
import network.T4jClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.ConcreteAcc;
import model.AccIdent;
import model.ElementProxy;
import model.MatrixAct;
import dbaware.DbConnect4ImportAccsSingle;

/**
 * Выполняет действия: - Импортит в БД акки из текста
 */
public class AccImporter extends Thread {
	private DbConnect4ImportAccsSingle dbConnector = new DbConnect4ImportAccsSingle();
	private String email;
	private String pass;
	private String name;
	private String phone;
	private String mailpass;
	
	private int Datatype;

	static Logger logger = LoggerFactory.getLogger(AccImporter.class);

	public AccImporter(String data) {
		String[] sp = data.split(":");
		if (sp.length == 3) {
			email = sp[0];
			pass = sp[1];
			name = sp[2];
		} else {
			name = sp[0];
			pass = sp[1];
			phone = sp[2];
			email = sp[3];
			mailpass = sp[4];
		}
		this.Datatype = sp.length;
	}

	@Override
	public void run() {
		long user_id = SaveAcc();
		System.out.println(user_id);
		ElementProxy dbproxy = ProxyGetter.getProxy(user_id);
		if (dbproxy == null) {
			logger.error("AccImporter cant get proxy");
			logger.debug("AccImporter cant get proxy");
		} else {
			JobAtom job = new JobAtom(5L, "TWIT", "Hello!");
			ConcreteAcc acc = new ConcreteAcc(user_id);
			MatrixAct theact = new MatrixAct(job, acc);
			T4jClient t4wclient = new T4jClient(theact, dbproxy);
			t4wclient.Execute();
		}
	}

	private long SaveAcc() {
		int group_id = 1;
		long user_id = -1;
		AccIdent acc;
		if (this.Datatype == 3)
			acc = new AccIdent(user_id, email, pass, name);
		else
			acc = new AccIdent(user_id, email, pass, name, phone, mailpass);
		user_id = dbConnector.SaveAcc2Db(acc, group_id);
		return user_id;
	}

	public static void main(String[] args) throws FileNotFoundException {
		ExecutorService cachedPool = Executors.newCachedThreadPool();
		Scanner in = new Scanner(new FileReader("accs.txt"));
		while (in.hasNext()) {
			String data = in.next();
			// System.out.println (in.next());
			cachedPool.submit(new AccImporter(data));
		}
		in.close();
		cachedPool.shutdown();
	}

}
