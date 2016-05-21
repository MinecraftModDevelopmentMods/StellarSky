package stellarium.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
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
	
	public SkyCelestialRenderer(ClientSettings settings, CelestialManager celManager,
			DisplayManager display, LandscapeClientSettings landscapeSettings, LandscapeCache landscapeCache) {
		this.settings = settings;
		this.renderer = new StellarRenderer();
		this.celManager = celManager;
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
		
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer worldRenderer = tessellator.getBuffer();

		GlStateManager.depthMask(true);
		
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		
		float weathereff = 1.0F - theWorld.getRainStrength(partialTicks);
		
		EnumRenderPass pass = EnumRenderPass.OpaqueStellar;
		renderer.render(new StellarRenderInfo(mc, tessellator, worldRenderer, skycolor, weathereff, partialTicks, pass), celManager.getLayers());
		
		display.render(mc, tessellator, worldRenderer, partialTicks, false);
		
		pass = EnumRenderPass.DeepScattering;
		renderer.render(new StellarRenderInfo(mc, tessellator, worldRenderer, skycolor, weathereff, partialTicks, pass), celManager.getLayers());
		
		GlStateManager.disableDepth();
		
		pass = EnumRenderPass.ShallowScattering;
		renderer.render(new StellarRenderInfo(mc, tessellator, worldRenderer, skycolor, weathereff, partialTicks, pass), celManager.getLayers());
		
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.depthMask(false);
		
		display.render(mc, tessellator, worldRenderer, partialTicks, true);
		
		GlStateManager.depthMask(true);
		GlStateManager.enableDepth();
		GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
		GlStateManager.depthMask(false);
	}
	
	@Override
	public void renderSkyLandscape(Minecraft mc, WorldClient theWorld, float partialTicks) {
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer worldRenderer = tessellator.getBuffer();
		
		GlStateManager.depthMask(true);
		if(this.landscapeCache != null)
			landscapeRenderer.render(new LandscapeRenderInfo(mc, tessellator, worldRenderer, partialTicks), this.landscapeCache);
		
		GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
		GlStateManager.depthMask(false);
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
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer worldrenderer = tessellator.getBuffer();
        
        ICelestialObject object = StellarAPIReference.getEffectors(mc.theWorld, IEffectorType.Light).getPrimarySource();
        
        GlStateManager.disableTexture2D();
        GlStateManager.shadeModel(7425);
        GlStateManager.pushMatrix();
        GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(-(float)object.getCurrentHorizontalPos().x, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
        float f6 = colors[0];
        float f7 = colors[1];
        float f8 = colors[2];

        if (mc.gameSettings.anaglyph)
        {
            float f9 = (f6 * 30.0F + f7 * 59.0F + f8 * 11.0F) / 100.0F;
            float f10 = (f6 * 30.0F + f7 * 70.0F) / 100.0F;
            float f11 = (f6 * 30.0F + f8 * 70.0F) / 100.0F;
            f6 = f9;
            f7 = f10;
            f8 = f11;
        }

        worldrenderer.begin(6, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(0.0D, 100.0D, 0.0D).color(f6, f7, f8, colors[3]).endVertex();
        int j = 16;

        for (int l = 0; l <= 16; ++l)
        {
            float f21 = (float)l * (float)Math.PI * 2.0F / 16.0F;
            float f12 = MathHelper.sin(f21);
            float f13 = MathHelper.cos(f21);
            worldrenderer.pos((double)(f12 * 120.0F), (double)(f13 * 120.0F), (double)(-f13 * 40.0F * colors[3])).color(colors[0], colors[1], colors[2], 0.0F).endVertex();
        }

        tessellator.draw();
        GlStateManager.popMatrix();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.enableTexture2D();
	}

}
