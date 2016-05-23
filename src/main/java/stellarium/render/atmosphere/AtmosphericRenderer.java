package stellarium.render.atmosphere;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import net.minecraft.client.renderer.GLAllocation;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarium.render.shader.IShaderObject;

public class AtmosphericRenderer implements IPhasedRenderer {
	
	private Pair<IShaderObject, IShaderObject> scatterFuzzy;
	private Pair<IShaderObject, IShaderObject> scatterPoint;
	private Pair<IShaderObject, IShaderObject> srcTexture;
	
	private Pair<AtmosphereHolder, AtmosphereHolder> atmHolder;
	
	private List<SpCoord> dominatorPositions = Lists.newArrayList();
	private List<Vector3> dominatorColors = Lists.newArrayList();
	
	private Vector3 resolutionColor;
	private float resolutionGeneral;
	
	/** Size of radian 1 in rasterized pixel size */
	private float rasterizedAngleRatio;
	
	private void initialize() {
		
	}
	
	@Override
	public int numberDominators() {
		return dominatorPositions.size();
	}

	@Override
	public void setupDominateShader(int index, boolean asMap) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IShaderObject setupShader(boolean forDegradeMap, boolean forOpaque, boolean hasTexture) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void render(boolean forOpaque, boolean hasTexture) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double skyBrightness() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private class AtmosphericChecker implements IAtmosphericChecker {
		
		private SpCoord curPos;
		private float red, green, blue;
		
		@Override
		public void startDescription() {
			this.curPos = null;
			this.red = this.green = this.blue = 0;
		}

		@Override
		public void pos(SpCoord pos) {
			this.curPos = pos;
		}

		@Override
		public void brightness(float red, float green, float blue) {
			this.red = red;
			this.green = green;
			this.blue = blue;
		}

		@Override
		public boolean endCheckDominator() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean endCheckRendered() {
			// TODO Auto-generated method stub
			return false;
		}
	}
	
	private class AtmosphericTessellator implements IAtmosphericTessellator {

		private static final int maxBufferSize = 0x10000;
		
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
		private short rawBufferCount;
		
		AtmosphericTessellator(boolean hasTexture, IShaderObject shader) {
			this.hasTexture = hasTexture;
			this.shader = shader;
			this.buffer = GLAllocation.createDirectFloatBuffer(maxBufferSize);
			this.rawBuffer = new float[maxBufferSize];
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
		}

		@Override
		public void pos(SpCoord pos, float depth) {
			this.longitude = (float) pos.x;
			this.latitude = (float) pos.y;
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
			this.hasNormal = false;
		}

		@Override
		public void end() {
			// TODO Auto-generated method stub
			if(this.hasTexture) {

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
				
				;
			}
			
			buffer.put(this.rawBuffer, 0, this.rawBufferCount);
			buffer.limit(this.rawBufferCount);
			
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
