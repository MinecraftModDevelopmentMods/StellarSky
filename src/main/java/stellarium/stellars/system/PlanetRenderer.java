package stellarium.stellars.system;

import org.lwjgl.opengl.GL11;

import stellarium.render.stellars.CRenderHelper;
import stellarium.render.stellars.access.EnumStellarPass;
import stellarium.render.stellars.layer.LayerRI;
import stellarium.render.util.FloatVertexFormats;
import stellarium.stellars.render.ICelestialObjectRenderer;

public enum PlanetRenderer implements ICelestialObjectRenderer<PlanetRenderCache> {
	
	INSTANCE;
	
	@Override
	public void render(PlanetRenderCache cache, EnumStellarPass pass, LayerRI info) {
		if(pass != EnumStellarPass.OpaqueScatter || !cache.shouldRender)
			return;

		CRenderHelper helper = info.helper;

		helper.setup();
		info.builder.begin(GL11.GL_POINTS, FloatVertexFormats.POSITION_COLOR_F);
		info.builder.pos(cache.pos, info.deepDepth);
		info.builder.color(cache.brightness * helper.multRed(),
				cache.brightness * helper.multGreen(),
				cache.brightness * helper.multBlue(), 1.0f);
		info.builder.endVertex();
		helper.radius(cache.size);

		helper.setupSprite();
		info.tessellator.draw();
	}

}
