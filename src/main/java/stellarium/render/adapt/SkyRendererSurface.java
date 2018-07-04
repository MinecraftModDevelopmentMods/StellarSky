package stellarium.render.adapt;

import java.lang.reflect.Field;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Throwables;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import stellarapi.api.render.IAdaptiveRenderer;
import stellarium.world.StellarScene;

public class SkyRendererSurface extends IAdaptiveRenderer {

	private IRenderHandler subRenderer;
	private IRenderHandler otherRenderer;

	private static Field skyVBOField = ReflectionHelper.findField(RenderGlobal.class, "skyVBO", "field_175012_t");
	private static Field sky2VBOField = ReflectionHelper.findField(RenderGlobal.class, "sky2VBO", "field_175011_u");
	private static Field glSkyListField = ReflectionHelper.findField(RenderGlobal.class, "glSkyList", "field_72771_w");
	private static Field glSkyList2Field = ReflectionHelper.findField(RenderGlobal.class, "glSkyList2", "field_72781_x");

	private static Field starVBOField = ReflectionHelper.findField(RenderGlobal.class, "starVBO", "field_175013_s");
	private static Field glStarListField = ReflectionHelper.findField(RenderGlobal.class, "starGLCallList", "field_72772_v");

	private static Field vertexBufferField = ReflectionHelper.findField(Tessellator.class, "buffer", "field_178183_a");

	private static int skyList, skyList2, starList;
	private static net.minecraft.client.renderer.vertex.VertexBuffer skyVBO, sky2VBO, starVBO;
	private static BufferBuilder placeholder;

	static {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();

        skyList = GLAllocation.generateDisplayLists(1);
        GlStateManager.glNewList(skyList, 4864);
        GlStateManager.glEndList();
        
        skyList2 = GLAllocation.generateDisplayLists(1);
        GlStateManager.glNewList(skyList2, 4864);
        GlStateManager.glEndList();

        starList = GLAllocation.generateDisplayLists(1);
        GlStateManager.glNewList(starList, 4864);
        GlStateManager.glEndList();

        skyVBO = new net.minecraft.client.renderer.vertex.VertexBuffer(DefaultVertexFormats.POSITION);
        vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        vertexbuffer.finishDrawing();
        vertexbuffer.reset();
        skyVBO.bufferData(vertexbuffer.getByteBuffer());
        
        sky2VBO = new net.minecraft.client.renderer.vertex.VertexBuffer(DefaultVertexFormats.POSITION);
        vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        vertexbuffer.finishDrawing();
        vertexbuffer.reset();
        sky2VBO.bufferData(vertexbuffer.getByteBuffer());

        starVBO =  new net.minecraft.client.renderer.vertex.VertexBuffer(DefaultVertexFormats.POSITION);
        vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        vertexbuffer.finishDrawing();
        vertexbuffer.reset();
        starVBO.bufferData(vertexbuffer.getByteBuffer());

        placeholder = new BufferBuilderPlaceholder(32768);

		skyVBOField.setAccessible(true);
		sky2VBOField.setAccessible(true);
		starVBOField.setAccessible(true);

		glSkyListField.setAccessible(true);
		glSkyList2Field.setAccessible(true);
		glStarListField.setAccessible(true);

		vertexBufferField.setAccessible(true);
	}

	public SkyRendererSurface(IRenderHandler subRenderer) {
		this.subRenderer = subRenderer;
	}

	@Override
	public void setReplacedRenderer(IRenderHandler handler) {
		this.otherRenderer = handler;
	}

	@Override
	public void render(float partialTicks, WorldClient world, Minecraft mc) {
		GlStateManager.clearColor(0.0f, 0.0f, 0.0f, 0.0f);
		GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT);

