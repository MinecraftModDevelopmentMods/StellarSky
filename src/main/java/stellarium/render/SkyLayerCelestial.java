package stellarium.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Vec3;
import stellarium.StellarSky;
import stellarium.client.ClientSettings;
import stellarium.stellars.layer.CelestialManager;
import stellarium.stellars.layer.CelestialRenderer;

public class SkyLayerCelestial implements ISkyRenderLayer {

	private ClientSettings settings;
	private CelestialRenderer renderer;
	
	public SkyLayerCelestial() {
		this.settings = StellarSky.proxy.getClientSettings();
		this.renderer = new CelestialRenderer();
		
		CelestialManager manager = StellarSky.proxy.getClientCelestialManager();
		renderer.refreshRenderer(manager.getLayers());
		settings.checkDirty();
		manager.reloadClientSettings(this.settings);
	}
	
	public void renderCelestial(Minecraft mc, float bglight, float weathereff, float partialTicks) {
		CelestialManager manager = StellarSky.proxy.getClientCelestialManager();

		if(settings.checkDirty())
			manager.reloadClientSettings(this.settings);
		renderer.render(mc, Tessellator.instance, manager.getLayers(), bglight, weathereff, partialTicks);
	}
	
	@Override
	public void render(float partialTicks, WorldClient world, Minecraft mc) {
		Vec3 skyColor = world.getSkyColor(mc.renderViewEntity, partialTicks);
		float f1 = (float)skyColor.xCoord;
		float f2 = (float)skyColor.yCoord;
		float f3 = (float)skyColor.zCoord;
		
		GL11.glPushMatrix();
		float weathereff = 1.0F - world.getRainStrength(partialTicks);

		float bglight=f1+f2+f3;

		GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f); //e,n,z

		GL11.glColor4f(1.0F, 1.0F, 1.0F, weathereff);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		
		this.renderCelestial(mc, bglight, weathereff, partialTicks);

		GL11.glDisable(GL11.GL_TEXTURE_2D);


		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_FOG);
		GL11.glPopMatrix();
	}

}
