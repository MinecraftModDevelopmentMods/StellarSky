package stellarium.stellars.deepsky;

import java.io.IOException;

import com.google.common.base.Optional;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.util.ResourceLocation;
import stellarapi.api.CelestialPeriod;
import stellarapi.api.celestials.EnumObjectType;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarapi.api.view.ICCoordinates;
import stellarium.StellarSkyReferences;
import stellarium.stellars.layer.StellarObject;

public class DeepSkyObject extends StellarObject {
	protected String name;
	protected Vector3 centerPos;
	protected double magnitude;
	private double width, height;
	private Optional<DeepSkyTexture> texture;

	public DeepSkyObject(String objectId, JsonObject object) throws IOException {
		super(objectId, new ResourceLocation(StellarSkyReferences.MODID, object.get("name").getAsString()),
				EnumObjectType.DeepSkyObject);
		this.name = object.get("name").getAsString();
		this.magnitude = PositionUtil.getMagnitude(object.get("magnitude").getAsString());
		this.setStandardMagnitude(this.magnitude);

		JsonArray size = object.get("size").getAsJsonArray();
		this.width = Math.toRadians(PositionUtil.getDegreeFromDMS(size.get(0).getAsString()));
		this.height = Math.toRadians(PositionUtil.getDegreeFromDMS(size.get(1).getAsString()));

		JsonArray pos = object.get("position").getAsJsonArray();
		double ra = PositionUtil.getDegreeFromHMS(pos.get(0).getAsString());
		double dec = PositionUtil.getDegreeFromDMS(pos.get(1).getAsString());
		this.centerPos = new SpCoord(ra, dec).getVec();

		this.setPos(this.centerPos);
		
		if(object.has("textures"))
			this.texture = Optional.of(new DeepSkyTexture(object.get("textures").getAsJsonObject()));
		else this.texture = Optional.absent();
	}

	@Override
	public CelestialPeriod getHorizontalPeriod(ICCoordinates coords) {
		return new CelestialPeriod(String.format("Day; for %s", this.name),
				coords.getPeriod().getPeriodLength(),
				coords.calculateInitialOffset(this.centerPos, coords.getPeriod().getPeriodLength()));
	}

	public Optional<DeepSkyTexture> getTexture() {
		return this.texture;
	}

	/**
	 * Surface size in (rad)^2
	 * */
	public double getSurfaceSize() {
		return this.width * this.height;
	}

	public double getRadius() {
		return Math.sqrt(this.width * this.width + this.height * this.height);
	}

}
