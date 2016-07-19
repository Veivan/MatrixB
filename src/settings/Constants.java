package settings;

import java.text.DateFormat;
import java.text.SimpleDateFormat;


/**
 * Constants declarations.
 */
public final class Constants {
    
    public enum JobType
    {
        Tweet,
        ReTweet,
        SetAva
    }

    // Приоритеты списков задач
    public static int SetAva = 0;
    public static int Tweet = 1;
    public static int ReTweet = 2;
 
    public static DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    private Constants() {} // make clacc abstract
}
