package stellarium.stellars.deepsky;

import java.io.IOException;

import com.google.common.base.Optional;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Spmath;
import stellarapi.api.lib.math.Vector3;
import stellarium.stellars.layer.StellarObject;

public class DeepSkyObject extends StellarObject {

	private String id;
	protected String name;
	protected Vector3 centerPos;
	protected double magnitude;
	private double width, height;
	private Optional<DeepSkyTexture> texture;

	public DeepSkyObject(String objectId, JsonObject object) throws IOException {
		this.id = objectId;
		this.name = object.get("name").getAsString();
		this.magnitude = PositionUtil.getMagnitude(object.get("magnitude").getAsString());

		JsonArray size = object.get("size").getAsJsonArray();
		this.width = Spmath.Radians(PositionUtil.getDegreeFromDMS(size.get(0).getAsString()));
		this.height = Spmath.Radians(PositionUtil.getDegreeFromDMS(size.get(1).getAsString()));

		JsonArray pos = object.get("position").getAsJsonArray();
		double ra = PositionUtil.getDegreeFromHMS(pos.get(0).getAsString());
		double dec = PositionUtil.getDegreeFromDMS(pos.get(1).getAsString());
		this.centerPos = new SpCoord(ra, dec).getVec();
		
		if(object.has("textures"))
			this.texture = Optional.of(new DeepSkyTexture(object.get("textures").getAsJsonObject()));
		else this.texture = Optional.absent();
	}

	@Override
	public String getID() {
		return this.id;
	}

	public Optional<DeepSkyTexture> getTexture() {
		return this.texture;
	}

	public double getSurfaceSize() {
		return this.width * this.height;
	}

	public double getRadius() {
		return Math.sqrt(this.width * this.width + this.height * this.height);
	}

}
