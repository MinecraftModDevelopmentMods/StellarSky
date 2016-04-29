package stellarium.client;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.client.settings.KeyBinding;

//Client Only
public enum EnumKey {
	SWITCH_CUSTOMIZE_GUI("key.stellarsky.customizegui.description", Keyboard.KEY_I, "key.stellarsky"),
	KEY_NORMAL;

	private boolean isDefault;
	private String lang;
	private String categoryLang;

	private int defKey;
	private KeyBinding key;
	
	EnumKey() {
		this.isDefault = true;
	}
	
	EnumKey(String lang, int defKey, String categoryLang) {
		this.isDefault = false;
		this.lang = lang;
		this.categoryLang = categoryLang;
		this.defKey = defKey;
	}
	
	public void registerKey() {
		if(this.isDefault)
			return;
		
		this.key = new KeyBinding(this.lang, this.defKey, this.categoryLang);
		ClientRegistry.registerKeyBinding(key);
	}
	
	public boolean isThisKey(int keyCode) {
		return this.isDefault || key.getKeyCode() == keyCode;
	}
	
	public boolean isPressed() {
		return !this.isDefault && key.isPressed();
	}
}
