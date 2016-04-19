package stellarium.stellars.milkyway;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stellarapi.api.celestials.EnumCelestialCollectionType;
import stellarapi.api.celestials.ICelestialObject;
import stellarapi.api.lib.config.IConfigHandler;
import stellarapi.api.lib.config.INBTConfig;
import stellarapi.api.lib.math.SpCoord;
import stellarium.render.CelestialRenderingRegistry;
import stellarium.stellars.layer.CelestialObject;
import stellarium.stellars.layer.ICelestialLayer;

public class LayerMilkyway implements ICelestialLayer<INBTConfig, IConfigHandler> {
	
	public static int milkywayRenderId;
	private Milkyway milkyway;

	@Override
	public void initializeClient(boolean isRemote, IConfigHandler config) throws IOException {
		this.milkyway = new Milkyway(isRemote);
	}
	
	@Override
	public void initializeCommon(boolean isRemote, INBTConfig config) throws IOException {
		if(this.milkyway == null)
			this.milkyway = new Milkyway(isRemote);
	}

	@Override
	public void updateLayer(double year) { }

	@Override
	public List<? extends CelestialObject> getObjectList() {
		return Lists.newArrayList(this.milkyway);
	}

	@Override
	public int getLayerRendererIndex() {
		return -1;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerRenderers() {
		milkywayRenderId = CelestialRenderingRegistry.getInstance().registerObjectRenderer(new MilkywayRenderer());
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<ICelestialObject> getObjects() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<ICelestialObject> getObjectInRange(SpCoord pos, double radius) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICelestialObject getNearerObject(SpCoord pos, ICelestialObject obj1, ICelestialObject obj2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int searchOrder() {
		return 2;
	}

	@Override
	public boolean isBackground() {
		return true;
	}

	@Override
	public EnumCelestialCollectionType getCollectionType() {
		// TODO Auto-generated method stub
		return null;
	}


}
