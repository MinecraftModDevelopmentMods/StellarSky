package stellarium.client.gui;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import stellarapi.api.lib.config.ConfigManager;
import stellarium.client.EnumKey;
import stellarium.client.gui.clock.OverlayClockType;
import stellarium.client.gui.pos.ElementPos;
import stellarium.client.gui.pos.EnumHorizontalPos;
import stellarium.client.gui.pos.EnumVerticalPos;
import stellarium.client.gui.pos.OverlayPosCfgType;

public class OverlayContainer {
	
	private int width;
	private int height;
	private double scale;
	
	private ConfigManager guiConfig;
	private EnumOverlayMode currentMode = EnumOverlayMode.OVERLAY;
	private List<Delegate> elementList = Lists.newArrayList();
	private ScaledResolution resolution;
	
	public class Delegate<Element extends IGuiOverlay<Settings>, Settings extends PerOverlaySettings> {
		ElementPos pos;
		final IGuiOverlayType<Element, Settings> type;
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
	
	public OverlayContainer(ConfigManager guiConfig) {
		this.guiConfig = guiConfig;
		
		this.register(new OverlayPosCfgType(this));
		this.register(new OverlayClockType());
		
		for(Delegate delegate : this.elementList)
			guiConfig.register(delegate.type.getName(), delegate.settings);
	}
	
	public <E extends IGuiOverlay<S>, S extends PerOverlaySettings> void register(IGuiOverlayType<E, S> type) {
		elementList.add(new Delegate<E, S>(type));
	}
	
	public void initialize(Minecraft mc) {
		for(Delegate delegate : this.elementList)
			delegate.initialize(mc);
	}
	
	public void setSize(ScaledResolution resolution) {
		this.width = resolution.getScaledWidth();
		this.height = resolution.getScaledHeight();
		this.resolution = resolution;
	}

	public void switchMode(EnumOverlayMode mode) {
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
			IGuiOverlay element = delegate.element;
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
			IGuiOverlay element = delegate.element;
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
			IGuiOverlay element = delegate.element;
			int width = element.getWidth();
			int height = element.getHeight();
			float animationOffsetX = element.animationOffsetX(partialTicks);
			float animationOffsetY = element.animationOffsetY(partialTicks);
			
			int scaledMouseX = pos.getHorizontalPos().translateInto(mouseX, this.width, width);
			int scaledMouseY = pos.getVerticalPos().translateInto(mouseY, this.height, height);
			scaledMouseX -= animationOffsetX;
			scaledMouseY -= animationOffsetY;
			
			GL11.glPushMatrix();
			GL11.glTranslatef((pos.getHorizontalPos().getOffset(this.width, width) + animationOffsetX) * resolution.getScaleFactor(),
					(pos.getVerticalPos().getOffset(this.width, width) + animationOffsetY) * resolution.getScaleFactor(),
					0.0f);
			
			element.render(scaledMouseX, scaledMouseY, partialTicks);
			GL11.glPopMatrix();
		}
	}
}
