package stellarium.render.atmosphere;

import java.nio.FloatBuffer;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.util.ResourceLocation;
import stellarapi.api.ISkyEffect;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Spmath;
import stellarapi.api.lib.math.Vector3;
import stellarapi.api.optics.Wavelength;
import stellarium.StellarSkyResources;
import stellarium.render.shader.IShaderObject;
import stellarium.render.shader.ShaderHelper;
import stellarium.render.stellars.access.IAtmosphericChecker;
import stellarium.view.ViewerInfo;

public class AtmosphericRenderer implements IPhasedRenderer {
	
	private AtmosphericChecker checker = new AtmosphericChecker();
		
	private List<SpCoord> dominatorPositions = Lists.newArrayList();
	private List<Vector3> dominatorColors = Lists.newArrayList();
	
	/** Size of radian 1 in rasterized pixel size */
	private float rasterizedAngleRatio;
	
	private float leastBrightnessRendered = 1.0e-4f;
	private float leastBrightnessDominator = 1.0e-7f;
	
	@Override
	public void check(Iterable<IAtmRenderedObjects> objects) {
		for(IAtmRenderedObjects object : objects)
			object.check(this.info, this.checker);
	}

	@Override
	public void render(Iterable<IAtmRenderedObjects> objects, boolean forDegradeMap, boolean forOpaque, boolean hasTexture) {
		for(IAtmRenderedObjects object : objects)
			object.render(tessellator, forOpaque, hasTexture);
	}
	
	public double dominationScale() {
		return 0.8;
	}

	
	private class AtmosphericChecker implements IAtmosphericChecker {
		
		private static final float DEFAULT_SIZE = Spmath.Radians(0.3f);
		
		private SpCoord curPos;
		private float red, green, blue;
		private float scale, radius;
		
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
			this.scale = atmHolder.getLeft().brightnessScale(this.curPos, sky);
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
				
				dominatorPositions.add(this.curPos);
				dominatorColors.add(new Vector3(
						this.red * colorMultiplier.getX(),
						this.green * colorMultiplier.getY(),
						this.blue * colorMultiplier.getZ()));
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
}