		StellarScene dimManager = StellarScene.getScene(world);
		if(dimManager.getSettings().renderPrevSky()) {
			RenderGlobal renderGlobal = mc.renderGlobal;
			float lat = (float) dimManager.getSettings().latitude;

			try {
				net.minecraft.client.renderer.vertex.VertexBuffer sky1 = (net.minecraft.client.renderer.vertex.VertexBuffer)skyVBOField.get(renderGlobal);
				net.minecraft.client.renderer.vertex.VertexBuffer sky2 = (net.minecraft.client.renderer.vertex.VertexBuffer)sky2VBOField.get(renderGlobal);
				net.minecraft.client.renderer.vertex.VertexBuffer star = (net.minecraft.client.renderer.vertex.VertexBuffer)starVBOField.get(renderGlobal);
				int sky1id = (Integer)glSkyListField.get(renderGlobal);
				int sky2id = (Integer)glSkyList2Field.get(renderGlobal);
				int starid = (Integer)glStarListField.get(renderGlobal);

				BufferBuilder buffer = (BufferBuilder)vertexBufferField.get(Tessellator.getInstance());
				
				skyVBOField.set(renderGlobal, skyVBO);
				sky2VBOField.set(renderGlobal, sky2VBO);
				starVBOField.set(renderGlobal, starVBO);
				glSkyListField.set(renderGlobal, skyList);
				glSkyList2Field.set(renderGlobal, skyList2);
				glStarListField.set(renderGlobal, starList);
	
				vertexBufferField.set(Tessellator.getInstance(), placeholder);

				GlStateManager.pushMatrix();
				GlStateManager.rotate(lat, 1.0f, 0.0f, 0.0f);
				if(this.otherRenderer != null)
					otherRenderer.render(partialTicks, world, mc);
				else {
					IRenderHandler renderer = world.provider.getSkyRenderer();
					world.provider.setSkyRenderer(null);
					renderGlobal.renderSky(partialTicks, 0);
					world.provider.setSkyRenderer(renderer);
				}
				GlStateManager.popMatrix();

				skyVBOField.set(renderGlobal, sky1);
				sky2VBOField.set(renderGlobal, sky2);
				starVBOField.set(renderGlobal, star);
				glSkyListField.set(renderGlobal, sky1id);
				glSkyList2Field.set(renderGlobal, sky2id);
				glStarListField.set(renderGlobal, starid);

				vertexBufferField.set(Tessellator.getInstance(), buffer);
			} catch (Exception exc) {
				throw new RuntimeException(exc);
			}

			this.renderDarkening(partialTicks, world, mc);
		}

		subRenderer.render(partialTicks, world, mc);
	}

	private void renderDarkening(float partialTicks, WorldClient world, Minecraft mc) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		float brightness = (float) world.getSunBrightness(partialTicks);

		GlStateManager.disableAlpha();
		GlStateManager.disableFog();
		GlStateManager.enableBlend();
		GlStateManager.depthMask(false);
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		for (int i = 0; i < 6; ++i)
		{
			GlStateManager.pushMatrix();

			if (i == 1)
				GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);

			if (i == 2)
				GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);

			if (i == 3)
				GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);

			if (i == 4)
				GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);

			if (i == 5)
				GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);

			vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
			vertexbuffer.pos(-100.0D, -100.0D, -100.0D).color(0.0f, 0.0f, 0.0f, 0.5f + 0.1f * brightness).endVertex();
			vertexbuffer.pos(-100.0D, -100.0D, 100.0D).color(0.0f, 0.0f, 0.0f, 0.5f + 0.1f * brightness).endVertex();
			vertexbuffer.pos(100.0D, -100.0D, 100.0D).color(0.0f, 0.0f, 0.0f, 0.5f + 0.1f * brightness).endVertex();
			vertexbuffer.pos(100.0D, -100.0D, -100.0D).color(0.0f, 0.0f, 0.0f, 0.5f + 0.1f * brightness).endVertex();

			tessellator.draw();
			GlStateManager.popMatrix();
		}

		GlStateManager.enableFog();
		GlStateManager.enableAlpha();
		GlStateManager.depthMask(true);
	}

	private static class BufferBuilderPlaceholder extends BufferBuilder {

		private boolean flag = false;

		public BufferBuilderPlaceholder(int bufferSizeIn) {
			super(bufferSizeIn);
		}

		@Override
		public void begin(int glMode, VertexFormat format) {
			super.begin(glMode, format);
			if(glMode == GL11.GL_TRIANGLE_FAN && format == DefaultVertexFormats.POSITION_COLOR)
				this.flag = true;
		}

		@Override
		public int getVertexCount() {
			return this.flag? 0 : super.getVertexCount();
		}

		@Override
		public void reset() {
			this.flag = false;
			super.reset();
		}
	}
}