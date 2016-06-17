package stellarium.stellars.deepsky;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.util.ResourceLocation;
import stellarapi.api.lib.math.Vector3;

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
	
	public void fill(Vector3 center, Vector3 dirWidth, Vector3 dirHeight, Vector3[] points) {
		
	}

}
