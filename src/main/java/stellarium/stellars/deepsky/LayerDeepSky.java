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

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stellarapi.api.celestials.EnumCelestialCollectionType;
import stellarapi.api.celestials.CelestialObject;
import stellarapi.api.lib.config.IConfigHandler;
import stellarapi.api.lib.config.INBTConfig;
import stellarapi.api.lib.math.SpCoord;
import stellarium.StellarSky;
import stellarium.stellars.layer.IStellarLayerType;
import stellarium.stellars.layer.StellarCollection;
import stellarium.stellars.render.ICelestialLayerRenderer;

public class LayerDeepSky implements IStellarLayerType<DeepSkyObject, IConfigHandler, INBTConfig> {

	private List<DeepSkyObject> deepSkyObjects = Lists.newArrayList();
	
	@Override
	public void initializeClient(IConfigHandler config, StellarCollection<DeepSkyObject> container)
			throws IOException {
		this.loadMessierData();
	}

	@Override
	public void initializeCommon(INBTConfig config, StellarCollection<DeepSkyObject> container)
			throws IOException {
		for(DeepSkyObject object : this.deepSkyObjects) {
			container.loadObject("Messier", object);
			container.addRenderCache(object, new DeepSkyObjectCache());
		}
	}
	
	private static final String messierParent = "/assets/stellarium/deepsky/messier/";
	private Gson gson = new Gson();
	private JsonParser parser = new JsonParser();
	
	@SideOnly(Side.CLIENT)
	private void loadMessierData() throws IOException {
		StellarSky.INSTANCE.getLogger().info("Loading Messier Objects...");

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
		
		StellarSky.INSTANCE.getLogger().info("Loaded Messier Objects.");
	}

	@Override
	public void updateLayer(StellarCollection<DeepSkyObject> container, double year) { }

	@SideOnly(Side.CLIENT)
	@Override
	public ICelestialLayerRenderer getLayerRenderer() {
		return DeepSkyRenderer.INSTANCE;
	}

	@Override
	public String getName() {
		return "DeepSky";
	}

	@Override
	public int searchOrder() {
		return 2;
	}

	@Override
	public EnumCelestialCollectionType getCollectionType() {
		return EnumCelestialCollectionType.DeepSkyObjects;
	}

	@Override
	public Collection<DeepSkyObject> getSuns(StellarCollection<DeepSkyObject> container) {
		return null;
	}

	@Override
	public Collection<DeepSkyObject> getMoons(StellarCollection<DeepSkyObject> container) {
		return null;
	}

	@Override
	public Comparator<CelestialObject> getDistanceComparator(final SpCoord pos) {
		return new Comparator<CelestialObject>() {
			@Override
			public int compare(CelestialObject obj1, CelestialObject obj2) {
				if(obj1 instanceof DeepSkyImage && obj2 instanceof DeepSkyImage) {
					DeepSkyImage image = (DeepSkyImage) obj1;
					DeepSkyImage image2 = (DeepSkyImage) obj2;

					if(pos.distanceTo(image.appPos) - image.radius < pos.distanceTo(image2.appPos) - image2.radius)
						return -1;
					else return 1;
				}
				return 1;
			}
		};
	}

	@Override
	public Predicate<CelestialObject> conditionInRange(final SpCoord pos, final double radius) {
		return new Predicate<CelestialObject>() {
			@Override
			public boolean apply(CelestialObject input) {
				if(input instanceof DeepSkyImage) {
					DeepSkyImage image = (DeepSkyImage) input;
					return pos.distanceTo(image.appPos) < radius + image.radius;
				}

				return true;
			}
		};
	}
}
