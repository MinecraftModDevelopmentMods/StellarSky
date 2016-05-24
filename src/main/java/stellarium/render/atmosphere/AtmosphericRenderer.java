package stellarium.render.atmosphere;

import java.nio.FloatBuffer;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import net.minecraft.client.renderer.GLAllocation;
import stellarapi.api.ISkyEffect;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Spmath;
import stellarapi.api.lib.math.Vector3;
import stellarapi.api.optics.IOpticalFilter;
import stellarapi.api.optics.IViewScope;
import stellarapi.api.optics.Wavelength;
import stellarium.StellarSkyResources;
import stellarium.render.shader.IShaderObject;
import stellarium.render.shader.ShaderHelper;

public class AtmosphericRenderer implements IPhasedRenderer {
	
	private AtmosphericChecker checker = new AtmosphericChecker();
	
	// Left is mapped, Right is not mapped
	private Pair<AtmosphericTessellator, AtmosphericTessellator> scatterFuzzy;
	private Pair<AtmosphericTessellator, AtmosphericTessellator> scatterPoint;
	private Pair<AtmosphericTessellator, AtmosphericTessellator> srcTexture;
	
	private Pair<AtmosphereHolder, AtmosphereHolder> atmHolder;
	
	private List<IAtmRenderedObjects> renderedObjectsList;
	
	private List<SpCoord> dominatorPositions = Lists.newArrayList();
	private List<Vector3> dominatorColors = Lists.newArrayList();
	
	private Vector3 colorMultiplier = new Vector3();
	private float multiplyingPower;
	
	private ISkyEffect sky;
	
	private Vector3 resolutionColor = new Vector3();
	private float resolutionGeneral;
	
	/** Size of radian 1 in rasterized pixel size */
	private float rasterizedAngleRatio;
	
	private float leastBrightnessRendered;
	private float leastBrightnessDominator;
	//private float maxDominatorMultiplier;
	
	private static final float DEFAULT_SIZE = Spmath.Radians(0.3f);
	
	public Pair<AtmosphereHolder, AtmosphereHolder> initialize(List<IAtmRenderedObjects> renderedObjectsList) {
		this.renderedObjectsList = renderedObjectsList;
		
		this.scatterFuzzy = Pair.of(
				new AtmosphericTessellator(false,
						ShaderHelper.getInstance().buildShader("fuzzymapped", StellarSkyResources.vertexScatterFuzzyMapped, StellarSkyResources.fragmentScatterFuzzyMapped)),
				new AtmosphericTessellator(false,
						ShaderHelper.getInstance().buildShader("fuzzyskylight", StellarSkyResources.vertexScatterFuzzySkylight, StellarSkyResources.fragmentScatterFuzzySkylight)));
		
		this.scatterPoint = Pair.of(
				new AtmosphericTessellator(false,
						ShaderHelper.getInstance().buildShader("pointmapped", StellarSkyResources.vertexScatterPointMapped, StellarSkyResources.fragmentScatterPointMapped)),
				new AtmosphericTessellator(false,
						ShaderHelper.getInstance().buildShader("pointskylight", StellarSkyResources.vertexScatterPointSkylight, StellarSkyResources.fragmentScatterPointSkylight)));
		
		this.srcTexture = Pair.of(
				new AtmosphericTessellator(true,
						ShaderHelper.getInstance().buildShader("srctexturemapped", StellarSkyResources.vertexTextureMapped, StellarSkyResources.fragmentTextureMapped)),
				new AtmosphericTessellator(true,
						ShaderHelper.getInstance().buildShader("srctextureskylight", StellarSkyResources.vertexTextureSkylight, StellarSkyResources.fragmentTextureSkylight)));
		
		this.atmHolder = Pair.of(
				new AtmosphereHolder(ShaderHelper.getInstance().buildShader("atmospherespcoord", StellarSkyResources.vertexAtmosphereSpCoord, StellarSkyResources.fragmentAtmosphereSpCoord)),
				new AtmosphereHolder(ShaderHelper.getInstance().buildShader("atmospheresimple", StellarSkyResources.vertexAtmosphereSimple, StellarSkyResources.fragmentAtmosphereSimple)));
		
		return this.atmHolder;
	}
	
