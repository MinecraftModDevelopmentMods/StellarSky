package stellarium.render.celesital;

import stellarium.StellarSky;

/**
 * Enumeration for Stellar Render Pass.
 * @see #OpaqueStellar
 * @see #DeepScattering
 * @see #ShallowScattering
 * @see #OpaqueSky
 */
public enum EnumRenderPass {
	/**
	 * Opaque objects farther than the sky will be rendered here, as Pass 0.
	 * Also display which is behind everything will be rendered here.
	 * */
	OpaqueStellar,
	
	/**
	 * Scattering from farther objects, e.g. Deep sky objects and stars,
	 *  will be rendered here, as Pass 1. <p>
	 *  (Or Scattering) <p>
	 * They should be on {@link #getDeepDepth()}
	 * */
	DeepScattering,
	
	/**
	 * Scattering from objects farther than the sky will be rendered here, as Pass 2.
	 * Depth test will be disabled, so there's no need to control distance.
	 * */
	ShallowScattering;
	
	/**
	 * Gets Distance(depth) for scatters from far objects.
	 * */
	public static final double getDeepDepth() {
		int renderDistance = StellarSky.proxy.getRenderDistanceSettings();
		return 30.0 * renderDistance;
	}
	
	/**
	 * The depth that it is confirmed usable when the sky is rendered.
	 * This is always smaller than {@link #getDeepDepth()}
	 * */
	public static final double DEFAULT_OPAQUE_DEPTH = 100.0;
}