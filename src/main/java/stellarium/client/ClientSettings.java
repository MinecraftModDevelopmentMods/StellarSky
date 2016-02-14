package stellarium.client;

import stellarium.StellarSky;
import stellarium.config.EnumViewMode;

public class ClientSettings {

	public float mag_Limit;
	public int imgFrac;
	public float turb;
	public double minuteLength;
	public int anHourToMinute;
	
	private EnumViewMode viewMode = EnumViewMode.EMPTY;
	
	public void incrementViewMode() {
		this.viewMode = viewMode.nextMode();
		StellarSky.proxy.getCfgManager().syncFromFields();
	}
	
	public EnumViewMode getViewMode() {
		return this.viewMode;
	}
	
	public void setViewMode(EnumViewMode mode) {
		this.viewMode = mode;
	}
	
}
