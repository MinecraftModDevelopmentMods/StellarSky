package stellarium.api;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.client.IRenderHandler;

public class SkyRendererSurface extends IAdaptiveRenderer {

	private IRenderHandler subRenderer;
	private IRenderHandler otherRenderer;

	public SkyRendererSurface(IRenderHandler subRenderer) {
		this.subRenderer = subRenderer;
	}

	@Override
	public IAdaptiveRenderer setReplacedRenderer(IRenderHandler handler) {
		this.otherRenderer = handler;
		return this;
	}

	@Override
	public void render(float partialTicks, WorldClient world, Minecraft mc) {
		if(otherRenderer != null) {
			otherRenderer.render(partialTicks, world, mc);
			return;
		}

		subRenderer.render(partialTicks, world, mc);
	}

}
