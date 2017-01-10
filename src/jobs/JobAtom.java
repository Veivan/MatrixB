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
	
	private String name; // ScreenName
	private String url = "";
	private String location = "Гондурас";
	private String description = "";


	/** Construct for TWIT */
	public JobAtom(long JobID, String Type, String TContent) {
		this.JobID = JobID;
		this.Type = Constants.JobType.valueOf(Type.toUpperCase());
		this.TContent = TContent;
	}

	/** Construct for UPDATEPROFILE */
	public JobAtom(long JobID, String Type, String name, String url, String location, String description ) {
		this.JobID = JobID;
		this.Type = Constants.JobType.valueOf(Type.toUpperCase());
		this.name = name;
		this.url = url;
		this.location = location;
		this.description = description;
	}

	/** Construct new copy */
	public JobAtom(JobAtom job) {
		this.JobID = job.JobID;		
		this.Type = job.Type; 
		this.TContent = job.TContent;
		this.timestamp = job.timestamp;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

	public String getLocation() {
		return location;
	}

	public String getDescription() {
		return description;
	}

}