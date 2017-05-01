package model;

public class Regimen {
	public int WakeHour = 3; // Begin activity
	public int BedHour = 23; // End activity

	// public int Lounch = 12; // No activity
	// public int Supper = 19; // No activity
	// private int ActiveH = BedHour - WakeHour; // Number of active hours

	/**
	 * Constructor
	 * 
	 * @param groupid
	 */
	public Regimen(int WakeHour, int BedHour) {
		this.WakeHour = WakeHour;
		this.BedHour = BedHour;
	}

	/**
	 * @return Number of active hours
	 */
	public int getActiveH() {
		return BedHour - WakeHour;
	}
}
