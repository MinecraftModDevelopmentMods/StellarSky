package stellarium.stellars.layer;

import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;

import com.google.common.base.Predicate;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stellarapi.api.celestials.CelestialObject;
import stellarapi.api.celestials.EnumCollectionType;
import stellarapi.api.lib.config.IConfigHandler;
import stellarapi.api.lib.config.INBTConfig;
import stellarapi.api.lib.math.SpCoord;
import stellarium.stellars.render.ICelestialLayerRenderer;

public interface IStellarLayerType<Obj extends StellarObject, ClientConfig extends IConfigHandler, CommonConfig extends INBTConfig> {

	public void initializeClient(ClientConfig config, StellarCollection<Obj> container) throws IOException;
	public void initializeCommon(CommonConfig config, StellarCollection<Obj> container) throws IOException;

	public void updateLayer(StellarCollection<Obj> container, double year);

	/**
	 * Gets layer renderer, which should be static.
	 * */
	@SideOnly(Side.CLIENT)
	public ICelestialLayerRenderer getLayerRenderer();

	public ResourceLocation getName();
	public int searchOrder();
	public EnumCollectionType getCollectionType();

	public Collection<Obj> getSuns(StellarCollection<Obj> container);
	public Collection<Obj> getMoons(StellarCollection<Obj> container);

	/**
	 * Can be null to use default logic.
	 * */
	public Comparator<CelestialObject> getDistanceComparator(SpCoord pos);

	/**
	 * Can be null to use default logic.
	 * */
	public Predicate<CelestialObject> conditionInRange(SpCoord pos, double radius);
}
