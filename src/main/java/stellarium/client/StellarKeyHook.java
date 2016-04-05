package stellarium.client;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import stellarium.StellarSky;

public class StellarKeyHook {
	
	private KeyBinding modeKey = new KeyBinding("key.stellarsky.viewmod.description", Keyboard.KEY_U, "key.stellarsky");
	
	public StellarKeyHook() {
		ClientRegistry.registerKeyBinding(this.modeKey);
	}
	
	@SubscribeEvent
	public void onKeyInput(KeyInputEvent event) {
		if(modeKey.isPressed()) {
			StellarSky.proxy.getClientSettings().incrementViewMode();
		}
	}

}
