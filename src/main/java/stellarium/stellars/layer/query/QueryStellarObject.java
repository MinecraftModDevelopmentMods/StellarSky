package stellarium.stellars.layer.query;

import com.google.common.base.Predicate;

import stellarapi.api.celestials.ICelestialObject;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Spmath;

public class QueryStellarObject implements Predicate<ICelestialObject> {
	private SpCoord pos;
	private double radius;
	
	public QueryStellarObject(SpCoord pos, double radius) {
		this.pos = new SpCoord(pos.x, pos.y);
		this.radius = radius;
	}
	
	public SpCoord getPos() {
		return this.pos;
	}
	
	public double getRadius() {
		return this.radius;
	}
	
	public double distance(QueryStellarObject query) {
		// TODO reasonable distance metric
		return (this.radius + query.radius) / Math.max(this.radius + query.radius - pos.distanceTo(query.pos), 0.0);
	}

	public boolean isRelated(QueryStellarObject query) {
		return pos.distanceTo(query.pos) <= this.radius + query.radius;
	}
	
	@Override
	public int hashCode() {
		return Double.hashCode(pos.x) ^ Double.hashCode(pos.y) ^ Double.hashCode(this.radius);
	}

	/**
	 * Default behavior of accepting objects
	 * */
	@Override
	public boolean apply(ICelestialObject input) {
		return pos.distanceTo(input.getCurrentHorizontalPos()) < this.radius;
	}
	
	/*@Override
	public boolean equals(Object obj) {
		if(obj instanceof QueryStellarObject) {
			QueryStellarObject query = (QueryStellarObject) obj;
			if(Math.abs(query.radius - this.radius) / (query.radius + this.radius) >= 0.01)
				return false;
			if(query.pos.distanceTo(pos) >= 0.02 * this.radius)
				return false;
			return true;
		} else return false;
	}*/
}
