package stellarium.stellars.layer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;

@SideOnly(Side.CLIENT)
public interface ICelestialLayerRenderer {
	
	public void preRender(Minecraft mc, Tessellator tessellator, float bglight, float weathereff, float partialTicks);
	public void postRender(Minecraft mc, Tessellator tessellator, float bglight, float weathereff, float partialTicks);

}
