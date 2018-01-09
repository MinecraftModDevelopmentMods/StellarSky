package stellarium.util.math;

import stellarium.StellarSky;

// Power Cache, for float
public class PowerCache {
	
	private static final int EXPONENT_NUM = Float.MAX_EXPONENT - Float.MIN_EXPONENT + 3;
	private static final int MANTISSA_CACHE_NUM = 0x100;
	private static final int MANTISSA_CHECK = 0xff;
	private static final int BITS_TO_INSPECT = 8;

	private float[] exponentCache = new float[EXPONENT_NUM];
	private float[] mantissaCache = new float[MANTISSA_CACHE_NUM];

	public void initialize(float power) {
		int cnt = 0;
		for(int i = Float.MIN_EXPONENT - 1; i <= Float.MAX_EXPONENT + 1; i++)
			this.exponentCache[cnt++] = (float) Math.pow(2.0, i * power);

		cnt = 0;
		for(int i = MANTISSA_CACHE_NUM; i < 2 * MANTISSA_CACHE_NUM; i++)
			this.mantissaCache[cnt++] = (float) Math.pow(Math.scalb(i, -BITS_TO_INSPECT), power);

		StellarSky.INSTANCE.getLogger().info("Initialized Power Cache.");
		StellarSky.INSTANCE.getLogger().info(String.format("Test #1: cached %e compared with expected %e", this.getPower(1.0f), Math.pow(1.0, power)));
		StellarSky.INSTANCE.getLogger().info(String.format("Test #2: cached %e compared with expected %e", this.getPower(2.512f), Math.pow(2.512, power)));
		StellarSky.INSTANCE.getLogger().info(String.format("Test #3: cached %e compared with expected %e", this.getPower(2512.0f), Math.pow(2512.0, power)));
	}

	public float getPower(float base) {
		int exponent = Math.getExponent(base);
		int bits = Float.floatToRawIntBits(base);
		bits = (bits >> (23 - BITS_TO_INSPECT)) & MANTISSA_CHECK;
		return this.exponentCache[exponent - Float.MIN_EXPONENT + 1] * this.mantissaCache[bits];
	}

}