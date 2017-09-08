package twcommands;

import java.util.List;

import dbaware.DbConnector;
import jobs.JobAtom;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.User;
import inrtfs.TwiCommand;

public class ReadUserTimeLineCommand implements TwiCommand {
	private final Twitter twitter;
	private final String ScreenName; // юзер, чью ленту будем читать

	DbConnector dbConnector = DbConnector.getInstance();

	public ReadUserTimeLineCommand(final JobAtom job, final User user, final Twitter twitter) {
		this.twitter = twitter;
		String scr = job.getName();
		this.ScreenName = scr.isEmpty() ? user.getScreenName() : scr;
	}

	@Override
	public void execute() throws Exception {
		List<Status> statuses = null;
		statuses = twitter.getUserTimeline(this.ScreenName);
		System.out.println("Showing @"
					+ this.ScreenName
					+ "'s user timeline.");
		for (Status stat : statuses) {
			System.out.println(stat.getCreatedAt() + " @"
					+ stat.getUser().getScreenName() + " - "
					+ stat.getText());
			//dbConnector.StoreStatus(stat);
		}
	}

}
