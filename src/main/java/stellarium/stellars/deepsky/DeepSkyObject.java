package stellarium.stellars.deepsky;

import java.io.IOException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import stellarapi.api.lib.math.Matrix3;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarium.stellars.layer.StellarObject;

public class DeepSkyObject extends StellarObject {
	
	//Zero-time axial tilt
	public static final double e=0.4090926;
	public static final Matrix3 EqtoEc = new Matrix3();
	
	static {
		EqtoEc.setAsRotation(1.0, 0.0, 0.0, -e);
	}

	private String id;
	private String name;
	private Vector3 centerPos;
	private double magnitude;
	private double width, height;
	private DeepSkyTexture texture;

	public DeepSkyObject(String objectId, JsonObject object) throws IOException {
		this.id = objectId;
		this.name = object.get("name").getAsString();
		this.magnitude = object.get("magnitude").getAsDouble();

		JsonArray size = object.get("size").getAsJsonArray();
		this.width = PositionUtil.getDegreeFromDMS(size.get(0).getAsString());
		this.height = PositionUtil.getDegreeFromDMS(size.get(1).getAsString());
		
		JsonArray pos = object.get("position").getAsJsonArray();
		double ra = PositionUtil.getDegreeFromHMS(pos.get(0).getAsString());
		double dec = PositionUtil.getDegreeFromDMS(pos.get(1).getAsString());
		this.centerPos = EqtoEc.transform(new SpCoord(ra, dec).getVec());
		
		this.texture = new DeepSkyTexture(object.get("textures").getAsJsonObject());
	}

	@Override
	public String getID() {
		return this.id;
	}

}
