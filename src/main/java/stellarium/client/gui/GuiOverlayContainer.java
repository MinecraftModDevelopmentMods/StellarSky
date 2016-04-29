package stellarium.client.gui;

import java.util.Set;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Sets;

import net.minecraft.client.Minecraft;
import stellarapi.api.lib.config.ConfigManager;
import stellarium.client.EnumKey;
import stellarium.client.gui.clock.GuiOverlayClock;
import stellarium.client.gui.clock.GuiOverlayClockSettings;
import stellarium.client.gui.clock.GuiOverlayClockType;
import stellarium.client.gui.pos.ElementPos;

public class GuiOverlayContainer {
	
	private int width;
	private int height;
	
	private ConfigManager guiConfig;
	private EnumGuiOverlayMode currentMode = EnumGuiOverlayMode.OVERLAY;
	private Set<Delegate> elementSet = Sets.newHashSet();
	
	private class Delegate<Element extends IGuiOverlayElement<Settings>, Settings extends GuiOverlayElementSettings> {
		ElementPos pos;
		IGuiOverlayType type;
		Element element;
		Settings settings;
		
		Delegate(IGuiOverlayType<Element, Settings> type) {
			this.type = type;
			this.pos = new ElementPos(type.defaultHorizontalPos(), type.defaultVerticalPos());
			this.element = type.generateElement();
			this.settings = type.generateSettings();
		}
		
		public void initialize(Minecraft mc) {
			element.initialize(mc, this.settings);
		}
		
		@Override
		public int hashCode() {
			return pos.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof ElementPos) {
				return pos.equals(obj);
			} else if(obj instanceof Delegate) {
				return pos.equals(((Delegate) obj).pos);
				
			} return false;
		}
	}
	
	public GuiOverlayContainer(ConfigManager guiConfig) {
		this.guiConfig = guiConfig;
		
		elementSet.add(new Delegate<GuiOverlayClock, GuiOverlayClockSettings>(new GuiOverlayClockType()));
		
		for(Delegate delegate : this.elementSet)
			guiConfig.register(delegate.type.getName(), delegate.settings);
	}
	
	public void initialize(Minecraft mc) {
		for(Delegate delegate : this.elementSet)
			delegate.initialize(mc);
	}
	
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void switchMode(EnumGuiOverlayMode mode) {
		for(Delegate delegate : this.elementSet)
			delegate.element.switchMode(mode);
	}
	
	public void updateOverlay() {
		for(Delegate delegate : this.elementSet)
			delegate.element.updateOverlay();
	}
	
	public void mouseClicked(int mouseX, int mouseY, int eventButton) {
		boolean changed = false;
		for(Delegate delegate : this.elementSet)
		{
			ElementPos pos = delegate.pos;
			IGuiOverlayElement element = delegate.element;
			int width = element.getWidth();
			int height = element.getHeight();
			int scaledMouseX = pos.getHorizontalPos().translateInto(mouseX, this.width, width);
			int scaledMouseY = pos.getVerticalPos().translateInto(mouseY, this.height, height);
			scaledMouseX -= element.animationOffsetX(0.0f);
			scaledMouseY -= element.animationOffsetY(0.0f);
			
			changed = element.mouseClicked(scaledMouseX, scaledMouseY, eventButton) || changed;
		}
		
		if(changed)
			guiConfig.syncFromFields();
	}
	
	public void mouseMovedOrUp(int mouseX, int mouseY, int eventButton) {
		boolean changed = false;
		for(Delegate delegate : this.elementSet)
		{
			ElementPos pos = delegate.pos;
			IGuiOverlayElement element = delegate.element;
			int width = element.getWidth();
			int height = element.getHeight();
			int scaledMouseX = pos.getHorizontalPos().translateInto(mouseX, this.width, width);
			int scaledMouseY = pos.getVerticalPos().translateInto(mouseY, this.height, height);
			scaledMouseX -= element.animationOffsetX(0.0f);
			scaledMouseY -= element.animationOffsetY(0.0f);
			
			changed = element.mouseMovedOrUp(scaledMouseX, scaledMouseY, eventButton) || changed;
		}
		
		if(changed)
			guiConfig.syncFromFields();
	}
	
	public void keyTyped(EnumKey key, char eventChar) {
		boolean changed = false;
		
		for(Delegate delegate : this.elementSet)
			changed = delegate.element.keyTyped(key, eventChar) || changed;
		
		if(changed)
			guiConfig.syncFromFields();
	}
	
	public void render(int mouseX, int mouseY, float partialTicks) {
		for(Delegate delegate : this.elementSet)
		{
			ElementPos pos = delegate.pos;
			IGuiOverlayElement element = delegate.element;
			int width = element.getWidth();
			int height = element.getHeight();
			float animationOffsetX = element.animationOffsetX(partialTicks);
			float animationOffsetY = element.animationOffsetY(partialTicks);
			
			int scaledMouseX = pos.getHorizontalPos().translateInto(mouseX, this.width, width);
			int scaledMouseY = pos.getVerticalPos().translateInto(mouseY, this.height, height);
			scaledMouseX -= animationOffsetX;
			scaledMouseY -= animationOffsetY;
			
			GL11.glPushMatrix();
			GL11.glTranslatef(pos.getHorizontalPos().getOffset(this.width, width) + animationOffsetX,
					pos.getVerticalPos().getOffset(this.width, width) + animationOffsetY,
					0.0f);
			
			element.render(scaledMouseX, scaledMouseY, partialTicks);
			GL11.glPopMatrix();
		}
	}
}
