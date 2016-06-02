package stellarium.render.stellars.phased;

import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Spmath;
import stellarapi.api.lib.math.Vector3;
import stellarium.render.stellars.access.IStellarChecker;
import stellarium.view.ViewerInfo;

public class StellarChecker implements IStellarChecker {
	
	private static final float DEFAULT_SIZE = Spmath.Radians(0.3f);
	
	private static final float leastBrightnessRendered = 1.0e-4f;
	private static final float leastBrightnessDominator = 1.0e-7f;
	
	private double multiplyingPower;
	private Vector3 colorMultiplier;
	private Vector3 resolutionColor;
	
	private SpCoord curPos;
	private float red, green, blue;
	private float scale, radius;
	
	public void setView(ViewerInfo info) {
		this.multiplyingPower = info.multiplyingPower;
		this.colorMultiplier = info.colorMultiplier;
		this.resolutionColor = info.resolutionColor;
	}
	
	@Override
	public void startDescription() {
		this.curPos = null;
		this.red = this.green = this.blue = 0;
		this.scale = 1.0f;
		this.radius = 0.0f;
	}

	@Override
	public void pos(SpCoord pos) {
		this.curPos = pos;
		this.scale = ?.brightnessScale(this.curPos, sky);
	}
	
	@Override
	public void radius(float radius) {
		this.radius = radius;
	}

	@Override
	public void brightness(float red, float green, float blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	@Override
	public boolean endCheckDominator() {
		if(Math.max(this.red * colorMultiplier.getX(),
				Math.max(this.green * colorMultiplier.getY(),
						this.blue * colorMultiplier.getZ()))
				> leastBrightnessDominator / scale) {
			
			return true;
		} else return false;
	}

	@Override
	public boolean endCheckRendered() {
		if(Math.max(this.red * colorMultiplier.getX() / Spmath.sqr(multiplyingPower * (this.radius + resolutionColor.getX())),
				Math.max(this.green * colorMultiplier.getY() / Spmath.sqr(multiplyingPower * (this.radius + resolutionColor.getY())),
						this.blue * colorMultiplier.getZ() / Spmath.sqr(multiplyingPower * (this.radius + resolutionColor.getZ()))))
				> leastBrightnessRendered / scale * Spmath.sqr(DEFAULT_SIZE)) {
			return true;
		} else return false;
	}
}
