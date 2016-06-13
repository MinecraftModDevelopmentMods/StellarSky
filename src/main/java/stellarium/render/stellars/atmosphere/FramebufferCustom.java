package stellarium.render.stellars.atmosphere;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.ARBTextureFloat;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import stellarium.util.OpenGlVersionUtil;

public class FramebufferCustom extends Framebuffer {
	
	private boolean isInterpolated;

	public FramebufferCustom(int width, int height, boolean withDepth) {
		super(width, height, withDepth);
	}
	
	@Override
    public void createFramebuffer(int p_147605_1_, int p_147605_2_)
    {
        this.framebufferWidth = p_147605_1_;
        this.framebufferHeight = p_147605_2_;
        this.framebufferTextureWidth = p_147605_1_;
        this.framebufferTextureHeight = p_147605_2_;

        if (!OpenGlHelper.isFramebufferEnabled())
        {
            this.framebufferClear();
        }
        else
        {
            this.framebufferObject = OpenGlHelper.func_153165_e();
            this.framebufferTexture = TextureUtil.glGenTextures();

            if (this.useDepth)
            {
                this.depthBuffer = OpenGlHelper.func_153185_f();
            }

            GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.framebufferTexture);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, OpenGlVersionUtil.rgb16f(), this.framebufferTextureWidth, this.framebufferTextureHeight, 0, GL11.GL_RGB, GL11.GL_FLOAT, (FloatBuffer)null);
            OpenGlHelper.func_153171_g(OpenGlHelper.field_153198_e, this.framebufferObject);
            OpenGlHelper.func_153188_a(OpenGlHelper.field_153198_e, OpenGlHelper.field_153200_g, GL11.GL_TEXTURE_2D, this.framebufferTexture, 0);

            if (this.useDepth)
            {
                OpenGlHelper.func_153176_h(OpenGlHelper.field_153199_f, this.depthBuffer);
                if (net.minecraftforge.client.MinecraftForgeClient.getStencilBits() == 0)
                {
                	OpenGlHelper.func_153186_a(OpenGlHelper.field_153199_f, 33190, this.framebufferTextureWidth, this.framebufferTextureHeight);
                	OpenGlHelper.func_153190_b(OpenGlHelper.field_153198_e, OpenGlHelper.field_153201_h, OpenGlHelper.field_153199_f, this.depthBuffer);
                }
                else
                {
                    OpenGlHelper.func_153186_a(OpenGlHelper.field_153199_f, org.lwjgl.opengl.EXTPackedDepthStencil.GL_DEPTH24_STENCIL8_EXT, this.framebufferTextureWidth, this.framebufferTextureHeight);
                    OpenGlHelper.func_153190_b(OpenGlHelper.field_153198_e, org.lwjgl.opengl.EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT, OpenGlHelper.field_153199_f, this.depthBuffer);
                    OpenGlHelper.func_153190_b(OpenGlHelper.field_153198_e, org.lwjgl.opengl.EXTFramebufferObject.GL_STENCIL_ATTACHMENT_EXT, OpenGlHelper.field_153199_f, this.depthBuffer);
                }
            }

            this.framebufferClear();
            this.unbindFramebufferTexture();
        }
    }

}
