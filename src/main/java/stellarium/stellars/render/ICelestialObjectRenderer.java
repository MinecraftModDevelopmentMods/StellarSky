package stellarium.stellars.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stellarium.render.stellars.access.EnumStellarPass;
import stellarium.render.stellars.access.IStellarTessellator;
import stellarium.render.stellars.layer.IObjRenderCache;
import stellarium.render.stellars.layer.LayerRenderInformation;
import stellarium.stellars.layer.IRenderCache;

@SideOnly(Side.CLIENT)
public interface ICelestialObjectRenderer<Cache extends IObjRenderCache> {

	public void render(Cache cache, EnumStellarPass pass, LayerRenderInformation info);

}
