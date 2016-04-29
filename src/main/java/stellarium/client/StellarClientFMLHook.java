package stellarium.client;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import stellarium.StellarSky;
import stellarium.client.gui.OverlayHandler;

public class StellarClientFMLHook {
	
	private KeyBinding focusGuiKey = new KeyBinding("key.stellarsky.focusgui.description", Keyboard.KEY_U, "key.stellarsky");
	
	private OverlayHandler overlay;
	
	public StellarClientFMLHook(OverlayHandler overlay) {
		ClientRegistry.registerKeyBinding(this.focusGuiKey);
		
		for(EnumKey key : EnumKey.values())
			key.registerKey();
		
		this.overlay = overlay;
	}
	
	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		if(event.phase == TickEvent.Phase.START) {
			overlay.updateOverlay();
		}
	}
	
	@SubscribeEvent
	public void onKeyInput(KeyInputEvent event) {
		if(focusGuiKey.isPressed())
			overlay.openGui(Minecraft.getMinecraft(), this.focusGuiKey);
		else {
			for(EnumKey key : EnumKey.values()) {
				if(key.isPressed()) {
					char eventChar = Keyboard.getEventCharacter();
					overlay.keyTyped(key, eventChar);
				}
			}
		}
	}

}
