package network;

import twcommands.ReadHomeTimeLineCommand;
import twcommands.SendTwitCommand;
import twitter4j.Twitter;
import twitter4j.User;
import jobs.JobAtom;
import inrtfs.TwiCommand;

/**
 * Генерация объекта Command типа TwiCommand из объекта job
 *
 */
public class CommandBuilder {
	private JobAtom job;
	private Twitter twitter;
	private User user;
	private TwiCommand intCommand = null;

	public CommandBuilder(JobAtom job, Twitter twitter, User user) {
		this.job = job;
		this.twitter = twitter;
		this.user = user;
	}

	public TwiCommand GetCommand() {
		switch (this.job.Type) {
		case TWIT:
			intCommand = new SendTwitCommand(job, twitter);
			break;
		case READHOMETIMELINE:
			intCommand = new ReadHomeTimeLineCommand(user, twitter);
			break;
		case SETAVA:
			break;
		case CHECKENABLED:
			break;
		case DIRECT:
			break;
		case FOLLOW:
			break;
		case LIKE:
			break;
		case NEWUSER:
			break;
		case NEWUSERBRUT:
			break;
		case READUSERTIMELINE:
			break;
		case REPLAY:
			break;
		case RETWIT:
			break;
		case SEARCH:
			break;
		case SETBANNER:
			break;
		case UNFOLLOW:
			break;
		case UPDATEPROFILE:
			break;
		case VISIT:
			break;
		default:
			break;
		}
		return intCommand;
	}
}
