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
        Twit,
        ReTwit,
        Like,
        Replay,
        Direct,
        Follow,
        UnFollow
    }

    // Приоритеты списков задач
    public static int ReTwit = 90;
    public static int Like = 80;
    public static int Replay = 70;
    public static int Direct = 60;
    public static int Twit = 50;

    public static int UnFollow = 40;
    public static int Follow = 30;

    public static int SetAva = 20;
    public static int SetBackgrnd = 10;


    public static DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    private Constants() {} // make clacc abstract
}
