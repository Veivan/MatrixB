package settings;


public final class Constants {
    /**
     * Constants declarations.
     */
	public static final String CONNECTED_STATUS = "connected";                        // Describes the connection status with the WhatsApp server.
	public static final String DISCONNECTED_STATUS = "disconnected";                  // Describes the connection status with the WhatsApp server.
	public static final String UNAUTHORIZED_STATUS = "UNAUTHORIZED";                  // Describes the connection status with the WhatsApp server.
    
    public enum JobType
    {
        Tweet,
        ReTweet,
        SetAva
    }

    // Приоритеты задач и списков задач
    public static int SetAva = 0;
    public static int Tweet = 1;
    public static int ReTweet = 2;
    
    private Constants() {} // make clacc abstract
}
