package stellarium.stellars.layer;

import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;

import com.google.common.base.Predicate;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stellarapi.api.celestials.EnumCelestialCollectionType;
import stellarapi.api.celestials.ICelestialObject;
import stellarapi.api.lib.config.IConfigHandler;
import stellarapi.api.lib.config.INBTConfig;
import stellarapi.api.lib.math.SpCoord;
import stellarium.stellars.layer.query.ILayerTempManager;

public interface IStellarLayerType<Obj extends StellarObject, ClientConfig extends IConfigHandler, CommonConfig extends INBTConfig> {

	public void initializeClient(ClientConfig config, StellarObjectContainer<Obj, ClientConfig> container) throws IOException;
	public void initializeCommon(CommonConfig config, StellarObjectContainer<Obj, ClientConfig> container) throws IOException;

	public void updateLayer(StellarObjectContainer<Obj, ClientConfig> container, double year);

	public int getLayerRendererIndex();

	@SideOnly(Side.CLIENT)
	public void registerRenderers();

	public String getName();
	public int searchOrder();
	public boolean isBackground();
	public EnumCelestialCollectionType getCollectionType();

	public Collection<Obj> getSuns(StellarObjectContainer container);
	public Collection<Obj> getMoons(StellarObjectContainer container);

	/**
	 * Can be null to use default logic.
	 * */
	public Comparator<ICelestialObject> getDistanceComparator(SpCoord pos);

	/**
	 * Can be null to use default logic.
	 * */
	public Predicate<ICelestialObject> conditionInRange(SpCoord pos, double radius);

	/**
	 * Can be null not to support temporal loading.
	 * Should be constructed with the layer, since this has universal use.
	 * */
	public ILayerTempManager<Obj> getTempLoadManager();
}
