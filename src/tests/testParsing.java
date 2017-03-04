package tests;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

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

	public static void main(String args[]) throws Exception {
		MakeUProxy();
	}
}
