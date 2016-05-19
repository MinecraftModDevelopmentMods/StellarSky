package stellarium.render;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Vec3;
import stellarapi.api.StellarAPIReference;
import stellarapi.api.celestials.ICelestialObject;
import stellarapi.api.celestials.IEffectorType;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarium.api.ICelestialRenderer;
import stellarium.render.celesital.EnumRenderPass;
import stellarium.render.shader.IShaderObject;
import stellarium.render.shader.IUniformField;
import stellarium.render.shader.ShaderHelper;
import stellarium.util.math.VectorHelper;

public class TheSkyRenderer {
    private int skyList;
    private int skyListUnderPlayer;
    
    private ICelestialRenderer celestials;
    private IShaderObject atmosphere;
    
    private IUniformField lightDir, cameraHeight;
    private IUniformField outerRadius, innerRadius;
    private IUniformField nSamples;
    private IUniformField exposure;
    private IUniformField depthToFogFactor;
    private IUniformField extinctionFactor, gScattering, rayleighFactor, mieFactor;

	public TheSkyRenderer(ICelestialRenderer subRenderer) {
        Tessellator tessellator = Tessellator.instance;
        
        this.atmosphere = ShaderHelper.getInstance().buildShader("atmosphere",
        		"/stellarium/render/shaders/atmospheric_shader.vsh",
        		"/stellarium/render/shaders/atmospheric_shader.psh");
        
        this.lightDir = atmosphere.getField("v3LightDir");
        this.cameraHeight = atmosphere.getField("cameraHeight");
        this.outerRadius = atmosphere.getField("outerRadius");
        this.innerRadius = atmosphere.getField("innerRadius");
        this.nSamples = atmosphere.getField("nSamples");
		
        this.exposure = atmosphere.getField("exposure");
        this.depthToFogFactor = atmosphere.getField("depthToFogFactor");
		
		this.extinctionFactor = atmosphere.getField("extinctionFactor");
		this.gScattering = atmosphere.getField("g");
		this.rayleighFactor = atmosphere.getField("rayleighFactor");
		this.mieFactor = atmosphere.getField("mieFactor");


        this.celestials = subRenderer;
        this.latn = 99;
        this.longn = this.latn * 2;
        
        Vector3[][] displayvec = VectorHelper.createAndInitialize(longn, latn+1);
        
		for(int longc=0; longc<longn; longc++){
			for(int latc=0; latc<=latn; latc++){
				displayvec[longc][latc].set(new SpCoord(longc*360.0/longn, calculateLat(latc)).getVec());
				displayvec[longc][latc].scale(EnumRenderPass.getDeepDepth());
			}
		}
        
        
        this.skyList = GLAllocation.generateDisplayLists(1);
        
        GL11.glNewList(this.skyList, GL11.GL_COMPILE);

		tessellator.startDrawingQuads();

		for(int longc=0; longc<longn; longc++) {
			for(int latc=0; latc<latn; latc++) {
				int longcd=(longc+1)%longn;

				tessellator.addVertex(displayvec[longc][latc].getX(), displayvec[longc][latc].getY(), displayvec[longc][latc].getZ());
				tessellator.addVertex(displayvec[longc][latc+1].getX(), displayvec[longc][latc+1].getY(), displayvec[longc][latc+1].getZ());
				tessellator.addVertex(displayvec[longcd][latc+1].getX(), displayvec[longcd][latc+1].getY(), displayvec[longcd][latc+1].getZ());
				tessellator.addVertex(displayvec[longcd][latc].getX(), displayvec[longcd][latc].getY(), displayvec[longcd][latc].getZ());
			}
		}
		
		tessellator.draw();
        
        GL11.glEndList();
	}
	
	private int longn, latn;
	
	private float calculateLat(int latc) {
		float ratio = 2.0f * latc / latn - 1.0f;
		return 90.0f * ratio;
	}
	
	@SideOnly(Side.CLIENT)
	public void render(float partialTicks, WorldClient theWorld, Minecraft mc) {
		Tessellator tessellator = Tessellator.instance;
				
		ICelestialObject object = StellarAPIReference.getEffectors(theWorld, IEffectorType.Light).getPrimarySource();
		
		float rainStrength = theWorld.getRainStrength(partialTicks);
		float rainStrengthFactor = 1.0f + 5.0f * rainStrength;
        float weatherFactor = (float)(1.0D - (double)(rainStrength * 5.0F) / 16.0D);
        weatherFactor *= (1.0D - (double)(theWorld.getWeightedThunderStrength(partialTicks) * 5.0F) / 16.0D);
		
        float height = 0.1f;
        float groundFactor = 1.0f / (2 * height + 1.0f);
        
		Vec3 vec3 = theWorld.getSkyColor(mc.renderViewEntity, partialTicks);
        float red = (float)vec3.xCoord;
		float green = (float)vec3.yCoord;
		float blue = (float)vec3.zCoord;
        
        atmosphere.bindShader();
		
		lightDir.setVector3(object.getCurrentHorizontalPos().getVec());
		cameraHeight.setDouble(height);
		outerRadius.setDouble(820.0);
		innerRadius.setDouble(800.0);
		nSamples.setInteger(20);
		
		exposure.setDouble(2.0);
		depthToFogFactor.setDouble(10.0);
		
		Vector3 vec = new Vector3(0.09, 0.18, 0.27);
		extinctionFactor.setVector3(vec.scale(1.0));
		gScattering.setDouble(-0.8);
		
		rayleighFactor.setDouble4(
				4 * weatherFactor * red / 0.45,
				8 * weatherFactor * green / 0.65,
				16 * weatherFactor * blue,
				1.0);
		
		mieFactor.setDouble4(
				0.1 * rainStrengthFactor * weatherFactor,
				0.2 * rainStrengthFactor * weatherFactor,
				0.3 * rainStrengthFactor * weatherFactor,
				1.0);

        GL11.glDepthMask(false);

        GL11.glPushMatrix();
        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F); // e,n,z
		        
        GL11.glCallList(this.skyList);
		
        atmosphere.releaseShader();
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
        celestials.renderCelestial(mc, theWorld, new float[] {
        		red * groundFactor, green  * groundFactor, blue * groundFactor}, partialTicks);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        
        GL11.glPopMatrix();
	}
}
