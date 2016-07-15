package tests;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class testDates {

	public static void main(String[] args) {
	    DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  

		GregorianCalendar calendar = new GregorianCalendar(1975,
				Calendar.DECEMBER, 31);
		calendar.set(1976, Calendar.FEBRUARY, 23);
		// Убедимся, что возвращает 1 - февраль
		System.out.println(calendar.get(Calendar.MONTH));


		Date d = new Date();
		long now =  System.currentTimeMillis();
		
		System.out.printf("Value: %s  %s \n", String.valueOf(now), String.valueOf(d.getTime()));
		System.out.printf("%s \n", dfm.format(d));
		
		d = new Date(now + 5000l);
		System.out.printf("%s \n", dfm.format(d));
		
		//GregorianCalendar calendar = new GregorianCalendar(year, month, day);
		Date hireDay = calendar.getTime();
		
		/*		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG,
				DateFormat.LONG, locale);

		String formattedDate = dateFormat.format(date);
*/
	}
	/*public class Timeconversion
	{
	    DateFormat dfm = new SimpleDateFormat("yyyyMMddHHmm");  

	    long unixtime;
	    public long timeConversion(String time)
	    {
	        dfm.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));//Specify your timezone 
	    try
	    {
	        unixtime = dfm.parse(time).getTime();  
	        unixtime=unixtime/1000;
	    } 
	    catch (ParseException e) 
	    {
	        e.printStackTrace();
	    }
	    return unixtime;
	    }

	}*/

}
