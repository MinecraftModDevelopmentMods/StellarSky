package stellarium.render;

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
	 *  will be rendered here, as Pass 1.
	 * They should be on distance 300.0
	 * @see #DEEP_DEPTH
	 * */
	DeepScattering,
	
	/**
	 * Scattering from objects farther than the sky will be rendered here, as Pass 2.
	 * Depth mask will be disabled, so there's no need to control distance.
	 * */
	ShallowScattering,
	
	/**
	 * Opaque objects which is nearer than the sky.
	 * Mainly used for overlays.
	 * (Depth mask is enabled again here)
	 * */
	OpaqueSky;
	
	/**
	 * Distance(depth) for scatters from far objects.
	 * */
	public static final double DEEP_DEPTH = 400.0;
}