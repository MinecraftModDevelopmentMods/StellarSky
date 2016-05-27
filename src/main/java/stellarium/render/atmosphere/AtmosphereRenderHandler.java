package stellarium.render.atmosphere;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import stellarapi.api.ICelestialCoordinate;
import stellarapi.api.ISkyEffect;
import stellarapi.api.StellarAPIReference;
import stellarapi.api.optics.IOpticalFilter;
import stellarapi.api.optics.IViewScope;
import stellarium.StellarSky;
import stellarium.view.ViewerInfo;

public class AtmosphereRenderHandler implements Runnable {
	
	private AtmosphereGLHandler glHandler;
	private AtmosphericRenderer renderer;
	private Pair<AtmosphereHolder, AtmosphereHolder> holderPair;
	private List<IAtmRenderedObjects> rendered;
	
	public AtmosphereRenderHandler() {
		this.glHandler = new AtmosphereGLHandler();
		this.renderer = new AtmosphericRenderer();
	}
	
	public void reInitialize(float deepDepth) {
		this.holderPair = renderer.initialize();
		glHandler.prepareCache(2000, 1000, 100, 200, deepDepth);
	}
	
	public void render(Minecraft mc, WorldClient theWorld, float partialTicks) {		
		holderPair.getLeft().update(mc, theWorld, partialTicks);
		holderPair.getRight().update(mc, theWorld, partialTicks);
		glHandler.renderAtmosphere(this.rendered, this.renderer);
	}

	/**
	 * Per-Tick Run.
	 * */
	@Override
	public void run() {
		float screenWidth = StellarSky.proxy.getScreenWidth();
		
		ICelestialCoordinate coordinate = StellarAPIReference.getCoordinate(StellarSky.proxy.getDefWorld());
		ISkyEffect sky = StellarAPIReference.getSkyEffect(StellarSky.proxy.getDefWorld());
		IViewScope scope = StellarAPIReference.getScope(StellarSky.proxy.getDefViewerEntity());
		IOpticalFilter filter = StellarAPIReference.getFilter(StellarSky.proxy.getDefViewerEntity());
		
		if(coordinate == null || sky == null || scope == null || filter == null)
			return;
		
		renderer.setMultipliers(new ViewerInfo(coordinate, sky, scope, filter), screenWidth);
		renderer.check(this.rendered);
	}
	
}