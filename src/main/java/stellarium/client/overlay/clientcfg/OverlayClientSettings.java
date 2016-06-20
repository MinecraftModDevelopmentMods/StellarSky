package stellarium.client.overlay.clientcfg;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigCategory;
import stellarapi.api.gui.overlay.EnumOverlayMode;
import stellarapi.api.gui.overlay.IOverlayElement;
import stellarapi.api.gui.pos.EnumHorizontalPos;
import stellarapi.api.gui.pos.EnumVerticalPos;
import stellarapi.api.lib.config.ConfigManager;
import stellarapi.lib.gui.GuiContent;
import stellarapi.lib.gui.GuiElement;
import stellarapi.lib.gui.GuiRenderer;
import stellarapi.lib.gui.IGuiPosition;
import stellarapi.lib.gui.IRectangleBound;
import stellarapi.lib.gui.IRenderer;
import stellarapi.lib.gui.RectangleBound;
import stellarapi.lib.gui.button.GuiButtonSimple;
import stellarapi.lib.gui.button.IButtonController;
import stellarapi.lib.gui.dynamic.GuiDynamic;
import stellarapi.lib.gui.dynamic.IDynamicController;
import stellarapi.lib.gui.dynamic.tooltip.ITooltipElementController;
import stellarapi.lib.gui.dynamic.tooltip.StringFormat;
import stellarapi.lib.gui.model.basic.ModelSimpleRect;
import stellarapi.lib.gui.spacing.GuiSpacing;
import stellarapi.lib.gui.spacing.ISpacingController;
import stellarium.ClientProxy;
import stellarium.StellarSky;
import stellarium.client.lib.gui.HierarchyUtil;
import stellarium.client.lib.gui.IHierarchyElement;
import stellarium.client.lib.gui.IRollHelper;
import stellarium.client.lib.gui.RollHierarchyController;
import stellarium.client.overlay.clientcfg.category.CategoryElementSimple;
import stellarium.client.overlay.clientcfg.model.ModelBackground;
import stellarium.client.overlay.clientcfg.model.ModelCfgFixButton;
import stellarium.client.overlay.clientcfg.model.ModelLockButton;
import stellarium.stellars.StellarManager;

public class OverlayClientSettings implements IOverlayElement<SettingsOverlaySettings>, IRollHelper {

	private static final int ANIMATION_DURATION = 10;
	private static final int SPACING = 2;
	
	private Minecraft mc;
	private SettingsOverlaySettings settings;
	
	private ICfgHierarchyHandler handler;
	private RollHierarchyController controller;
	
	private ConfigManager notified;
	private GuiContent gui;
	
	private EnumHorizontalPos currentHorizontal;
	private EnumVerticalPos currentVertical;
	
	private float animationTick;
	private EnumOverlayMode currentMode = EnumOverlayMode.OVERLAY;
	
