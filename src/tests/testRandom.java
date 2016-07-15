package tests;

import java.util.Random;

public class testRandom {
	public static void main(String[] args) {
		
		Random random = new Random();
		for (int i = 0; i < 9; i++) {
			double tickscnt = random.nextGaussian();
			System.out.printf("Value: %s \n", String.valueOf(tickscnt));
		}
	}
}
