package stellarium.render.atmosphere;

import stellarium.render.shader.IShaderObject;

public interface IPhasedRenderer {

	public static String dominationMapField = "skyDominationMap";
	public static String skyBrightnessField = "skyBrightness";
	public static String defaultTexture = "texture";

	public int numberDominators();
	public void setupDominateShader(int dominatorIndex, boolean asMap);

	public IShaderObject setupShader(boolean forDegradeMap, boolean forOpaque, boolean hasTexture);

	/**
	 * Rendering opaque object without texture means that its rendering scatter from the object.
	 * */
	public void render(boolean forOpaque, boolean hasTexture);

	public double skyBrightness();

}
