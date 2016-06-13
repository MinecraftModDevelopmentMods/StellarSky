package stellarium.stellars.deepsky;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.util.ResourceLocation;

public class DeepSkyTexture {
	
	private ResourceLocation textureLocation;
	private double width, height;
	private String attribution;
	
	public DeepSkyTexture(JsonObject textureInfo) {
		this.textureLocation = new ResourceLocation(textureInfo.get("location").getAsString());
		JsonArray size = textureInfo.get("size").getAsJsonArray();
		this.width = PositionUtil.getDegreeFromDMS(size.get(0).getAsString());
		this.height = PositionUtil.getDegreeFromDMS(size.get(1).getAsString());
		this.attribution = textureInfo.get("attribution").getAsString();
	}

}
