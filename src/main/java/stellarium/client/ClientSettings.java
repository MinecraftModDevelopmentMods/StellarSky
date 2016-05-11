package stellarium.client;

import net.minecraftforge.common.config.Configuration;
import stellarapi.api.lib.config.SimpleHierarchicalConfig;
import stellarapi.api.lib.config.property.ConfigPropertyDouble;
import stellarium.stellars.layer.StellarLayerRegistry;

public class ClientSettings extends SimpleHierarchicalConfig {
	
	public float mag_Limit;
	
	private ConfigPropertyDouble propMagLimit;
	//private ConfigPropertyString propLockBtnPosition;
	
	//private EnumLockBtnPosition btnPosition = EnumLockBtnPosition.UPRIGHT;
	
	private boolean isDirty = false;
	
	public ClientSettings() {
		StellarLayerRegistry.getInstance().composeSettings(this);
		
		this.propMagLimit = new ConfigPropertyDouble("Mag_Limit", "", 6.0);
		//this.propLockBtnPosition = new ConfigPropertyString("Lock_Button_Position", "", btnPosition.getName());
		
		this.addConfigProperty(this.propMagLimit);
		//this.addConfigProperty(this.propLockBtnPosition);
	}
	
	@Override
	public void setupConfig(Configuration config, String category) {
        config.setCategoryComment(category, "Configurations for client modifications.\n"
        		+ "Most of them are for rendering/view.");
        config.setCategoryLanguageKey(category, "config.category.client");
        config.setCategoryRequiresMcRestart(category, false);
		
        super.setupConfig(config, category);
        
        propMagLimit.setComment("Limit of magnitude can be seen on naked eye.\n" +
        		"If you want to increase FPS, lower the Mag_Limit.\n" +
        		"(Realistic = 6.5, Default = 6.0)\n" +
        		"The lower you set it, the fewer stars you will see\n" +
        		"but the better FPS you will get");
        propMagLimit.setRequiresMcRestart(true);
        propMagLimit.setLanguageKey("config.property.client.maglimit");
        propMagLimit.setMinValue(2.0);
        propMagLimit.setMaxValue(7.0);
        
        /*propLockBtnPosition.setValidValues(EnumLockBtnPosition.names);
        propLockBtnPosition.setComment("Position of sky lock button.\n"
        		+ "Now there are upright and downleft.");
        propLockBtnPosition.setRequiresMcRestart(false);
        propLockBtnPosition.setLanguageKey("config.property.client.lockbtnpos");*/
	}

	@Override
	public void loadFromConfig(Configuration config, String category) {
		super.loadFromConfig(config, category);
		
		this.mag_Limit=(float)propMagLimit.getDouble();
        
        //this.btnPosition = EnumLockBtnPosition.getModeForName(propLockBtnPosition.getString());
        
        this.isDirty = true;
	}
	
	@Override
	public void saveToConfig(Configuration config, String category) {
		//propLockBtnPosition.setString(btnPosition.getName());
		super.saveToConfig(config, category);
	}
	
	public boolean checkDirty() {
		boolean flag = this.isDirty;
		this.isDirty = false;
		return flag;
	}
	
	/*public EnumLockBtnPosition getBtnPosition() {
		return this.btnPosition;
	}*/
}
