package stellarium.client.gui.pos;

import stellarium.client.gui.OverlayContainer;
import stellarium.client.gui.PerOverlaySettings;
import stellarium.client.gui.IGuiOverlayType;

public class OverlayPosCfgType implements IGuiOverlayType<OverlayPosCfg, PerOverlaySettings> {

	private OverlayContainer container;
	
	public OverlayPosCfgType(OverlayContainer container) {
		this.container = container;
	}
	
	@Override
	public OverlayPosCfg generateElement() {
		return new OverlayPosCfg(this.container);
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
	public boolean accepts(EnumHorizontalPos pos) {
		return pos == EnumHorizontalPos.RIGHT;
	}

	@Override
	public boolean accepts(EnumVerticalPos pos) {
		return pos == EnumVerticalPos.CENTER;
	}

}
