package service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;


/**
 * Constants declarations.
 */
public final class Constants {
    
    public enum JobType
    {
        SetAva,
        SetBackgrnd,
        Tweet,
        ReTweet,
        Like,
        Replay,
        Direct,
        Follow,
        UnFollow
    }

    // Приоритеты списков задач
    public static int ReTweet = 90;
    public static int Like = 80;
    public static int Replay = 70;
    public static int Direct = 60;
    public static int Tweet = 50;

    public static int UnFollow = 40;
    public static int Follow = 30;

    public static int SetAva = 20;
    public static int SetBackgrnd = 10;


    public static DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    private Constants() {} // make clacc abstract
}
