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
 * Выполняет действия: - Импортит в БД один акк
 */
public class ImpSingleAcc extends Thread {
	private DbConnector dbConnector = new DbConnector();
	private cDatatype Datatype;
	private int group_id;

	private String email;
	private String pass;
	private String name;
	private String phone;
	private String mailpass;

	/**
	 * Легенда EPN - email, pass, name NPHEM - name, pass, phone, email,
	 * mailpass NPEM - name, pass, email, mailpass
	 * NP - name(email), pass
	 */
	public static enum cDatatype {
		EPN, NPHEM, NPEM, NP
	}

	static Logger logger = LoggerFactory.getLogger(AccImporter.class);

	public ImpSingleAcc(String data, cDatatype Datatype, int group_id) {
		this.Datatype = Datatype;
		this.group_id = group_id;
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
		case NP:
			email = sp[0];
			pass = sp[1];
			name = sp[0];
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
		//ElementProxy dbproxy = ProxyGetter.getProxy(user_id);
		
		//ElementProxy dbproxy = new ElementProxy("192.99.190.182", 8080, Constants.ProxyType.HTTP, -1); //Test
		//ElementProxy dbproxy = new ElementProxy("217.24.246.166", 1080, Constants.ProxyType.SOCKS, -1); //Test
		ElementProxy dbproxy = new ElementProxy("139.162.45.210", 1080, Constants.ProxyType.SOCKS, -1); //Test
								
		//String jobtp = (this.Datatype == cDatatype.EPN) ? "NEWUSERBRUT": "NEWUSER";		
		String jobtp = "NEWUSERBRUT";
		JobAtom job = new JobAtom(101L, jobtp, "");
		MatrixAct theact = new MatrixAct(job, acc);

		if (dbproxy == null) {
			String failreason = "ImpSingleAcc cant get proxy";
			logger.error(failreason);
			dbConnector.StoreActResult(theact, false, failreason);
		} else {
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
		case NP:
			acc = new ConcreteAcc(user_id, email, pass, name);
			break;
		default:
			break;
		}

		user_id = dbConnector.SaveAcc2Db(acc, group_id);
		acc.setAccID(user_id);
		return acc;
	}

}
