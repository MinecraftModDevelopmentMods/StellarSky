package stellarium.render.stellars.access;

import stellarapi.api.lib.math.Vector3;

public interface IDominateRenderer {
	public void renderDominate(Vector3 lightDir, float red, float green, float blue);
}
