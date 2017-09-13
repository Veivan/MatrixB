package twcommands;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import model.RandomTwitContent;

import org.json.JSONObject;

import dbaware.DbConnector;
import service.Constants;
import service.TwitStripper;
import service.Utils;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import jobs.JobAtom;
import inrtfs.TwiCommand;

public class SendTwitCommand implements TwiCommand {
	private final JobAtom job;
	private final Twitter twitter;
	DbConnector dbConnector = DbConnector.getInstance();

	public SendTwitCommand(final JobAtom job, final Twitter twitter) {
		this.job = job;
		this.twitter = twitter;
	}

	@Override
	public void execute() throws Exception {
		StatusUpdate latestStatus = null;
		String twit_id = job.GetContentProperty("twit_id");
		String tags = job.GetContentProperty("tags");
		String twcontent = job.GetContentProperty("twcontent");
		String pic_id = job.GetContentProperty("pic_id");
		String fileName = "";
		String link = "";
		String listString = "";
		InputStream is = null;
		switch (twit_id) {
		case "0": // обычный твит
			if (!Utils.empty(pic_id)) {
				byte[] buf = dbConnector.getPictureByID(Integer
						.parseInt(pic_id));
				if (buf != null) {
					is = new ByteArrayInputStream(buf);
					fileName = "pic" + System.currentTimeMillis() + ".jpg";
				}
			}
			latestStatus = new StatusUpdate(twcontent + " " + tags); 
			break;
		case "1": // helpchildren
			// Получение id и картинки
			String page = Utils.GetPageContent(Constants.URL_RANDOM_SERVLET);
			JSONObject json = new JSONObject(page);
			int id = json.getInt("id");
			String pname = json.getString("name");
			String ppage = json.getString("age");
			String picenc = json.getString("picture");
			byte[] decodedBytes = Base64.getDecoder().decode(picenc.getBytes());

			twcontent = String.format("%s %s.%n", pname, ppage);
			List<String> helps = Arrays.asList("Требуется лечение.", "Вы можете помочь.", "Помогите!", "Нужна помощь!", "Help!");
			String randomHelp = helps.get(new Random().nextInt(helps.size())); 
			//String tags = "#ДобротаПодаритЖизнь #СотвориБлаго";
			link = "http://helpchildren.online/?id=" + id;
			String randomTwit = GetRandomStatusText();

			// Определение длины будущего твита без рандомной части
			List<String> predetails = Arrays.asList(twcontent, randomHelp, link, tags);		
			listString = String.join(" ", predetails);	
			int randomlen = 138 - listString.length();
			String points = "...";
			// Усечение рандомной части при необходимости
			if (randomTwit.length() > randomlen)
				randomTwit = randomTwit.substring(0, randomlen-points.length()) + points;

			List<String> details = Arrays.asList(twcontent, randomHelp, link, tags, String.format("%n%s%n", randomTwit));		
			Collections.shuffle(details);
			listString = String.join(" ", details);	

			if (decodedBytes != null) {
				is = new ByteArrayInputStream(decodedBytes);
				fileName = Integer.toString(id) + ".jpg";
			}
			latestStatus = new StatusUpdate(listString); 
		case "2": // йога
			RandomTwitContent rtc = dbConnector.getRandomContent(Integer.parseInt(twit_id));
			if (rtc == null)
				throw new Exception("getRandomContent retuns null");
			twcontent = rtc.getText();
			link = rtc.getUrl();
			List<String> details2 = Arrays.asList(twcontent, link, tags);		
			Collections.shuffle(details2);
			
			byte[] buf = rtc.getPicture();
			if (buf != null) {
				is = new ByteArrayInputStream(buf);
				fileName = "pic" + System.currentTimeMillis() + ".jpg";
			}
			listString = String.join(" ", details2);	
			latestStatus = new StatusUpdate(listString); 
			break;
		}

		if (!Utils.empty(fileName))
			// Загрузка картинки в твиттер
			latestStatus.setMedia(fileName, is);
		if (is != null)
			is.close();

		/*/ Добавление Гео
		try {
			double lat = Double.parseDouble(job.GetContentProperty("lat"));
			double lon = Double.parseDouble(job.GetContentProperty("lon"));
			latestStatus.setLocation(new GeoLocation(lat, lon));
		} catch (Exception e) {
		} */

		// Твиттинг
		Status sendedstatus = twitter.updateStatus(latestStatus);
		dbConnector.StoreStatus(sendedstatus);
	}
	
	private String GetRandomStatusText() throws TwitterException
	{
		List<String> ScreenNames = Arrays.asList("ntvru", "vesti_news", "lentaruofficial", "lifenews_ru");
		String randomScreenName = ScreenNames.get(new Random().nextInt(ScreenNames.size())); 
		List<Status> statuses = twitter.getUserTimeline(randomScreenName);
		
		TwitStripper x = new TwitStripper(statuses);
		List<String> stripped = x.GetStrippedList();
		// Saving texts 2 DB
		for (String item : stripped) {
			dbConnector.StoreRandText(item, 0, null, 1);
		} 
		String twittext = stripped.get(new Random().nextInt(stripped.size())); 
		return twittext;
	}

}
