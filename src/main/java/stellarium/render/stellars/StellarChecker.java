package stellarium.render.stellars;

import net.minecraft.world.World;
import stellarapi.api.ISkyEffect;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Spmath;
import stellarapi.api.lib.math.Vector3;
import stellarapi.api.optics.NakedScope;
import stellarium.render.stellars.access.ICheckedAtmModel;
import stellarium.render.stellars.access.IStellarChecker;
import stellarium.stellars.OpticsHelper;
import stellarium.view.ViewerInfo;

@Deprecated
public class StellarChecker implements IStellarChecker {	
	private static final float NOT_POINT_MULT = 2.0f;
	
	private static final float leastBrightnessDominator = 1.0e-7f;

	private ICheckedAtmModel atmChecker;
	
	private Vector3 colorMultiplier;
	private double resolutionGeneral;

	private SpCoord pos;
	private float red, green, blue;
	private float radius;

	public void setAtmModel(ICheckedAtmModel atmModel) {
		this.atmChecker = atmModel;
	}

	public void setView(World world, ViewerInfo info) {
		this.colorMultiplier = info.colorMultiplier;
		this.resolutionGeneral = info.resolutionGeneral;
	}

	@Override
	public void startDescription() {
		this.red = this.green = this.blue = 0;
		this.radius = 0.0f;
	}
	
	@Override
	public void pos(SpCoord pos) {
		this.pos = pos;
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
	public boolean checkDominator() {
		if(Math.max(this.red * colorMultiplier.getX(),
				Math.max(this.green * colorMultiplier.getY(),
						this.blue * colorMultiplier.getZ()))
				> leastBrightnessDominator) {
			
			return true;
		} else return false;
	}

	@Override
	public boolean checkRendered() {
		return atmChecker.isRendered(this.pos);
	}

	@Override
	public boolean checkEnoughRadius() {
		return this.radius > NOT_POINT_MULT * this.resolutionGeneral;
	}
}
