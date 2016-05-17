package stellarium.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import stellarapi.api.StellarAPIReference;
import stellarapi.api.celestials.ICelestialObject;
import stellarapi.api.celestials.IEffectorType;
import stellarium.api.ICelestialRenderer;
import stellarium.client.ClientSettings;
import stellarium.display.DisplayManager;
import stellarium.stellars.StellarManager;
import stellarium.stellars.layer.CelestialManager;
import stellarium.world.StellarDimensionManager;
import stellarium.world.landscape.LandscapeCache;
import stellarium.world.landscape.LandscapeClientSettings;
import stellarium.world.landscape.LandscapeRenderInfo;
import stellarium.world.landscape.LandscapeRenderer;

public class SkyCelestialRenderer implements ICelestialRenderer {
	
	private ClientSettings settings;
	
	private StellarRenderer renderer;
	
	private LandscapeRenderer landscapeRenderer;
	private LandscapeCache landscapeCache;
	private LandscapeClientSettings landscapeSettings;
	
	private DisplayManager display;
	
	private boolean updated = false;
	private CelestialManager celManager;
	
	public SkyCelestialRenderer(ClientSettings settings, DisplayManager display,
			LandscapeClientSettings landscapeSettings, LandscapeCache landscapeCache) {
		this.settings = settings;
		this.renderer = new StellarRenderer();
		this.celManager = StellarManager.getClientManager().getCelestialManager();
		this.display = display;
		this.landscapeRenderer = new LandscapeRenderer();
		this.landscapeCache = landscapeCache;
		this.landscapeSettings = landscapeSettings;
		
		renderer.refreshRenderer();
		settings.checkDirty();
		celManager.reloadClientSettings(this.settings);
		display.reloadClientSettings(this.settings);
		if(this.landscapeCache != null)
			landscapeCache.initialize(this.landscapeSettings);
		this.onSettingsUpdated(Minecraft.getMinecraft());
	}
	
	@Override
	public void renderCelestial(Minecraft mc, WorldClient theWorld, float[] skycolor, float partialTicks) {		
		if(settings.checkDirty()) {
			celManager.reloadClientSettings(this.settings);
			display.reloadClientSettings(this.settings);
			if(this.landscapeCache != null)
				landscapeCache.initialize(this.landscapeSettings);
			this.updated = false;
		}
		
		if(!this.updated)
			this.onSettingsUpdated(mc);
		
		Tessellator tessellator = Tessellator.instance;

		GL11.glDepthMask(true);
		
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		
		float weathereff = 1.0F - theWorld.getRainStrength(partialTicks);
		
		EnumRenderPass pass = EnumRenderPass.OpaqueStellar;
		renderer.render(new StellarRenderInfo(mc, tessellator, skycolor, weathereff, partialTicks, pass), celManager.getLayers());
		
		display.render(mc, tessellator, partialTicks, false);
		
		pass = EnumRenderPass.DeepScattering;
		renderer.render(new StellarRenderInfo(mc, tessellator, skycolor, weathereff, partialTicks, pass), celManager.getLayers());
		
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		pass = EnumRenderPass.ShallowScattering;
		renderer.render(new StellarRenderInfo(mc, tessellator, skycolor, weathereff, partialTicks, pass), celManager.getLayers());
				
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDepthMask(false);
		
		display.render(mc, tessellator, partialTicks, true);
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
	}
	
	@Override
	public void renderSkyLandscape(Minecraft mc, WorldClient theWorld, float partialTicks) {
		Tessellator tessellator = Tessellator.instance;
		
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		if(this.landscapeCache != null)
			landscapeRenderer.render(new LandscapeRenderInfo(mc, tessellator, partialTicks), this.landscapeCache);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		GL11.glDepthMask(false);
	}
	
	private void onSettingsUpdated(Minecraft mc) {
		//Initialization update
		World world = mc.theWorld;
		
		if(world != null) {
			StellarManager manager = StellarManager.getClientManager();
			if(manager.getCelestialManager() != null) {
				this.updated = true;
				manager.update(world.getWorldTime());
				StellarDimensionManager dimManager = StellarDimensionManager.get(world);
				if(dimManager != null)
					dimManager.update(world, world.getWorldTime(), world.getTotalWorldTime());
				
				manager.updateClient(this.settings);
			}
		}
	}
	

	@Override
	public void renderSunriseSunsetEffect(Minecraft mc, WorldClient theWorld, float[] colors, float partialTicks) {
        Tessellator tessellator1 = Tessellator.instance;
		
        ICelestialObject object = StellarAPIReference.getEffectors(mc.theWorld, IEffectorType.Light).getPrimarySource();
        
		GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glPushMatrix();
        GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(-(float)object.getCurrentHorizontalPos().x, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
        float f6 = colors[0];
        float f7 = colors[1];
        float f8 = colors[2];
        float f9, f10, f11;

        if (mc.gameSettings.anaglyph)
        {
            f9 = (f6 * 30.0F + f7 * 59.0F + f8 * 11.0F) / 100.0F;
            f10 = (f6 * 30.0F + f7 * 70.0F) / 100.0F;
            f11 = (f6 * 30.0F + f8 * 70.0F) / 100.0F;
            f6 = f9;
            f7 = f10;
            f8 = f11;
        }

        tessellator1.startDrawing(6);
        tessellator1.setColorRGBA_F(f6, f7, f8, colors[3]);
        tessellator1.addVertex(0.0D, 100.0D, 0.0D);
        byte b0 = 16;
        tessellator1.setColorRGBA_F(colors[0], colors[1], colors[2], 0.0F);

        for (int j = 0; j <= b0; ++j)
        {
            f11 = (float)j * (float)Math.PI * 2.0F / (float)b0;
            float f12 = MathHelper.sin(f11);
            float f13 = MathHelper.cos(f11);
            tessellator1.addVertex((double)(f12 * 120.0F), (double)(f13 * 120.0F), (double)(-f13 * 40.0F * colors[3]));
        }

        tessellator1.draw();
        GL11.glPopMatrix();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

}
