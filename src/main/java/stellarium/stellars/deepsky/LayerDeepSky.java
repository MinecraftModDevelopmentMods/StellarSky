package stellarium.stellars.deepsky;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stellarapi.api.celestials.EnumCelestialCollectionType;
import stellarapi.api.celestials.ICelestialObject;
import stellarapi.api.lib.config.IConfigHandler;
import stellarapi.api.lib.config.INBTConfig;
import stellarapi.api.lib.math.SpCoord;
import stellarium.StellarSky;
import stellarium.stellars.layer.IStellarLayerType;
import stellarium.stellars.layer.StellarObjectContainer;
import stellarium.stellars.layer.query.ILayerTempManager;
import stellarium.stellars.render.ICelestialLayerRenderer;

public class LayerDeepSky implements IStellarLayerType<DeepSkyObject, IConfigHandler, INBTConfig> {

	private List<DeepSkyObject> deepSkyObjects = Lists.newArrayList();
	
	@Override
	public void initializeClient(IConfigHandler config, StellarObjectContainer<DeepSkyObject, IConfigHandler> container)
			throws IOException {
		this.loadMessierData();
	}

	@Override
	public void initializeCommon(INBTConfig config, StellarObjectContainer<DeepSkyObject, IConfigHandler> container)
			throws IOException {
		// TODO Auto-generated method stub
		
	}
	
	private static final String messierParent = "/assets/stellarium/deepsky/messier/";
	private Gson gson = new Gson();
	private JsonParser parser = new JsonParser();
	
	@SideOnly(Side.CLIENT)
	private void loadMessierData() throws IOException {
		StellarSky.logger.info("Loading Messier Objects...");
		
		InputStream inp = LayerDeepSky.class.getResourceAsStream(messierParent + "messier.json");
		String[] read = gson.fromJson(new BufferedReader(new InputStreamReader(inp)), String[].class);
		for(String objectInfo : read) {
			InputStream objectInp = LayerDeepSky.class.getResourceAsStream(messierParent + objectInfo + ".json");
			JsonElement parsed = parser.parse(new BufferedReader(new InputStreamReader(objectInp)));
			JsonObject object = parsed.getAsJsonObject();
			deepSkyObjects.add(new DeepSkyObject(objectInfo, object));
			objectInp.close();
		}
		
		inp.close();
		
		StellarSky.logger.info("Loaded Messier Objects.");
	}

	@Override
	public void updateLayer(StellarObjectContainer<DeepSkyObject, IConfigHandler> container, double year) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ICelestialLayerRenderer getLayerRenderer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int searchOrder() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isBackground() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public EnumCelestialCollectionType getCollectionType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<DeepSkyObject> getSuns(StellarObjectContainer container) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<DeepSkyObject> getMoons(StellarObjectContainer container) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Comparator<ICelestialObject> getDistanceComparator(SpCoord pos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Predicate<ICelestialObject> conditionInRange(SpCoord pos, double radius) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ILayerTempManager<DeepSkyObject> getTempLoadManager() {
		return null;
	}

}
