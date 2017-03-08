package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import service.JsonParser;

public class testJsonParsser {
	/*
	 * String rawjson =
	 * "{\"menu\": { \"id\": \"file\", \"value\": \"File\", \"popup\": { \"menuitem\": [ "
	 * +
	 * "{\"value\": \"New\", \"onclick\": \"CreateNewDoc()\"}, {\"value\": \"Open\", "
	 * +
	 * "\"onclick\": \"OpenDoc()\"}, {\"value\": \"Close\", \"onclick\": \"CloseDoc()\"} ] }   }} "
	 * ;
	 */
	String rawjson = "{\"command\": \"VISIT\" , "
			+ " \"url\" : \"http://veivan.ucoz.ru\" , "
			+ " \"twcontent\" : \"Hello world\" , "
			+ " \"tags\" : [ \"#helpchildren\", \"t2\" ] , "
			+ " \"lat\" : \"55.751244\" , " + " \"lon\" : \"37.618423\" , "
			+ " \"query\" : \"#helpchildren\" " + "} ";
	// Moscow

	JsonParser parser;

	@Before
	public void setUp() throws Exception {
		parser = new JsonParser(rawjson);
	}

	@Test
	public void test() {
		String result = parser.GetContentProperty("query");
		System.out.println(result);
		assertFalse(result.isEmpty());
	}

	String VISIT = "{\"command\": \"VISIT\" , "
			+ " \"url\" : \"http://veivan.ucoz.ru\" }";
	
	String TWIT = "{\"command\": \"TWIT\" , "
			+ " \"twcontent\" : \"Hello world\" }";


}
