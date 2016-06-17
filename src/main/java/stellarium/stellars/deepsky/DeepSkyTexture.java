package stellarium.stellars.deepsky;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.util.ResourceLocation;
import stellarapi.api.lib.math.Spmath;
import stellarapi.api.lib.math.Vector3;

public class DeepSkyTexture {
	
	private ResourceLocation textureLocation;
	private double width, height;
	private String attribution;
	
	public DeepSkyTexture(JsonObject textureInfo) {
		this.textureLocation = new ResourceLocation(textureInfo.get("location").getAsString());
		JsonArray size = textureInfo.get("size").getAsJsonArray();
		this.width = Spmath.Radians(PositionUtil.getDegreeFromDMS(size.get(0).getAsString()));
		this.height = Spmath.Radians(PositionUtil.getDegreeFromDMS(size.get(1).getAsString()));
		this.attribution = textureInfo.get("attribution").getAsString();
	}
	
	public void fill(Vector3 center, Vector3 dirWidth, Vector3 dirHeight, Vector3[] points) {
		dirWidth.scale(this.width);
		dirHeight.scale(this.height);
		points[0].set(center).add(dirWidth).add(dirHeight); //1,0
		points[1].set(center).sub(dirWidth).add(dirHeight); //0,0
		points[2].set(center).sub(dirWidth).sub(dirHeight); //0,1
		points[3].set(center).add(dirWidth).sub(dirHeight); //1,1
	}

	public ResourceLocation getTextureLocation() {
		return this.textureLocation;
	}

}
