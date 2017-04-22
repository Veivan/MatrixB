package twcommands;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

import org.json.JSONObject;

import dbaware.DbConnector;
import service.Constants;
import service.Utils;
import twitter4j.GeoLocation;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import jobs.JobAtom;
import inrtfs.TwiCommand;

public class SendTwitCommand implements TwiCommand {
	private JobAtom job;
	private Twitter twitter;
	StatusUpdate latestStatus = null;
	DbConnector dbConnector = DbConnector.getInstance();

	public SendTwitCommand(JobAtom job, Twitter twitter) {
		this.job = job;
		this.twitter = twitter;
	}

	@Override
	public void execute() throws Exception {
		String tags = job.GetContentProperty("tags");
		if (tags.contains("#helpchildren")) {
			// Получение id и картинки
			String page = Utils.GetPageContent(Constants.URL_RANDOM_SERVLET);
			JSONObject json = new JSONObject(page);
			int id = json.getInt("id");
			String pname = json.getString("name");
			String ppage = json.getString("age");
			String picenc = json.getString("picture");
			byte[] decodedBytes = Base64.getDecoder().decode(picenc.getBytes());

			// Формирование Статуса
			InputStream is = new ByteArrayInputStream(decodedBytes);
			String fileName = Integer.toString(id) + ".jpg";

			String message = String.format(
					"%s %s. Требуется лечение, Вы можете помочь.%n", pname,
					ppage)
					+ "http://helpchildren.online/?id=" + id + " " + tags; // +
																			// "#Дети"
																			// +
																			// " #ПодариЖизнь";
			latestStatus = new StatusUpdate(message);
			// Загрузка картинки в твиттер
			latestStatus.setMedia(fileName, is);
		} else {
			latestStatus = new StatusUpdate(job.GetContentProperty("twcontent"));
		}

		// Добавление Гео
		try {
			double lat = Double.parseDouble(job.GetContentProperty("lat"));
			double lon = Double.parseDouble(job.GetContentProperty("lon"));
			latestStatus.setLocation(new GeoLocation(lat, lon));
		} catch (Exception e) {
		}
		// Твиттинг
		Status sendedstatus = twitter.updateStatus(latestStatus);
		dbConnector.StoreStatus(sendedstatus);
		
	}

}
