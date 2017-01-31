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

import model.ConcreteAcc;
import model.ElementProxy;
import model.MatrixAct;
import dbaware.DbConnector;

/**
 * Выполняет действия: - Импортит в БД акки из текста
 */
public class AccImporter extends Thread {

	private DbConnector dbConnector = new DbConnector();

	private String email;
	private String pass;
	private String name;
	private String phone;
	private String mailpass;

	/**
	 * Легенда EPN - email, pass, name NPHEM - name, pass, phone, email,
	 * mailpass NPEM - name, pass, email, mailpass
	 */
	private static enum cDatatype {
		EPN, NPHEM, NPEM
	}

	// Настройка вручную
	private cDatatype Datatype = cDatatype.EPN;
	private int group_id = 4;

	static Logger logger = LoggerFactory.getLogger(AccImporter.class);

	public AccImporter(String data) {
		String[] sp = data.split(":");
		switch (Datatype) {
		case EPN:
			email = sp[0];
			pass = sp[1];
			name = sp[2];
			break;
		case NPHEM:
			name = sp[0];
			pass = sp[1];
			phone = sp[2];
			email = sp[3];
			mailpass = sp[4];
			break;
		case NPEM:
			name = sp[0];
			pass = sp[1];
			email = sp[2];
			mailpass = sp[3];
			break;
		default:
			break;
		}
	}

	@Override
	public void run() {
		ConcreteAcc acc = SaveAcc();
		long user_id = acc.getAccID();
		System.out.println(user_id);
		ElementProxy dbproxy = ProxyGetter.getProxy(user_id);
		if (dbproxy == null) {
			logger.error("AccImporter cant get proxy");
		} else {
			//String jobtp = (this.Datatype == cDatatype.EPN) ? "NEWUSERBRUT": "NEWUSER";		
			String jobtp = "NEWUSERBRUT";
			
			JobAtom job = new JobAtom(100L, jobtp, "");
			MatrixAct theact = new MatrixAct(job, acc);
			T4jClient t4wclient = new T4jClient(theact, dbproxy);
			t4wclient.Execute();
		}
	}

	private ConcreteAcc SaveAcc() {
		long user_id = -1;
		ConcreteAcc acc = null;
		switch (Datatype) {
		case EPN:
			acc = new ConcreteAcc(user_id, email, pass, name);
			break;
		case NPHEM:
			acc = new ConcreteAcc(user_id, email, pass, name, phone, mailpass);
			break;
		case NPEM:
			acc = new ConcreteAcc(user_id, email, pass, name, mailpass);
			break;
		default:
			break;
		}

		user_id = dbConnector.SaveAcc2Db(acc, group_id);
		acc.setAccID(user_id);
		return acc;
	}

	private static void DoImportAccsFromFile() throws FileNotFoundException {
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

	public static void main(String[] args) throws FileNotFoundException {
		DoImportAccsFromFile();
	}

}
