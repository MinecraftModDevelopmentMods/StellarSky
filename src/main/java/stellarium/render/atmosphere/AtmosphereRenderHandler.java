package stellarium.render.atmosphere;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;

public class AtmosphereRenderHandler implements Runnable {
	
	private AtmosphereGLHandler glHandler;
	private AtmosphericRenderer renderer;
	private Pair<AtmosphereHolder, AtmosphereHolder> holderPair;
	
	public AtmosphereRenderHandler() {
		this.glHandler = new AtmosphereGLHandler();
		this.renderer = new AtmosphericRenderer();
	}
	
	public void reInitialize(List<IAtmRenderedObjects> rendered, float deepDepth) {
		this.holderPair = renderer.initialize(rendered);
		glHandler.prepareCache(2000, 1000, 100, 200, deepDepth);
	}
	
	public void render(Minecraft mc, WorldClient theWorld, float partialTicks) {
		holderPair.getLeft().update(mc, theWorld, partialTicks);
		holderPair.getRight().update(mc, theWorld, partialTicks);
		glHandler.renderAtmosphere(this.renderer);
	}

	/**
	 * Per-Tick Run.
	 * */
	@Override
	public void run() {
		renderer.check();
	}
	
}