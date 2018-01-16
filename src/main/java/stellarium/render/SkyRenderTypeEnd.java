package stellarium.render;

import net.minecraftforge.client.IRenderHandler;
import stellarapi.api.render.IAdaptiveRenderer;
import stellarapi.api.world.worldset.WorldSet;
import stellarium.api.ISkyRenderType;

public enum SkyRenderTypeEnd implements ISkyRenderType {
	INSTANCE;

	@Override
	public String getName() {
		return "End Sky";
	}

	@Override
	public boolean acceptFor(WorldSet worldSet) {
		return true;
	}

	@Override
	public IAdaptiveRenderer createSkyRenderer(IRenderHandler celestialRenderer) {
		return new SkyRendererEnd(celestialRenderer);
	}

}
