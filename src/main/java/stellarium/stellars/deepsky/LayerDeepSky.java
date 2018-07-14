package stellarium.stellars.deepsky;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stellarapi.api.celestials.CelestialObject;
import stellarapi.api.celestials.EnumCollectionType;
import stellarapi.api.lib.config.IConfigHandler;
import stellarapi.api.lib.config.INBTConfig;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.observe.SearchRegion;
import stellarium.StellarSky;
import stellarium.StellarSkyReferences;
import stellarium.stellars.layer.StellarCollection;
import stellarium.stellars.layer.StellarLayer;
import stellarium.stellars.render.ICelestialLayerRenderer;

public class LayerDeepSky extends StellarLayer<DeepSkyObject, IConfigHandler, INBTConfig> {
	public LayerDeepSky() {
		super(new ResourceLocation(StellarSkyReferences.MODID, "deepsky"),
				EnumCollectionType.DeepSkyObjects, 2);
	}

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
			container.loadObject("messier", object);
			container.addRenderCache(object, new DeepSkyObjectCache());
		}
	}

	@Override
	public Set<CelestialObject> findIn(StellarCollection<DeepSkyObject> container, SearchRegion region, float efficiency, float multPower) {
		// TODO Find correct area check and take account of efficiency and brightness
		SpCoord cache = new SpCoord();
		return container.getLoadedObjects("messier").stream()
				.filter(object -> region.test(cache.setWithVec(object.centerPos)))
				.collect(Collectors.toSet());
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

	@SideOnly(Side.CLIENT)
	@Override
	public ICelestialLayerRenderer getLayerRenderer() {
		return DeepSkyRenderer.INSTANCE;
	}
}
