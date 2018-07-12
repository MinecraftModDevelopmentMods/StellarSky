package stellarium.stellars.layer;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stellarapi.api.celestials.EnumCollectionType;
import stellarapi.api.lib.config.IConfigHandler;
import stellarapi.api.lib.config.INBTConfig;
import stellarium.stellars.render.ICelestialLayerRenderer;

public class StellarLayer<S extends StellarObject, L extends IConfigHandler, M extends INBTConfig> {
	final ResourceLocation name;
	final EnumCollectionType type;
	final int searchOrder;

	public StellarLayer(ResourceLocation nameIn, EnumCollectionType typeIn, int order) {
		this.name = nameIn;
		this.type = typeIn;
		this.searchOrder = order;
	}

	public void initializeClient(L config, StellarCollection<S> container) throws IOException { }

	public void initializeCommon(M config, StellarCollection<S> container) throws IOException { }

	public void updateLayer(StellarCollection<S> container, double currentYear) { }


	/**
	 * Gets layer renderer, which should be static.
	 * */
	@SideOnly(Side.CLIENT)
	public ICelestialLayerRenderer getLayerRenderer() { return null; }

	public Collection<S> getSuns(StellarCollection<S> container) { return Collections.emptySet(); }
	public Collection<S> getMoons(StellarCollection<S> container) { return Collections.emptySet(); }

	public void initialUpdate(StellarCollection<S> container) { }
}
