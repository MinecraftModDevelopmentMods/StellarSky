package stellarium.stellars.render;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stellarium.render.stellars.access.EnumStellarPass;
import stellarium.render.stellars.layer.IObjRenderCache;
import stellarium.render.stellars.layer.LayerRI;

@SideOnly(Side.CLIENT)
public interface ICelestialObjectRenderer<Cache extends IObjRenderCache> {

	public void render(Cache cache, EnumStellarPass pass, LayerRI info);

}
