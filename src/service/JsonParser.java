package service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonParser {
	private String rawjson;
	static Logger logger = LoggerFactory.getLogger(JsonParser.class);

	public JsonParser(String rawjson) {
		this.rawjson = rawjson;
	}

	/**
	 * Выполняет разбор json. Возвращает значение атрибута key.
	 * 
	 * Возможны ключи : command, url, twcontent, tags, lat, lon, query
	 */
	public String GetContentProperty(String key) {
		String result = "";

		JSONObject body;
		try {
			body = new JSONObject(this.rawjson);
			if (key == "tags") {
				JSONArray tags = body.getJSONArray("tags");
				if (tags != null)
					for (int i = 0; i < tags.length(); i++) {
						result += tags.getString(i) + " ";
					}
			} else
				if (body.has(key))
					result = body.getString(key);				
		} catch (JSONException e) {
			logger.error(e.getMessage());
		}

		return result;
	}

}
