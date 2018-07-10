package stellarium.api;

import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stellarapi.api.SAPIReferences;
import stellarapi.api.render.IAdaptiveRenderer;
import stellarapi.api.world.worldset.WorldSet;
import stellarapi.api.world.worldset.WorldSets;
import stellarium.render.adapt.SkyRendererSurface;

public enum SkyRenderTypeSurface implements ISkyRenderType {
	INSTANCE;

	@Override
	public String getName() {
		return "Overworld Sky";
	}

	@Override
	public boolean acceptFor(WorldSet worldSet) {
		return worldSet != WorldSets.endType();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IAdaptiveRenderer createSkyRenderer(IRenderHandler subRenderer) {
		return new SkyRendererSurface(subRenderer);
	}

}
