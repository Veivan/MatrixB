package jobs;

import service.Constants;

/** Класс описывает единицу задания - твит, ретвит... . */
public class JobAtom {
	public long JobID;

	/** Свойство - тип задания. */
	public Constants.JobType Type;
	
	/** Свойство - содержание задания. Например, содержание твита или ссылка для посещения */
	public String TContent;

	/**
	 * Свойство - время выполнения задания. Заполняется при размещении задания в
	 * тайминге аккаунта
	 */
	public Long timestamp = 0l;

	public boolean IsFinished = false;

	public JobAtom(long JobID, String Type, String TContent) {
		this.JobID = JobID;		
		this.Type = Constants.JobType.valueOf(Type.toUpperCase()); 
		this.TContent = TContent;
	}

	/** Construct new copy */
	public JobAtom(JobAtom job) {
		this.JobID = job.JobID;		
		this.Type = job.Type; 
		this.TContent = job.TContent;
	}

	public JobAtom(long JobID, Constants.JobType Type) {
		this.JobID = JobID;
		this.Type = Type;
	}
	
}