package stellarium.stellars.milkyway;

import java.io.IOException;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stellarium.config.IConfigHandler;
import stellarium.stellars.layer.CelestialObject;
import stellarium.stellars.layer.CelestialRenderingRegistry;
import stellarium.stellars.layer.ICelestialLayer;

public class LayerMilkyway implements ICelestialLayer<IConfigHandler> {
	
	public static int milkywayRenderId;
	private Milkyway milkyway;

	@Override
	public void initialize(boolean isRemote, IConfigHandler config) throws IOException {
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


}
