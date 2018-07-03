package stellarium.stellars.layer;

import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;

import com.google.common.base.Predicate;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stellarapi.api.celestials.EnumCelestialCollectionType;
import stellarapi.api.celestials.ICelestialObject;
import stellarapi.api.lib.config.IConfigHandler;
import stellarapi.api.lib.config.INBTConfig;
import stellarapi.api.lib.math.SpCoord;
import stellarium.stellars.render.ICelestialLayerRenderer;

public interface IStellarLayerType<Obj extends StellarObject, ClientConfig extends IConfigHandler, CommonConfig extends INBTConfig> {

	public void initializeClient(ClientConfig config, StellarObjectContainer<Obj> container) throws IOException;
	public void initializeCommon(CommonConfig config, StellarObjectContainer<Obj> container) throws IOException;

	public void updateLayer(StellarObjectContainer<Obj> container, double year);

	/**
	 * Gets layer renderer, which should be static.
	 * */
	@SideOnly(Side.CLIENT)
	public ICelestialLayerRenderer getLayerRenderer();

	public String getName();
	public int searchOrder();
	public boolean isBackground();
	public EnumCelestialCollectionType getCollectionType();

	public Collection<Obj> getSuns(StellarObjectContainer<Obj> container);
	public Collection<Obj> getMoons(StellarObjectContainer<Obj> container);

	/**
	 * Can be null to use default logic.
	 * */
	public Comparator<ICelestialObject> getDistanceComparator(SpCoord pos);

	/**
	 * Can be null to use default logic.
	 * */
	public Predicate<ICelestialObject> conditionInRange(SpCoord pos, double radius);
}
