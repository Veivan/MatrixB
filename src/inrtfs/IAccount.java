package inrtfs;

import jobs.Homeworks;
import jobs.JobAtom;

public interface IAccount {
	int getAccID();

	/**
	 * Метод - получает из тайминга задание, соответствующее по времени
	 * <b>moment</b>.
	 */
	JobAtom getTimedJob(long moment);

	void RebuldAccTiming(Homeworks homeworks);
}
