package stellarium.client.overlay.clock;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import stellarapi.api.CelestialPeriod;
import stellarapi.api.PeriodHelper;
import stellarapi.api.gui.overlay.EnumOverlayMode;
import stellarapi.api.gui.overlay.IOverlayElement;
import stellarapi.lib.gui.GuiContent;
import stellarapi.lib.gui.GuiElement;
import stellarapi.lib.gui.GuiRenderer;
import stellarapi.lib.gui.IFontHelper;
import stellarapi.lib.gui.IGuiPosition;
import stellarapi.lib.gui.IRenderer;
import stellarapi.lib.gui.PositionSimple;
import stellarapi.lib.gui.RectangleBound;
import stellarapi.lib.gui.dynamic.GuiDynamic;
import stellarapi.lib.gui.dynamic.IDynamicController;
import stellarapi.lib.gui.dynamic.tooltip.GuiHasTooltip;
import stellarapi.lib.gui.dynamic.tooltip.ITooltipController;
import stellarapi.lib.gui.dynamic.tooltip.StringFormat;
import stellarapi.lib.gui.list.GuiHasFixedList;
import stellarapi.lib.gui.list.IHasFixedListController;
import stellarapi.lib.gui.model.basic.ModelSimpleRect;
import stellarapi.lib.gui.model.font.ModelFont;

public class OverlayClock implements IOverlayElement<ClockSettings> {
	
	private static final int HEIGHT = 40;
	private static final int ADDBTN_HEIGHT = 10;
	private static final int ADDITIONAL_HEIGHT = 65;
	private static final int ANIMATION_DURATION = 10;
	
	private Minecraft mc;
	private int animationTick = 0;
	private EnumOverlayMode currentMode = EnumOverlayMode.OVERLAY;
	private boolean needUpdate;
	private boolean controlEnabled;

	private ClockSettings settings;
	
	private GuiContent content;
	private OverlayClockTexts texts;
	private OverlayClockControllers controllers;
	
	private ModelFont font = new ModelFont(false);
	
	@Override
	public int getWidth() {
		return settings.viewMode.getGuiWidth();
	}

	@Override
	public int getHeight() {
		return HEIGHT;
	}
	
	@Override
	public void initialize(Minecraft mc, ClockSettings settings) {
		this.mc = mc;
		this.settings = settings;
		
		this.controllers = new OverlayClockControllers(settings);
		this.texts = new OverlayClockTexts(settings);
		
		this.content = new GuiContent(new GuiRenderer(mc),
				this.generateElement(),
				new PositionSimple() {
			@Override
			public RectangleBound getBound() {
				return new RectangleBound(0.0f, 0.0f, getWidth(),
						controlEnabled? HEIGHT + ADDITIONAL_HEIGHT : HEIGHT);
			}

			@Override
			public void updateBound(RectangleBound bound) {
				bound.set(0.0f, 0.0f, getWidth(),
						controlEnabled? HEIGHT + ADDITIONAL_HEIGHT : HEIGHT);
			}
		});
	}
	
	public GuiElement generateElement() {
		GuiHasTooltip tooltipRegistry = new GuiHasTooltip();
		GuiElement theContent = new GuiElement<IDynamicController>(new GuiDynamic(),
				new DynamicController(tooltipRegistry));
		
		tooltipRegistry.setWrappedGui(theContent);
		return new GuiElement<ITooltipController>(tooltipRegistry, new TooltipController());
	}
	
	public class DynamicController implements IDynamicController {
		private GuiHasTooltip registry;
		private GuiElement textElement, conElement;

		public DynamicController(GuiHasTooltip registry) {
			this.registry = registry;
			this.textElement = texts.generateElement(this.registry);
			this.conElement = controllers.generateElement(this.registry);
		}

		@Override
		public boolean needUpdate() {
			boolean flag = needUpdate;
			needUpdate = false;
			return flag;
		}

		@Override
		public GuiElement generateElement() {
			if(!currentMode.focused())
				return this.textElement;

			GuiElement combined = new GuiElement<IHasFixedListController>(
					new GuiHasFixedList(this.conElement, this.textElement, HEIGHT),
					new ListController());
			return combined;
		}
	}
	
