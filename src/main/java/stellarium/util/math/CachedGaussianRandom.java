package stellarium.util.math;

import java.util.Random;

public class CachedGaussianRandom {
	
	private double[] randomValues;
	private int currentIndex = 0;
	
	public CachedGaussianRandom(int numRandoms) {
		Random random = new Random();
		this.generateRandomValues(numRandoms, random);
	}
	
	public CachedGaussianRandom(int numRandoms, long seed) {
		Random random = new Random(seed);
		this.generateRandomValues(numRandoms, random);
	}
	
	private void generateRandomValues(int numRandoms, Random random) {
		this.randomValues = new double[numRandoms];
		for(int i = 0; i < numRandoms; i++)
			randomValues[i] = random.nextGaussian();
	}

	public double nextGaussian() {
		this.currentIndex = (this.currentIndex + 1) % randomValues.length;
		return randomValues[this.currentIndex++];
	}

}
