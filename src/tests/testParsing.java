package tests;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class testParsing {

	private static List<String> Parse() throws Exception {
		List<String> txt = new ArrayList<String>();

		String line;
		try (InputStream fis = new FileInputStream("unsort");
				InputStreamReader isr = new InputStreamReader(fis,
						Charset.forName("UTF-8"));
				BufferedReader br = new BufferedReader(isr);) {
			while ((line = br.readLine()) != null) {
				String[] splits = line.split("\\t");
				String addr = splits[0] + ":" + splits[1];
				txt.add(addr);
				System.out.println(addr);
			}
		}

		return txt;
	}

	private static void MakeUProxy() throws Exception {
		try (FileWriter writer = new FileWriter("uproxy.txt", false)) {
			List<String> txt = Parse();
			for (int i = 0; i < txt.size(); i++) {
				writer.write(txt.get(i));
				writer.append('\n');
			}
			writer.flush();
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		}
		System.out.println("Finish");
	}

	private static List<NameValuePair> MakeChallengeParams(String url)
			throws Exception {
		List<NameValuePair> paramList = new ArrayList<NameValuePair>();
		int pos = url.indexOf("?");
		String[] sp = { url.substring(0, pos), url.substring(pos + 1) };
		paramList.add(new BasicNameValuePair("url", URLEncoder.encode(sp[0],
				"UTF-8")));
		String[] sp2 = sp[1].split("&");
		for (String record : sp2) {
			String[] sp3 = record.split("=");
			paramList.add(new BasicNameValuePair(sp3[0], URLEncoder.encode(
					sp3[1], "UTF-8")));
		}
		paramList.add(new BasicNameValuePair("challenge_response", URLEncoder
				.encode("25242875@mail.ru", "UTF-8")));
		return paramList;
	}

	private static String GetChallengeUrl(String html) throws Exception {
		String result = "";
		Document doc = Jsoup.parse(html);
		Element mBody = doc.body();
		Elements urls = mBody.getElementsByTag("a");
		for (Element url : urls) {
			// ... и вытаскиваем их название...
			System.out.println("\n" + url.attr("href"));
			result = url.attr("href");
			break;
		}
		return result;
	}

	private static String readAuthenticityToken(String html, String formname)
			throws Exception {
		Document doc = Jsoup.parse(html);
		Element mBody = doc.body();
		String result = "";
		// Login form id
		Element loginform = mBody.getElementById(formname);
		Elements inputElements = loginform.getElementsByTag("input");

		for (Element inputElement : inputElements) {
			String key = inputElement.attr("name");
			String value = inputElement.attr("value");
			if (key.equals("authenticity_token")) {
				result = value;
				break;
			}
		}
		return result;
	}

	public static void main(String args[]) throws Exception {
		// MakeUProxy();
		/*
		 * String html =
		 * "<html><body>You are being <a href=https://twitter.com/account/login_challenge?platform=web&amp;user_id=2424398037&amp;challenge_type=RetypeEmail&amp;challenge_id=kZ3H3dYz3Zt1s3yM3m1gHJst7PxAFz2W&amp;remember_me=false&amp;redirect_after_login_verification=%2F>redirected</a>.</body></html>"
		 * ; String url = GetChallengeUrl(html); List<NameValuePair> params =
		 * MakeChallengeParams(url); for (NameValuePair nameValuePair : params)
		 * { System.out.println(nameValuePair.toString()); }
		 */

		String html = new String(Files.readAllBytes(Paths.get("1.htm")));
		String formname = "login-challenge-form";
		String authenticity_token = readAuthenticityToken(html, formname);
		System.out.println(authenticity_token);
	}
}
