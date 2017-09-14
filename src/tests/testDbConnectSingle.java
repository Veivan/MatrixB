package tests;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import model.ElementProxy;
import model.RandomTwitContent;
import model.TwFriend;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import service.GenderChecker.Gender;
import dbaware.DbConnector;

public class testDbConnectSingle {
	DbConnector dbConnector;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		dbConnector = DbConnector.getInstance();
	}

	@Test
	public void testgetRandomContente() throws Exception {
		int twit_id = 2;
		RandomTwitContent content = dbConnector.getRandomContent(twit_id);
		System.out.println(content.getText());
		System.out.println(content.getUrl());
		
		/*String tags = "#dd";
		List<String> details2 = Arrays.asList(content.getText(), content.getUrl(), tags);	
		Predicate<String> itemPredicate = p-> p == null;     
		details2.removeIf(itemPredicate);
		Collections.shuffle(details2);
		String listString = String.join(" ", details2);	
		System.out.println(listString);*/

		assertFalse(content == null);
	}
	
	//@Test
	public void testgetRandomPicture() throws Exception {
		Gender gender = Gender.FEMALE;
		int ptype_id = 2;
		byte[] bytes = dbConnector.getRandomPicture(gender, ptype_id);
		
		String filename =  "D:/temp/13.jpg";
        File file = new File(filename);
        FileOutputStream fos = new FileOutputStream(file);
        if (bytes != null)
        	fos.write(bytes);
        fos.close();
		//System.out.println(bytes);
		assertFalse(bytes.length == 0);
	}

	//@Test
	public void testGetFreeProxies() {
		List<ElementProxy> proxylist = dbConnector.getFreeProxies(130L);
		assertFalse(proxylist.size() == 0);
	}

	//@Test
	public void testGetProxy4Acc() {
		int AccID = 1;
		ElementProxy accproxy = dbConnector.getProxy4Acc(AccID);
		assertNotNull(accproxy); 		
	}

	//@Test
	public void testGetRandomScreenName() {
		int AccID = 130;
		TwFriend friend = dbConnector.GetRandomScreenName(AccID);
		assertNotNull(friend); 		
	}


}
