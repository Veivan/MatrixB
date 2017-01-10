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

	private byte[] profileImage = null;
	private byte[] profileImageBack = null;
	private boolean tiled = false;

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

	/** Construct for SETBACKGROUND */
	public JobAtom(long JobID, String Type, byte[] profileImageBack, boolean tiled) {
		this.JobID = JobID;
		this.Type = Constants.JobType.valueOf(Type.toUpperCase());
		this.profileImageBack = profileImageBack.clone();
		this.tiled = tiled;
	}

	/** Construct new copy */
	public JobAtom(JobAtom job) {
		this.JobID = job.JobID;		
		this.Type = job.Type; 
		this.TContent = job.TContent;
		this.timestamp = job.timestamp;

		this.name = job.name;
		this.url = job.url;
		this.location = job.location;
		this.description = job.description;

		this.profileImageBack = job.profileImageBack.clone();
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

	public byte[] getProfileImage() {
		return profileImage;
	}

	public byte[] getProfileImageBack() {
		return profileImageBack;
	}

	public boolean isTiled() {
		return tiled;
	}

}