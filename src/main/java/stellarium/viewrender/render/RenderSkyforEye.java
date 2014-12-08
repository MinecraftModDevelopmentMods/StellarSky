package stellarium.viewrender.render;

import java.nio.DoubleBuffer;

import org.lwjgl.opengl.GL11;

import stellarium.viewrender.viewer.Viewer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraftforge.client.*;

public class RenderSkyforEye extends IRenderHandler {
	
	private Minecraft mc;
	private WorldClient world;
	private TextureManager eng;
	private Tessellator tes;
	
	protected Viewer view;

	@Override
	@SideOnly(Side.CLIENT)
	public void render(float partialTicks, WorldClient world, Minecraft mc) {
		this.mc = mc;
		this.world = world;
		this.eng = mc.renderEngine;
		this.tes = Tessellator.instance;
		
		CRenderEngine.instance.reng = this.eng;
		
		if(!view.rm.CCDMode){
			PreRender();
			view.OnRender(partialTicks);
			PostRender();
		}
	}
	
	protected void PreRender(){
        GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDepthMask(false);
		SetCoord();
	}
	
	protected void PostRender(){
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPopMatrix();
	}
	
	protected void SetCoord(){
		GL11.glPushMatrix();
        GL11.glTranslatef(0.0f, 0.0f, 0.0f); //East, Zenith, South
        GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f); //East, North, Zenith - Horizontal Coord
 
	}

}
