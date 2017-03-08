package jobs;

import java.util.Comparator;

import service.Constants;
import service.JsonParser;
import service.Constants.JobType;

/** Класс описывает единицу задания - твит, ретвит... . */
public class JobAtom {
	public long JobID;

	/** Свойство - тип задания. */
	public Constants.JobType Type;

	/**
	 * Свойство - содержание задания. Например, содержание твита или ссылка для
	 * посещения в формате json
	 */
	private String TContent;
	private JsonParser parser;

	/**
	 * Свойство - ID группы, которой назначено задание. Если = 0, значит задание
	 * относится ко всем группам.
	 */
	public int group_id = 0;	

	/**
	 * Свойство - время выполнения задания. Заполняется при размещении задания в
	 * тайминге аккаунта
	 */
	public Long timestamp = 0l;

	public boolean IsFinished = false;

	/** for UPDATEPROFILE only */
	private String name;
	private String url = "";
	private String location = "Гондурас";
	private String description = "";

	private byte[] profileImage = null;
	private byte[] profileBanner = null;

	/** Construct for TWIT */
	public JobAtom(long JobID, String Type, String TContent) {
		this.JobID = JobID;
		this.Type = Constants.JobType.valueOf(Type.toUpperCase());
		this.TContent = TContent;
		this.parser = new JsonParser(TContent);
	}

	/** Construct for UPDATEPROFILE */
	public JobAtom(long JobID, String Type, String name, String url,
			String location, String description) {
		this.JobID = JobID;
		this.Type = Constants.JobType.valueOf(Type.toUpperCase());
		this.name = name;
		this.url = url;
		this.location = location;
		this.description = description;
	}

	/** Construct for SETAVA and SETBANNER */
	public JobAtom(long JobID, String Type, byte[] image) {
		this.JobID = JobID;
		this.Type = JobType.valueOf(Type.toUpperCase());
		if (this.Type == JobType.SETAVA)
			this.profileImage = image.clone();
		else
			this.profileBanner = image.clone();
	}

	/** Construct new copy */
	public JobAtom(JobAtom job) {
		this.JobID = job.JobID;
		this.Type = job.Type;
		this.TContent = job.TContent;
		this.group_id = job.group_id;
		this.timestamp = job.timestamp;
		this.parser = new JsonParser(job.TContent);

		this.name = job.name;
		this.url = job.url;
		this.location = job.location;
		this.description = job.description;

		if (job.profileImage != null)
			this.profileImage = job.profileImage.clone();
		if (job.profileBanner != null)
			this.profileBanner = job.profileBanner.clone();
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

	public byte[] getProfileBanner() {
		return profileBanner;
	}

	public static Comparator<JobAtom> JobAtomComparatorByID = new Comparator<JobAtom>() {
		@Override
		public int compare(JobAtom s1, JobAtom s2) {
			long p1 = s1.JobID;
			long p2 = s2.JobID;
			return p1 > p2 ? 1 : p1 == p2 ? 0 : -1;
		}
	};
	
	/**
	 * Выполняет разбор json из TContent.
	 * Возвращает значение атрибута key.
	 */
	public String GetContentProperty(String key)
	{
		String result = this.parser.GetContentProperty(key);		
		return result;
	}

}