package stellarium.display.horgrid;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stellarium.display.DisplayRenderInfo;
import stellarium.display.IDisplayRenderer;
import stellarium.render.stellars.layer.LayerRHelper;

@SideOnly(Side.CLIENT)
public class HorGridRenderer implements IDisplayRenderer<HorGridCache> {

	@Override
	public void render(DisplayRenderInfo info, HorGridCache cache) {
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		
		if(!cache.enabled || info.isPostCelesitals)
			return;
		
		GlStateManager.disableTexture2D();
		GlStateManager.pushMatrix();
		GlStateManager.scale(LayerRHelper.DEEP_DEPTH, LayerRHelper.DEEP_DEPTH, LayerRHelper.DEEP_DEPTH);
		
		if(cache.gridEnabled) {
			GlStateManager.glLineWidth(2.0f);
			GlStateManager.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);

			info.builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

			for(int longc=0; longc<cache.longn; longc++){
				for(int latc=0; latc<cache.latn; latc++){
					int longcd=(longc+1)%cache.longn;

					info.builder.pos(cache.displayvec[longc][latc].getX(), cache.displayvec[longc][latc].getY(), cache.displayvec[longc][latc].getZ());
					info.builder.color((float)cache.colorvec[longc][latc].getX(),
							(float)cache.colorvec[longc][latc].getY(),
							(float)cache.colorvec[longc][latc].getZ(), cache.brightness);
					info.builder.endVertex();
					
					info.builder.pos(cache.displayvec[longcd][latc].getX(), cache.displayvec[longcd][latc].getY(), cache.displayvec[longcd][latc].getZ());
					info.builder.color((float)cache.colorvec[longcd][latc].getX(),
							(float)cache.colorvec[longcd][latc].getY(),
							(float)cache.colorvec[longcd][latc].getZ(), cache.brightness);
					info.builder.endVertex();
					
					info.builder.pos(cache.displayvec[longcd][latc+1].getX(), cache.displayvec[longcd][latc+1].getY(), cache.displayvec[longcd][latc+1].getZ());
					info.builder.color((float)cache.colorvec[longcd][latc+1].getX(),
							(float)cache.colorvec[longcd][latc+1].getY(),
							(float)cache.colorvec[longcd][latc+1].getZ(), cache.brightness);
					info.builder.endVertex();
					
					info.builder.pos(cache.displayvec[longc][latc+1].getX(), cache.displayvec[longc][latc+1].getY(), cache.displayvec[longc][latc+1].getZ());
					info.builder.color((float)cache.colorvec[longc][latc+1].getX(),
							(float)cache.colorvec[longc][latc+1].getY(),
							(float)cache.colorvec[longc][latc+1].getZ(), cache.brightness);
					info.builder.endVertex();
				}
			}

			info.tessellator.draw();

			GlStateManager.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
			GlStateManager.glLineWidth(1.0f);
		}

		if(cache.horizonEnabled) {
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
			
			GlStateManager.glLineWidth(5.0f);

			GlStateManager.color(0.3f, 0.7f, 1.0f, 2.0f * cache.brightness);
			
			info.builder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);

			for(int longc=0; longc<cache.longn; longc++){
				int longcd=(longc+1)%cache.longn;
				info.builder.pos(cache.horizon[longc].getX(), cache.horizon[longc].getY(), cache.horizon[longc].getZ()).endVertex();
				info.builder.pos(cache.horizon[longcd].getX(), cache.horizon[longcd].getY(), cache.horizon[longcd].getZ()).endVertex();
			}

			info.tessellator.draw();
			
			GlStateManager.glLineWidth(1.0f);

			GlStateManager.shadeModel(GL11.GL_FLAT);
		}
		
		GlStateManager.enableTexture2D();
		GlStateManager.popMatrix();
	}

}
