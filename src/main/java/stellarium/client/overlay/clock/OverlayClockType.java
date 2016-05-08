package stellarium.client.overlay.clock;

import stellarapi.api.gui.overlay.IOverlayType;
import stellarapi.api.gui.overlay.IRawHandler;
import stellarapi.api.gui.pos.EnumHorizontalPos;
import stellarapi.api.gui.pos.EnumVerticalPos;

public class OverlayClockType implements IOverlayType<OverlayClock, ClockSettings> {

	@Override
	public OverlayClock generateElement() {
		return new OverlayClock();
	}

	@Override
	public ClockSettings generateSettings() {
		return new ClockSettings();
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
	public boolean accepts(EnumHorizontalPos horizontal, EnumVerticalPos vertical) {
		return vertical == EnumVerticalPos.UP;
	}
	

	@Override
	public IRawHandler generateRawHandler() {
		return null;
	}

	@Override
	public String overlayType() {
		return "Time";
	}

	@Override
	public boolean isUniversal() {
		return false;
	}

}