	public void setMultipliers(ISkyEffect sky, IViewScope scope, IOpticalFilter filter, float screenSize) {
		this.multiplyingPower = (float) scope.getMP();
		colorMultiplier.set(
				scope.getLGP() * filter.getFilterEfficiency(Wavelength.red),
				scope.getLGP() * filter.getFilterEfficiency(Wavelength.V),
				scope.getLGP() * filter.getFilterEfficiency(Wavelength.B)
				);
		
		resolutionColor.set(
				Spmath.Radians(Math.max(scope.getResolution(Wavelength.red), sky.getSeeing(Wavelength.red))),
				Spmath.Radians(Math.max(scope.getResolution(Wavelength.V), sky.getSeeing(Wavelength.V))),
				Spmath.Radians(Math.max(scope.getResolution(Wavelength.B), sky.getSeeing(Wavelength.B)))
				);
		
		this.resolutionGeneral = (float) Spmath.Radians(Math.max(scope.getResolution(Wavelength.visible), sky.getSeeing(Wavelength.visible)));
		
		this.rasterizedAngleRatio = this.multiplyingPower / Spmath.Radians(70.0f) * screenSize;
		
		this.sky = sky;
	}
	
	@Override
	public int numberDominators() {
		return dominatorPositions.size();
	}

	@Override
	public void setupDominateShader(int index, boolean asMap) {
		if(asMap) atmHolder.getLeft().setupShader(
				dominatorPositions.get(index), dominatorColors.get(index));
		else atmHolder.getRight().setupShader(
				dominatorPositions.get(index), dominatorColors.get(index));
	}

	@Override
	public IShaderObject setupShader(boolean forDegradeMap, boolean forOpaque, boolean hasTexture) {
		Pair<AtmosphericTessellator, AtmosphericTessellator> thePair;
		IShaderObject object;

		if(hasTexture)
			thePair = this.srcTexture;
		else if(forOpaque)
			thePair = this.scatterFuzzy;
		else thePair = this.scatterPoint;
		
		if(forDegradeMap)
			object = thePair.getLeft().getShader();
		else object = thePair.getRight().getShader();
		
		object.bindShader();
		return object;
	}
	
	@Override
	public void check() {
		for(IAtmRenderedObjects object : this.renderedObjectsList)
			object.check(this.checker);
	}

	@Override
	public void render(boolean forDegradeMap, boolean forOpaque, boolean hasTexture) {
		AtmosphericTessellator tessellator;
		Pair<AtmosphericTessellator, AtmosphericTessellator> thePair;

		if(hasTexture)
			thePair = this.srcTexture;
		else if(forOpaque)
			thePair = this.scatterFuzzy;
		else thePair = this.scatterPoint;
		
		if(forDegradeMap)
			tessellator = thePair.getLeft();
		else tessellator = thePair.getRight();
		
		for(IAtmRenderedObjects object : this.renderedObjectsList)
			object.render(tessellator, forOpaque, hasTexture);
	}

	@Override
	public double skyBrightness() {
		return atmHolder.getLeft().getSkyBrightness();
	}
	
	public double dominationScale() {
		return 0.8;
	}

	
	private class AtmosphericChecker implements IAtmosphericChecker {
		
		private SpCoord curPos;
		private float red, green, blue;
		private float scale, radius;
		
