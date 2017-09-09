package service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.Status;

/**
 * Clean text of twit from hashtags and links
 * @return {List<String>} of stripped texts.
 */
public class TwitStripper {

	private List<String> twtexts;
	private String shablon = "(http\\S*\\s)|(http\\S*)|(#\\p{L}+\\s)|(@\\S*\\s)|(@\\S*)";
	
	public TwitStripper(final List<Status> statuses) {
		this.twtexts = new ArrayList<String>();
		for (Status stat : statuses) 
			twtexts.add(stat.getText());
	}

	public TwitStripper(List<String> statusestexts, boolean deb) {
		this.twtexts = new ArrayList<String>(statusestexts);
	}
	
	/**
	 * @return {List<String>} list of twit texts without hashtags and links
	 */
	public List<String> GetStrippedList() {
		for (int i = 0; i < twtexts.size(); i++){
			String content = twtexts.get(i);
			Pattern p = Pattern.compile(shablon);
			Matcher m = p.matcher(content);
			String twtext = m.replaceAll("");
			twtexts.set(i, twtext);
			}
		return twtexts;
	}
}
