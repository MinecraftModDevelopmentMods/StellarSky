package stellarium.stellars.system;

import org.lwjgl.opengl.GL11;

import stellarium.render.stellars.CRenderHelper;
import stellarium.render.stellars.access.EnumStellarPass;
import stellarium.render.stellars.layer.LayerRI;
import stellarium.render.util.FloatVertexFormats;
import stellarium.stellars.OpticsHelper;
import stellarium.stellars.render.ICelestialObjectRenderer;

public enum PlanetRenderer implements ICelestialObjectRenderer<PlanetRenderCache> {

	INSTANCE;

	@Override
	public void render(PlanetRenderCache cache, EnumStellarPass pass, LayerRI info) {
		if(pass != EnumStellarPass.OpaqueScatter || !cache.shouldRender)
			return;

		// TODO Render fuzzy shape, for now it just render points
		float multiplier = OpticsHelper.getMultFromArea(info.pointArea());

		info.beginPoint();
		info.renderPoint(cache.pos,
				cache.brightness * multiplier, cache.brightness * multiplier, cache.brightness * multiplier);
		info.endPoint();
	}

}