	@Override
	public void initialize(Minecraft mc, SettingsOverlaySettings settings) {
		this.mc = mc;
		this.settings = settings;
		this.notified = StellarSky.instance.getCelestialConfigManager();
		
		this.handler = new DefCfgHierarchyHandler(SettingSpecific.generateModel(), this);
		
		ConfigCategory category = notified.getConfig().getCategory(ClientProxy.clientConfigCategory);
		
		ICfgTooltipHandler tooltip = new DefaultTooltipController();
		
		GuiElement fixedButton = tooltip.wrapElement(
				new GuiElement<IButtonController>(new GuiButtonSimple(), new FixButtonController()),
				"config.property.gui.settings.fix" + CfgConstants.SUFFIX_TOOLTIP);
		
		LockButtonController lockBtn = new LockButtonController();
		GuiElement lockButton = tooltip.wrapElement(
				new GuiElement<IButtonController>(new GuiButtonSimple(), lockBtn), lockBtn);
		
		IHierarchyElement main = new CategoryElementSimple(category, this.handler, tooltip, true);
		main = HierarchyUtil.prepend(main, HierarchyUtil.fromElement(fixedButton, CfgConstants.ELEMENT_SIZE, CfgConstants.SPACING));
		main = HierarchyUtil.append(main, HierarchyUtil.fromElement(lockButton, CfgConstants.ELEMENT_SIZE, CfgConstants.SPACING));
		
		this.controller = new RollHierarchyController(main, false, this, CfgConstants.SPACING);

		GuiElement clientSettings = new GuiElement<IDynamicController>(new GuiDynamic(), this.controller);
		GuiElement spaced = new GuiElement<ISpacingController>(new GuiSpacing(clientSettings),
				new ISpacingController() {
			@Override
			public String setupSpacingRenderer(IRenderer renderer) {
				renderer.bindModel(ModelBackground.getInstance());
				renderer.color(1.0f, 1.0f, 1.0f, 0.5f);
				return rollToRight()? "left" : "right";
			}

			@Override
			public float getSpacingLeft() {
				return 0;
			}

			@Override
			public float getSpacingRight() {
				return CfgConstants.BACKGROUND_SPACING;
			}

			@Override
			public float getSpacingUp() {
				return CfgConstants.BACKGROUND_SPACING;
			}

			@Override
			public float getSpacingDown() {
				return CfgConstants.BACKGROUND_SPACING;
			}
		});
		
		tooltip.setWrappedGui(spaced);
		
		this.gui = new GuiContent(new GuiRenderer(mc),
				tooltip.generateGui(),
				new IGuiPosition() {
			private RectangleBound main, tooltip;
					@Override
					public IRectangleBound getElementBound() {
						return this.main;
					}

					@Override
					public IRectangleBound getClipBound() {
						return this.main;
					}

					@Override
					public IRectangleBound getAdditionalBound(String boundName) {
						if(boundName.equals("tooltip"))
							return this.tooltip;
						else return null;
					}

					@Override
					public void initializeBounds() {
						this.main = new RectangleBound(0, 0, getWidth(), getHeight());
						this.tooltip = new RectangleBound(0, 0, Float.MAX_VALUE, getHeight());
					}

					@Override
					public void updateBounds() {
						main.set(0, 0, getWidth(), getHeight());
						tooltip.set(0, 0, Float.MAX_VALUE, getHeight());
					}

					@Override
					public void updateAnimation(float partialTicks) {
						this.updateBounds();
					}
		});
	}

	@Override
	public int getWidth() {
		return (int) (CfgConstants.ELEMENT_SIZE + 2 * CfgConstants.SPACING + CfgConstants.BACKGROUND_SPACING);
	}

	@Override
	public int getHeight() {
		return (int) (controller.rollSize() + 2 * CfgConstants.BACKGROUND_SPACING);
	}

	@Override
	public float animationOffsetX(float partialTicks) {
		partialTicks = settings.isFixed? 0.0f : currentMode.displayed()? -partialTicks : partialTicks;
		return -(this.getWidth() + CfgConstants.SPACING) * Math.max((this.animationTick + partialTicks) / ANIMATION_DURATION, 0.0f);
	}

	@Override
	public float animationOffsetY(float partialTicks) {
		return 0;
	}

	@Override
	public void switchMode(EnumOverlayMode mode) {
		if(mode.displayed() != currentMode.displayed()) {
			if(!settings.isFixed && mode.displayed())
				this.animationTick = ANIMATION_DURATION;
			else this.animationTick = 0;
		}
		
		if(!settings.isFixed && !mode.focused() && currentMode.focused())
			controller.forceRoll();
		
		this.currentMode = mode;
	}

	@Override
	public void updateOverlay() {
		if(this.currentHorizontal != settings.getHorizontal() || this.currentVertical != settings.getVertical()) {
			controller.forceUpdate();
			this.currentHorizontal = settings.getHorizontal();
			this.currentVertical = settings.getVertical();
		}
		
		gui.updateTick();
		
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
		
		gui.mouseClicked(mouseX, mouseY, eventButton);
		if(controller.checkSettingsChanged())
			notified.syncFromGUI();
		return false;
	}

	@Override
	public boolean mouseReleased(int mouseX, int mouseY, int eventButton) {
		if(!currentMode.focused())
			return false;
		
		gui.mouseReleased(mouseX, mouseY, eventButton);
		if(controller.checkSettingsChanged())
			notified.syncFromGUI();
		return false;
	}

	@Override
	public boolean keyTyped(char eventChar, int eventKey) {
		if(!currentMode.focused())
			return false;
		
		gui.keyTyped(eventChar, eventKey);
		if(controller.checkSettingsChanged())
			notified.syncFromGUI();
		return false;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		gui.render(mouseX, mouseY, partialTicks);
	}


	@Override
	public boolean isDirectionInverted(boolean isHorizontal) {
		return isHorizontal && this.rollToRight() || !isHorizontal && this.rollToDown();
	}

	@Override
	public boolean rollToRight() {
		return settings.getHorizontal() == EnumHorizontalPos.RIGHT;
	}

	@Override
	public boolean rollToDown() {
		return settings.getVertical() == EnumVerticalPos.DOWN;
	}

