package stellarium.client.gui.pos;

import stellarium.client.gui.GuiOverlayContainer;
import stellarium.client.gui.GuiOverlayElementSettings;
import stellarium.client.gui.IGuiOverlayType;

public class GuiOverlayPosCfgType implements IGuiOverlayType<GuiOverlayPosConfigurator, GuiOverlayElementSettings> {
	
	private GuiOverlayContainer container;
		
	public GuiOverlayPosCfgType(GuiOverlayContainer container) {
		this.container = container;
	}

	@Override
	public GuiOverlayPosConfigurator generateElement() {
		return new GuiOverlayPosConfigurator(this.container);
	}

	@Override
	public GuiOverlayElementSettings generateSettings() {
		return new GuiOverlayElementSettings();
	}

	@Override
	public String getName() {
		return "Position";
	}

	@Override
	public EnumHorizontalPos defaultHorizontalPos() {
		return EnumHorizontalPos.RIGHT;
	}

	@Override
	public EnumVerticalPos defaultVerticalPos() {
		return EnumVerticalPos.CENTER;
	}

	@Override
	public boolean accepts(EnumHorizontalPos pos) {
		return pos == EnumHorizontalPos.RIGHT;
	}

	@Override
	public boolean accepts(EnumVerticalPos pos) {
		return pos == EnumVerticalPos.CENTER;
	}

}
