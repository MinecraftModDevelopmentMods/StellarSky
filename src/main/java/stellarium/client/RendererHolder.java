package stellarium.client;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import stellarium.api.IAdaptiveRenderer;
import stellarium.api.IRendererHolder;
import stellarium.api.StellarSkyAPI;

public class RendererHolder implements ICapabilityProvider, IRendererHolder {

	private IAdaptiveRenderer renderer = null;

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == StellarSkyAPI.SKY_RENDER_HOLDER;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if(capability == StellarSkyAPI.SKY_RENDER_HOLDER)
			return StellarSkyAPI.SKY_RENDER_HOLDER.cast(this);
		else return null;
	}

	@Override
	public void setRenderer(IAdaptiveRenderer renderer) {
		this.renderer = renderer;
	}

	@Override
	public IAdaptiveRenderer getRenderer() {
		return this.renderer;
	}

}
