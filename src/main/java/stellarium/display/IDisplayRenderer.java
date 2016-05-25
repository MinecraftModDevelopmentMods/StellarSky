package stellarium.display;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import stellarium.stellars.render.StellarRenderInfo;

@SideOnly(Side.CLIENT)
public interface IDisplayRenderer<Cache extends IDisplayCache> {

	public void render(DisplayRenderInfo info, Cache cache);

}
