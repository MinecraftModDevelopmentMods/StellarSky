package stellarium.client.gui.clock;

import stellarium.client.gui.IGuiOverlayType;
import stellarium.client.gui.pos.EnumHorizontalPos;
import stellarium.client.gui.pos.EnumVerticalPos;

public class GuiOverlayClockType implements IGuiOverlayType<GuiOverlayClock, GuiOverlayClockSettings> {

	@Override
	public GuiOverlayClock generateElement() {
		return new GuiOverlayClock();
	}

	@Override
	public GuiOverlayClockSettings generateSettings() {
		return new GuiOverlayClockSettings();
	}

	@Override
	public String getName() {
		return "Clock";
	}

	@Override
	public EnumHorizontalPos defaultHorizontalPos() {
		return EnumHorizontalPos.LEFT;
	}

	@Override
	public EnumVerticalPos defaultVerticalPos() {
		return EnumVerticalPos.UP;
	}


	@Override
	public boolean accepts(EnumHorizontalPos pos) {
		return true;
	}

	@Override
	public boolean accepts(EnumVerticalPos pos) {
		return pos == EnumVerticalPos.UP;
	}

}
