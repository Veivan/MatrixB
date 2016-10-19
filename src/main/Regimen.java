package main;

public class Regimen {
	public int WakeHour = 7; // Begin activity
	public int BedHour = 23; // End activity
	public int Lounch = 12; // No activity
	public int Supper = 19; // No activity
	public int ActiveH = BedHour - WakeHour; // Number of active hours
}
