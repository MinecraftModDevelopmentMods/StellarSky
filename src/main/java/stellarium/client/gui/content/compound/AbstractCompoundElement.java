package stellarium.client.gui.content.compound;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import stellarium.client.EnumKey;

public abstract class AbstractCompoundElement implements IGuiElement {

	private Minecraft mc;
	private List<Delegate> components = Lists.newArrayList();
	private int width;
	private int height;
	
	private class Delegate {
		IGuiElement element;
		float posX;
		float posY;
		
		public Delegate(IGuiElement element) {
			this.element = element;
		}
	}

	public void addElement(IGuiElement element) {
		components.add(new Delegate(element));
	}

	@Override
	public void initElement(Minecraft mc) {
		this.mc = mc;
		for(Delegate delegate : this.components)
			delegate.element.initElement(mc);
	}

	@Override
	public void tickElement() {
		for(Delegate delegate : this.components)
			delegate.element.tickElement();
	}
	
	@Override
	public void setClip(float posX1, float posY1, float posX2, float posY2) {
		for(Delegate delegate : this.components)
			delegate.element.setClip(posX1-delegate.posX, posY1-delegate.posY,
					posX2-delegate.posX, posY2-delegate.posY);
	}

	@Override
	public boolean mouseClicked(float mouseX, float mouseY, int eventButton) {
		for(Delegate delegate : this.components)
		{
			float scaledMouseX = mouseX - delegate.posX;
			float scaledMouseY = mouseY - delegate.posY;
			
			if(delegate.element.mouseClicked(scaledMouseX, scaledMouseY, eventButton))
				return true;
		}
		
		return false;
	}

	@Override
	public boolean mouseMovedOrUp(float mouseX, float mouseY, int eventButton) {
		for(Delegate delegate : this.components)
		{
			float scaledMouseX = mouseX - delegate.posX;
			float scaledMouseY = mouseY - delegate.posY;
			
			if(delegate.element.mouseClicked(scaledMouseX, scaledMouseY, eventButton))
				return true;
		}
		
		return false;
	}

	@Override
	public boolean keyTyped(EnumKey key, char eventChar) {
		for(Delegate delegate : this.components)
			if(delegate.element.keyTyped(key, eventChar))
				return true;
		
		return false;
	}

	@Override
	public void render(float mouseX, float mouseY, float partialTicks) {
		for(Delegate delegate : this.components)
		{
			GL11.glPushMatrix();
			GL11.glTranslatef(delegate.posX, delegate.posY, 0.0f);
			delegate.element.render(mouseX-delegate.posX, mouseY-delegate.posY, partialTicks);
			GL11.glPopMatrix();
		}
	}

}