	@Override
	public float rollBtnSize() {
		return CfgConstants.SCROLL_BTN_PROG_SIZE;
	}
	
	@Override
	public boolean hasRollButton() {
		return false;
	}

	@Override
	public void setupRollBtnRenderer(boolean isHorizontal, boolean mouseOver, IRenderer renderer) { }

	@Override
	public String setupRollBtnOverlay(boolean isHorizontal, boolean mouseOver, IRenderer renderer) {
		return null;
	}

	@Override
	public String setupRollBtnMain(boolean isHorizontal, boolean mouseOver, IRenderer renderer) {
		return null;
	}
	
	
	@Override
	public void setupSpacingBtnRenderer(boolean isHorizontal, boolean mouseOver, IRenderer renderer) { }

	@Override
	public String setupSpacingBtnOverlay(boolean isHorizontal, boolean mouseOver, IRenderer renderer) {
		if(mouseOver) {
			renderer.bindModel(ModelSimpleRect.getInstance());
			renderer.color(1.0f, 1.0f, 1.0f, 0.11f);
			return "";
		} else return null;
	}

	@Override
	public String setupSpacingBtnMain(boolean isHorizontal, boolean mouseOver, IRenderer renderer) {
		return null;
	}
	
	
	public class FixButtonController implements IButtonController {
		@Override
		public boolean canClick(int eventButton) {
			return eventButton == 0;
		}

		@Override
		public void onClicked(int eventButton) {
			settings.isFixed = !settings.isFixed;
		}

		@Override
		public void onClickEnded(int eventButton) { }

		@Override
		public void setupRenderer(boolean mouseOver, IRenderer renderer) {
			renderer.bindModel(ModelCfgFixButton.getInstance());
		}

		@Override
		public String setupOverlay(boolean mouseOver, IRenderer renderer) {
			if(mouseOver)
				return "select";
			else return null;
		}

		@Override
		public String setupMain(boolean mouseOver, IRenderer renderer) {
			return settings.isFixed? "fixed" : "unfixed";
		}
	}
	
	public class LockButtonController implements IButtonController, ITooltipElementController {
		@Override
		public boolean canClick(int eventButton) {
			return eventButton == 0;
		}

		@Override
		public void onClicked(int eventButton) {
			final boolean locked = StellarManager.getClientManager().isLocked();
			String message = locked? "stellarsky.gui.unlock" : "stellarsky.gui.lock";
			
			mc.displayGuiScreen(new GuiYesNo(new GuiYesNoCallback() {
				@Override
				public void confirmClicked(boolean ok, int index) {
					if(ok)
						mc.thePlayer.sendChatMessage(
								String.format("/locksky %s", !locked));
					mc.displayGuiScreen(null);
				}
			}, I18n.format(message), I18n.format(message + ".description"), eventButton));			
		}

		@Override
		public void onClickEnded(int eventButton) { }

		@Override
		public void setupRenderer(boolean mouseOver, IRenderer renderer) {
			renderer.bindModel(ModelLockButton.getInstance());
		}

		@Override
		public String setupOverlay(boolean mouseOver, IRenderer renderer) {
			if(mouseOver)
				return "select";
			else return null;
		}

		@Override
		public String setupMain(boolean mouseOver, IRenderer renderer) {
			boolean locked = StellarManager.getClientManager().isLocked();
			return locked? "locked" : "unlocked";
		}

		@Override
		public boolean canDisplayTooltip() {
			return true;
		}

		@Override
		public int getTooltipDisplayWaitTime() {
			return CfgConstants.WAIT_TIME_FAST_TOOLTIP;
		}

		@Override
		public StringFormat getTooltipInfo(float ratioX, float ratioY) {
			boolean locked = StellarManager.getClientManager().isLocked();
			return new StringFormat(DefaultTooltipController.formatWarning + (locked? "stellarsky.gui.unlock" : "stellarsky.gui.lock")
					+ CfgConstants.SUFFIX_TOOLTIP);
		}
	}

	@Override
	public long hoverUpdateDelay() {
		return CfgConstants.WAIT_TIME_SLOW_TOOLTIP;
	}

	@Override
	public boolean mouseClickMove(int mouseX, int mouseY, int eventButton, long timeSinceLastClick) {
		if(!currentMode.focused())
			return false;
		
		gui.mouseClickMove(mouseX, mouseY, eventButton, timeSinceLastClick);
		if(controller.checkSettingsChanged())
			notified.syncFromGUI();
		return false;
	}
}
