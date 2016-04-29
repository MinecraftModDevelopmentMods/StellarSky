package stellarium.client.gui;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import stellarapi.api.lib.config.ConfigManager;
import stellarium.client.EnumKey;
import stellarium.client.gui.clock.GuiOverlayClock;
import stellarium.client.gui.clock.GuiOverlayClockSettings;
import stellarium.client.gui.clock.GuiOverlayClockType;
import stellarium.client.gui.pos.ElementPos;
import stellarium.client.gui.pos.EnumHorizontalPos;
import stellarium.client.gui.pos.EnumVerticalPos;
import stellarium.client.gui.pos.GuiOverlayPosCfgType;
import stellarium.client.gui.pos.GuiOverlayPosConfigurator;

public class GuiOverlayContainer {
	
	private int width;
	private int height;
	
	private ConfigManager guiConfig;
	private EnumGuiOverlayMode currentMode = EnumGuiOverlayMode.OVERLAY;
	private List<Delegate> elementList = Lists.newArrayList();
	
	public class Delegate<Element extends IGuiOverlayElement<Settings>, Settings extends GuiOverlayElementSettings> {
		ElementPos pos;
		final IGuiOverlayType type;
		final Element element;
		final Settings settings;
		
		private Delegate(IGuiOverlayType<Element, Settings> type) {
			this.type = type;
			this.element = type.generateElement();
			this.settings = type.generateSettings();
			settings.initializeSetttings(type.defaultHorizontalPos(), type.defaultVerticalPos());
		}
		
		private void initialize(Minecraft mc) {
			element.initialize(mc, this.settings);
			this.pos = new ElementPos(settings.horizontal, settings.vertical);
		}
		
		public boolean canSetPos(EnumHorizontalPos horizontal, EnumVerticalPos vertical) {
			if(!type.accepts(horizontal) || !type.accepts(vertical))
				return false;
			
			ElementPos pos = new ElementPos(horizontal, vertical);
			for(Delegate delegate : elementList) {
				if(delegate.equals(pos))
					return false;
			}
			
			return true;
		}
		
		public boolean trySetPos(EnumHorizontalPos horizontal, EnumVerticalPos vertical) {
			if(!canSetPos(horizontal, vertical))
				return false;
			
			ElementPos pos = new ElementPos(horizontal, vertical);
			
			settings.horizontal = pos.getHorizontalPos();
			settings.vertical = pos.getVerticalPos();
			this.pos = pos;
			return true;
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
		
		this.register(new GuiOverlayPosCfgType(this));
		this.register(new GuiOverlayClockType());
		
		for(Delegate delegate : this.elementList)
			guiConfig.register(delegate.type.getName(), delegate.settings);
	}
	
	public <
	Element extends IGuiOverlayElement<Settings>,
	Settings extends GuiOverlayElementSettings
	> void register(IGuiOverlayType<Element, Settings> type) {
		elementList.add(new Delegate(type));
	}
	
	public void initialize(Minecraft mc) {
		for(Delegate delegate : this.elementList)
			delegate.initialize(mc);
	}
	
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void switchMode(EnumGuiOverlayMode mode) {
		for(Delegate delegate : this.elementList)
			delegate.element.switchMode(mode);
	}
	
	public void updateOverlay() {
		for(Delegate delegate : this.elementList)
			delegate.element.updateOverlay();
	}
	
	public void mouseClicked(int mouseX, int mouseY, int eventButton) {
		boolean changed = false;
		for(Delegate delegate : this.elementList)
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
		for(Delegate delegate : this.elementList)
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
		
		for(Delegate delegate : this.elementList)
			changed = delegate.element.keyTyped(key, eventChar) || changed;
		
		if(changed)
			guiConfig.syncFromFields();
	}
	
	public void render(int mouseX, int mouseY, float partialTicks) {
		for(Delegate delegate : this.elementList)
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
