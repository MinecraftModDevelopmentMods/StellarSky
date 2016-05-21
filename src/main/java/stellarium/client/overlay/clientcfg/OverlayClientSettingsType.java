package stellarium.client.overlay.clientcfg;

import stellarapi.api.gui.overlay.IOverlayType;
import stellarapi.api.gui.overlay.IRawHandler;
import stellarapi.api.gui.pos.EnumHorizontalPos;
import stellarapi.api.gui.pos.EnumVerticalPos;

public class OverlayClientSettingsType implements IOverlayType<OverlayClientSettings, SettingsOverlaySettings> {

	@Override
	public OverlayClientSettings generateElement() {
		return new OverlayClientSettings();
	}

	@Override
	public SettingsOverlaySettings generateSettings() {
		return new SettingsOverlaySettings();
	}

	@Override
	public String getName() {
		return "Settings";
	}

	@Override
	public String overlayType() {
		return "Configuration";
	}

	@Override
	public EnumHorizontalPos defaultHorizontalPos() {
		return EnumHorizontalPos.LEFT;
	}

	@Override
	public EnumVerticalPos defaultVerticalPos() {
		return EnumVerticalPos.DOWN;
	}

	@Override
	public boolean accepts(EnumHorizontalPos horizontal, EnumVerticalPos vertical) {
		return horizontal == EnumHorizontalPos.LEFT;
	}

	@Override
	public IRawHandler<OverlayClientSettings> generateRawHandler() {
		return null;
	}

	@Override
	public boolean isUniversal() {
		return false;
	}

	@Override
	public boolean isOnMain() {
		return true;
	}

}
