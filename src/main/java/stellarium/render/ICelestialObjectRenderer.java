package stellarium.render;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ICelestialObjectRenderer<Cache extends IRenderCache> {
	
	public void render(StellarRenderInfo info, Cache cache);

}
