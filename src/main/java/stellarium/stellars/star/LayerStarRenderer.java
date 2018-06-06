package stellarium.stellars.star;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import stellarium.StellarSkyResources;
import stellarium.render.stellars.access.EnumStellarPass;
import stellarium.render.stellars.layer.LayerRI;
import stellarium.render.util.FloatVertexFormats;
import stellarium.stellars.render.ICelestialLayerRenderer;

public enum LayerStarRenderer implements ICelestialLayerRenderer {
	
	INSTANCE;

	@Override
	public void preRender(EnumStellarPass pass, LayerRI info) {
		info.helper.bindTexture(StellarSkyResources.resourceStar.getLocation());
		info.helper.setup();

		info.builder.begin(GL11.GL_POINTS, FloatVertexFormats.POSITION_COLOR_F);
	}

	@Override
	public void postRender(EnumStellarPass pass, LayerRI info) {
		info.helper.setupSprite();
		info.tessellator.draw();
	}

	@Override
	public boolean acceptPass(EnumStellarPass pass) {
		return pass == EnumStellarPass.PointScatter;
	}

}
