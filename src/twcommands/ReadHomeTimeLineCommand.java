package twcommands;

import java.util.List;

import dbaware.DbConnector;
import service.Utils;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.User;
import inrtfs.TwiCommand;

public class ReadHomeTimeLineCommand implements TwiCommand {
	private User user;
	private Twitter twitter;
	DbConnector dbConnector = DbConnector.getInstance();

	public ReadHomeTimeLineCommand(User user, Twitter twitter) {
		this.user = user;
		this.twitter = twitter;
	}

	@Override
	public void execute() throws Exception {
		List<Status> statuses = null;
		if (Utils.DoItByDice()) {
			statuses = twitter.getHomeTimeline();
			System.out.println("Showing @"
					+ user.getScreenName()
					+ "'s home timeline.");
			for (Status stat : statuses) {
				System.out.println(stat.toString());
				dbConnector.StoreStatus(stat);
			}
		}
	}

}
