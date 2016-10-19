package jobs;

import service.Constants;

/** Класс описывает единицу задания - твит, ретвит... .
*/
public class JobAtom {
	public int JobID;

	/** Свойство - тип задания.*/
	public Constants.JobType Type;

	/** Свойство - время выполнения задания. Заполняется при размещении задания в тайминге аккаунта */
	public Long timestamp = 0l;
	
	public boolean IsFinished = false;
	
	public JobAtom(int JobID, Constants.JobType Type)
	{
		this.JobID = JobID;
		this.Type = Type;
	}
}
