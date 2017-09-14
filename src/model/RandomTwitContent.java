package model;

public class RandomTwitContent {
	private String text;
	private String url;
	private byte[] picture;
	private int twit_id; // Тип проекта - из DicTwType
	
	public RandomTwitContent(String text, String url, byte[] picture, int twit_id) {
		this.text = text;
		this.url = url;
		this.picture = picture;
		this.twit_id = twit_id;
	}

	public String getText() {
		return text;
	}

	public String getUrl() {
		return url == null ? "" : url;
	}

	public byte[] getPicture() {
		return picture;
	}
	
	public int getType() {
		return twit_id;
	}
}
