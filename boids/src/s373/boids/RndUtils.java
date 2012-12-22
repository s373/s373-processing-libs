package s373.boids;

import java.util.Random;


public final class RndUtils {
	/*
	 * basic random
	 */
	private static Random myrand = new Random();

	public static float random(float max) {
		return myrand.nextFloat() * max;
	}

	public static float random(float min, float max) {
		float dis = max - min;
		return random(dis) + min;
	}

}
