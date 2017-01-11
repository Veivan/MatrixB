package network;

import java.net.URI;
import java.net.URISyntaxException;

import jobs.JobAtom;

import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import service.Constants;
import service.Constants.RequestType;

public class TypedURI {
	private URI uri;
	private RequestType type;

	static Logger logger = LoggerFactory.getLogger(TypedURI.class);

	public TypedURI(JobAtom job) {
		Constants.JobType jobType = job.Type;
		try {
			switch (jobType) {
			case VISIT:
				this.uri = new URIBuilder(job.TContent).build();
				this.type = RequestType.GET;
				// uri = new
				// URIBuilder("http://geokot.com/reqwinfo/getreqwinfo?")
				// uri = new URIBuilder("http://veivan.ucoz.ru").build();
				break;
			case SETAVA:
				/*
				 * uri = new URIBuilder(
				 * "https://stream.twitter.com/1.1/statuses/filter.json")
				 * .addParameter("track", "допинг").build();
				 */
				this.type = RequestType.GET;
				break;
			case TWIT:
				this.uri = new URIBuilder(
						"https://api.twitter.com/1.1/statuses/update.json")
						.addParameter("status", job.TContent).build();
				this.type = RequestType.POST;
				break;
			case DIRECT:
				break;
			case LIKE:
				break;
			case RETWIT:
				break;
			case REPLAY:
				break;
			case SETBANNER:
				break;
			case FOLLOW:
				break;
			case UNFOLLOW:
				break;
			default:
				break;
			}
		} catch (URISyntaxException e) {
			logger.error("MakeURI exception", e);
			logger.debug("MakeURI exception", e);
		}
	}

	public URI getUri() {
		return uri;
	}

	public RequestType getType() {
		return type;
	}

}
