package stellarium.client.gui.pos;

import stellarium.client.gui.OverlayContainer;
import stellarium.client.gui.PerOverlaySettings;
import stellarium.client.gui.IGuiOverlayType;
import stellarium.client.gui.IRawHandler;

public class OverlayPosCfgType implements IGuiOverlayType<OverlayPosCfg, PerOverlaySettings> {
	
	@Override
	public OverlayPosCfg generateElement() {
		return new OverlayPosCfg();
	}

	@Override
	public PerOverlaySettings generateSettings() {
		return new PerOverlaySettings();
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
	public boolean accepts(EnumHorizontalPos horizontal, EnumVerticalPos vertical) {
		return horizontal == EnumHorizontalPos.RIGHT && vertical == EnumVerticalPos.CENTER;
	}

	
	@Override
	public IRawHandler<OverlayPosCfg> generateRawHandler() {
		return new OverlayPosHandler();
	}

}
