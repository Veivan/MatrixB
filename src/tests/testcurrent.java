package tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import twitter4j.Status;

public class testcurrent {

	private class TestThread extends Thread {
		private int index;

		public TestThread(int j) {
			index = j;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("" + index + " finished");
		}
	}

	//@Test
	public void testshuffle() {
		String pname = "Иван Иванов";
		String ppage = "10 лет";
		String twcontent = String.format("%s %s.%n", pname, ppage);
		List<String> helps = Arrays.asList("Требуется лечение.", "Вы можете помочь.", "Помогите!", "Нужна помощь!", "Help!");
		String randomHelp = helps.get(new Random().nextInt(helps.size())); 
		String tags = "#ДобротаПодаритЖизнь #СпасиРебёнка";

		int id = 2367;
		String link = "http://helpchildren.online/?id=" + id;
		
		List<String> twits = Arrays.asList("random1", "random2", "random3", "random4");
		String randomTwit = twits.get(new Random().nextInt(twits.size())); 
		
		List<String> details = new ArrayList<String>(); 
		details.add(twcontent);
		details.add(randomHelp);
		details.add(link);
		details.add(tags);
		details.add(String.format("%n%s%n", randomTwit));
		
		Collections.shuffle(details);
		//for (String stat : details) System.out.println(stat);
		String listString = String.join(" ", details);	
		System.out.println(listString);

	}

	@Test
	public void testStripTwits() {
		List<String> statuses = Arrays.asList(
			"@YuraKas1 Число пострадавших при обрушении стены кинотеатра #ff в Балашихе возросло до 10 https://t.co/nOeX5Yhkm9 https://t.co/fiQhYR5ory qq @YuraKas1",
			"Пережитое Мексикой землетрясение объявили мощнейшим за 100 лет https://t.co/LbbLjJlH7K https://t.co/WdFhjArIXb",
			"Готовьте ваши купальники. На Москву надвигается аномальная 30-градусная сентябрьская жара! Уже в выходные начнет ст… https://t.co/4aqQj9x2a7",
			"В Балашихе стена кинотеатра обрушилась на людей: есть раненые https://t.co/XZnkG3OcBW https://t.co/6j5eeIDmzN",
			"В Госдуме ответили на заявление Госдепа о политике «око за око» https://t.co/14xCUOXHyp",
			"☀️💥😱Еще один взрыв на Солнце — и тоже максимально мощный! На Земле уже бушует магнитная буря в 10 раз сильнее, чем… https://t.co/eCDMXbxMFH",
			"Брифинг Марии Захаровой: прямая трансляция https://t.co/ZH6kfkiHNn #видео",
			"«Странно, что они так долго раскачивались»: Рогозин отметил запоздалость интереса Сирии и Израиля к «Терминаторам»:… https://t.co/MLCHVGpELq",
			"Сын Трампа объяснился перед спецкомиссией, которая расследует дело о «руке России» на выборах президента США https://t.co/hx9O7B3qHy #видео",
			"Хореограф рассказал о волнении участников проекта «Ты супер! Танцы» https://t.co/zvyYepMGGp",
			"Сильнейшее за 30 лет землетрясение произошло у берегов Мексики. Качало даже фонари и памятники. Больше #видео:… https://t.co/rQuFfyrdff",
			"@YuraKas1 слишком тонко, да? Ок, в следующий раз мы вас не разочаруем! :-)",
			"Путин принял участие в церемонии закладки четырех судов на заводе «Звезда» в Приморье https://t.co/l3UWtaAxZz #видео",
			"Сегодня отмечаеться Всемирный день грамотности. #мытаквидим https://t.co/H4Fsr1njOU",
			"Минобороны: #ВКС РФ уничтожили «министра войны» #ИГИЛ https://t.co/nfeIvK7y0F https://t.co/h8MAKm2M9j",
			"Транспортный налог предложили заменить экологическим https://t.co/BilesUv3qu",
			"Путин принял участие в церемонии закладки четырех судов https://t.co/TfYAbIs9U8 https://t.co/i5nJPj9fXK",
			"В аэропорту штата Флорида произошла стрельба https://t.co/l12pxYi56r",
			"Госдеп отрицает участие #ФБР в обысках дипсобственности РФ и призывает отказаться от ответных мер… https://t.co/4rxbhZsVBZ",
			"Во Флориде о приближении урагана «Ирма» оповещают на русском языке https://t.co/zIMBkcweXC"
			);
		String shablon = "(http\\S*\\s)|(http\\S*)|(#\\p{L}+\\s)|(@\\S*\\s)|(@\\S*)";
		for (String stat : statuses) {
			//System.out.println(stat);
			Pattern p = Pattern.compile(shablon);
			Matcher m = p.matcher(stat);
			String content = m.replaceAll("");
			content = content.substring(0, Math.min(78, content.length())) + "...";
			System.out.println(content);		
			}
		String content = statuses.get(0);
/*		Pattern pattern = Pattern.compile("http.*oauth_verifier=?(.*)$",
				Pattern.CASE_INSENSITIVE);
		Matcher m = pattern.matcher(content);
		result = m.matches() ? m.group(1) : null;
*/		
		// String shablon = "#\\w+";
		//String shablon = "#\\p{L}+";
		
		
		/*/ Reading tag without #
		while (m.find()) {
			//list.add(m.group().substring(1).toLowerCase());
			//System.out.println(m.group().substring(1).toLowerCase());		
			System.out.println(content.replaceAll(m.group(), ""));		
		}
*/

	
	}
	
/*	public static void main2(String[] args) {
		testcurrent x = new testcurrent();
		
		//ExecutorService cachedPool = Executors.newCachedThreadPool();
		ExecutorService service = Executors.newFixedThreadPool(10);
		for (int j = 0; j < 20; j++) {
			service.submit(x.new TestThread(j));
		}
		service.shutdown(); 
	}
	*/

}
