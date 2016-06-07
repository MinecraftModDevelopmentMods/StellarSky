package stellarium.render.stellars.atmosphere;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;

public class FramebufferCustom extends Framebuffer {

	public FramebufferCustom(int p_i45078_1_, int p_i45078_2_, boolean p_i45078_3_) {
		super(p_i45078_1_, p_i45078_2_, p_i45078_3_);
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

            this.setFramebufferFilter(9728);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.framebufferTexture);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB16, this.framebufferTextureWidth, this.framebufferTextureHeight, 0, GL11.GL_RGB, GL11.GL_FLOAT, (ByteBuffer)null);
            OpenGlHelper.func_153171_g(OpenGlHelper.field_153198_e, this.framebufferObject);
            OpenGlHelper.func_153188_a(OpenGlHelper.field_153198_e, OpenGlHelper.field_153200_g, 3553, this.framebufferTexture, 0);

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
