package stellarium.client.gui.content.button;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import stellarium.client.gui.content.util.GuiUtil;

public class GuiButtonTextured extends GuiButton {

	private ResourceLocation location;
	public float alpha;
	
	public GuiButtonTextured(int id, int xPos, int yPos, int width, int height, ResourceLocation location) {
		super(id, xPos, yPos, width, height, "");
		this.location = location;
	}
	
    /**
     * Draws this button to the screen.
     */
    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            this.field_146123_n = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int k = this.getHoverState(this.field_146123_n);
            GL11.glEnable(GL11.GL_BLEND);
            switch(k) {
            case 0:
            	GL11.glDisable(GL11.GL_TEXTURE_2D);
            	OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                GL11.glColor4f(0.0f, 0.0f, 0.0f, alpha);
                GuiUtil.drawRectSimple(xPosition, yPosition, width, height);
                GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha);
            	GL11.glEnable(GL11.GL_TEXTURE_2D);
            	break;
            case 1:
                GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha);
            	break;
            case 2:
                GL11.glColor4f(0.8f, 0.8f, 1.0f, alpha);
            	break;
            }
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        	mc.getTextureManager().bindTexture(location);
            GuiUtil.drawTexturedRectSimple(xPosition, yPosition, width, height);
            this.mouseDragged(mc, mouseX, mouseY);
        }
    }

}
