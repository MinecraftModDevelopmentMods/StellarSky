package stellarium.stellars.milkyway;

import java.io.IOException;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stellarapi.api.celestials.EnumCollectionType;
import stellarapi.api.lib.config.IConfigHandler;
import stellarapi.api.lib.config.INBTConfig;
import stellarium.StellarSkyReferences;
import stellarium.stellars.layer.StellarCollection;
import stellarium.stellars.layer.StellarLayer;
import stellarium.stellars.render.ICelestialLayerRenderer;

public class LayerMilkyway extends StellarLayer<Milkyway, IConfigHandler, INBTConfig> {
	public LayerMilkyway() {
		super(new ResourceLocation(StellarSkyReferences.MODID, "milkyway"),
				EnumCollectionType.DeepSkyObjects, 3);
	}

	@Override
	public void initializeCommon(INBTConfig config, StellarCollection<Milkyway> container) throws IOException {
		Milkyway milkyway = new Milkyway();
		container.loadObject("Milkyway", milkyway);
		container.addRenderCache(milkyway, new MilkywayRenderCache());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ICelestialLayerRenderer getLayerRenderer() {
		return MilkywayLayerRenderer.INSTANCE;
	}
}