		@Override
		public void startDescription() {
			this.curPos = null;
			this.red = this.green = this.blue = 0;
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
	
	private class AtmosphericTessellator implements IAtmosphericTessellator {

		//Limits size per draw call
		private static final int maxBufferSize = 0x40000;
		
		private float radius;
		
		private float longitude, latitude, depth;
		private float uCoord, vCoord;
		private float red, green, blue;
		private float normalX, normalY, normalZ;
		
		private final boolean hasTexture;
		private final IShaderObject shader;
		
		private int currentContextSize;
		private boolean hasNormal;
		
		private FloatBuffer buffer;
		private float[] rawBuffer;
		private int rawBufferCount;
		private short vertexCount;
		
		AtmosphericTessellator(boolean hasTexture, IShaderObject shader) {
			this.hasTexture = hasTexture;
			this.shader = shader;
			this.buffer = GLAllocation.createDirectFloatBuffer(maxBufferSize);
			this.rawBuffer = new float[maxBufferSize];
		}
		
		public IShaderObject getShader() {
			return this.shader;
		}
		
		@Override
		public void begin(boolean hasNormal) {
			if(this.hasTexture) {
				this.currentContextSize = hasNormal? 11 : 8;
				;
			} else {
				this.currentContextSize = 6;
				this.radius = 0.0f;
			}
			
			this.rawBufferCount = 0;
			this.vertexCount = 0;
		}

		@Override
		public void pos(SpCoord pos, float depth) {
			this.longitude = (float) Spmath.Radians(pos.x);
			this.latitude = (float) Spmath.Radians(pos.y);
			this.depth = depth;
		}

		@Override
		public void color(float red, float green, float blue) {
			this.red = red;
			this.green = green;
			this.blue = blue;
		}

		@Override
		public void texture(float u, float v) {
			if(this.hasTexture) {
				this.uCoord = u;
				this.vCoord = v;
			}
		}

		@Override
		public void normal(Vector3 normal) {
			if(this.currentContextSize == 11) {
				this.normalX = (float) normal.getX();
				this.normalY = (float) normal.getY();
				this.normalZ = (float) normal.getZ();
				this.hasNormal = true;
			}
		}

		@Override
		public void writeVertex() {
			this.rawBuffer[this.rawBufferCount] = this.longitude;
			this.rawBuffer[this.rawBufferCount + 1] = this.latitude;
			this.rawBuffer[this.rawBufferCount + 2] = this.depth;
			this.rawBuffer[this.rawBufferCount + 3] = this.red;
			this.rawBuffer[this.rawBufferCount + 4] = this.green;
			this.rawBuffer[this.rawBufferCount + 5] = this.blue;
			
			if(this.hasTexture) {
				this.rawBuffer[this.rawBufferCount + 6] = this.uCoord;
				this.rawBuffer[this.rawBufferCount + 7] = this.vCoord;
			}
			if(this.hasNormal) {
				this.rawBuffer[this.rawBufferCount + 8] = this.normalX;
				this.rawBuffer[this.rawBufferCount + 9] = this.normalY;
				this.rawBuffer[this.rawBufferCount + 10] = this.normalZ;
			}
			
			this.rawBufferCount += this.currentContextSize;
			this.vertexCount ++;
			this.hasNormal = false;
		}

		@Override
		public void end() {
			if(this.hasTexture) {
				;
			} else {
				float size = (2.5f * resolutionGeneral + this.radius);
				GL11.glPointSize(rasterizedAngleRatio * size);
				if(this.radius > 0.0f)
					shader.getField("scaledRadius").setDouble(this.radius / size);
				
				shader.getField("invres2").setDouble4(
						this.toInvRes2(size, (float)resolutionColor.getX()),
						this.toInvRes2(size, (float)resolutionColor.getY()),
						this.toInvRes2(size, (float)resolutionColor.getZ()),
						this.toInvRes2(size, resolutionGeneral));
			}
			
			buffer.put(this.rawBuffer, 0, this.rawBufferCount);
			buffer.limit(this.rawBufferCount);
			
			buffer.position(0);
			GL11.glVertexPointer(3, this.currentContextSize << 2, this.buffer);
			GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
			
			buffer.position(3);
			GL11.glColorPointer(3, this.currentContextSize << 2, this.buffer);
			GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);

			if(this.hasTexture) {
				buffer.position(6);
				GL11.glTexCoordPointer(2, this.currentContextSize << 2, this.buffer);
				GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
			}
			
			if(this.currentContextSize == 11) {
				buffer.position(8);
				GL11.glNormalPointer(this.currentContextSize << 2, this.buffer);
				GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
			}
			
			GL11.glDrawArrays(this.hasTexture? GL11.GL_QUADS : GL11.GL_POINTS,
					0, this.vertexCount);
			
			if(this.currentContextSize == 11) {
				GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
			}
			
			if(this.hasTexture) {
				GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
			}
			
			GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
			GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		}
		
		private float toInvRes2(float size, float resolution) {
			float invres = size / resolution;
			return 0.5f * invres * invres;
		}

		
		@Override
		public void radius(float angularRadius) {
			if(!this.hasTexture)
				this.radius = angularRadius;
		}
	}

}