	public class ListController implements IHasFixedListController {
		@Override
		public boolean isHorizontal() {
			return false;
		}
		@Override
		public String setupRenderer(IRenderer renderer) {
			return null;
		}
		@Override
		public boolean isModifiableFirst() {
			return false;
		}
		@Override
		public IGuiPosition wrapFixedPosition(IGuiPosition position, IGuiPosition listPos) {
			return position;
		}
		@Override
		public IGuiPosition wrapModifiablePosition(IGuiPosition position, IGuiPosition listPos) {
			return position;
		}
	}
	
	public class TooltipController implements ITooltipController {
		@Override
		public boolean hasClip() {
			return false;
		}

		@Override
		public IFontHelper lineSpecificFont(String lineContext) {
			return font;
		}

		@Override
		public String setupBackground(StringFormat info, IRenderer renderer) {
			renderer.bindModel(ModelSimpleRect.getInstance());
			renderer.pushSettingTillNextRender();
			renderer.color(0.2f, 0.2f, 0.2f, 0.2f + 0.6f * settings.alpha);
			return "";
		}

		@Override
		public List<String> getLineContext(StringFormat info) {
			String[] args = info.getMain().split("\\\n");
			List<String> arglist = Lists.newArrayList();
			for(String arg : args)
				arglist.add(I18n.format(arg));
			
			return arglist;
		}

		@Override
		public void setupTooltip(String context, IRenderer renderer) {
			renderer.bindModel(font);
			renderer.pushSettingTillNextRender();
			renderer.color(0.8f, 0.8f, 0.8f, 1.0f);
		}

		@Override
		public float getSpacingX() {
			return 2.0f;
		}

		@Override
		public float getSpacingY() {
			return 2.0f;
		}

		@Override
		public String toRenderableText(String context) {
			return context;
		}
	}

	@Override
	public float animationOffsetX(float partialTicks) {
		return 0.0f;
	}

	@Override
	public float animationOffsetY(float partialTicks) {
		partialTicks = settings.isFixed? 0.0f : currentMode.displayed()? -partialTicks : partialTicks;
		return -this.getHeight() * Math.max((this.animationTick + partialTicks) / ANIMATION_DURATION, 0.0f);
	}

	@Override
	public void switchMode(EnumOverlayMode mode) {
		if(mode.displayed() != currentMode.displayed())
		{
			if(!settings.isFixed && mode.displayed())
				this.animationTick = ANIMATION_DURATION;
			else this.animationTick = 0;
			
			if(!mode.displayed())
				controllers.setRollWithForce(true);
		}
		
		if(mode.focused() != currentMode.focused()) {
			texts.setFocused(mode.focused());
			this.needUpdate = true;
		}
		
		this.currentMode = mode;
	}

	@Override
	public void updateOverlay() {
		content.updateTick();
		texts.update(0.0f);
		
		this.controlEnabled = currentMode.focused();

		if(settings.isFixed) {
			this.animationTick = 0;
			return;
		}
		
		if(this.animationTick > 0 && currentMode.displayed())
			this.animationTick--;
		else if(this.animationTick < ANIMATION_DURATION && !currentMode.displayed())
			this.animationTick++;
	}

	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int eventButton) {
		if(!currentMode.focused())
			return false;
		
		content.mouseClicked(mouseX, mouseY, eventButton);
		return controllers.checkDirty() || texts.checkDirty();
	}

	@Override
	public boolean mouseReleased(int mouseX, int mouseY, int eventButton) {
		if(!currentMode.focused())
			return false;
		
		content.mouseReleased(mouseX, mouseY, eventButton);
		return controllers.checkDirty() || texts.checkDirty();
	}

	@Override
	public boolean keyTyped(char eventChar, int eventKey) {
		if(!currentMode.focused())
			return false;
		
		content.keyTyped(eventChar, eventKey);
		return controllers.checkDirty() || texts.checkDirty();
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		CelestialPeriod periodDay = PeriodHelper.getDayPeriod(Minecraft.getMinecraft().theWorld);
		CelestialPeriod periodYear = PeriodHelper.getYearPeriod(Minecraft.getMinecraft().theWorld);
		if(periodDay == null || periodYear == null)
			return;
		
		content.render(mouseX, mouseY, partialTicks);
		//AtmosphereRenderer.INSTANCE.renderPass(null, EnumAtmospherePass.TestAtmCache, null);
	}

	@Override
	public boolean mouseClickMove(int mouseX, int mouseY, int eventButton, long timeSinceLastClick) {
		if(!currentMode.focused())
			return false;
		
		content.mouseClickMove(mouseX, mouseY, eventButton, timeSinceLastClick);
		return controllers.checkDirty() || texts.checkDirty();
	}
}
