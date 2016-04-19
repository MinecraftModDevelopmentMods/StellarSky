package stellarium;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import stellarapi.api.event.ConstructCelestialsEvent;
import stellarapi.api.event.ResetCoordinateEvent;
import stellarapi.api.event.ResetSkyEffectEvent;
import stellarium.stellars.StellarManager;
import stellarium.stellars.view.StellarDimensionManager;

public class StellarAPIEventHook {
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onConstruct(ConstructCelestialsEvent event) {
		if(StellarManager.hasManager(event.getWorld(), event.getWorld().isRemote)) {
			
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onReset(ResetCoordinateEvent event) {
		StellarDimensionManager dimManager = StellarDimensionManager.get(event.getWorld());
		if(dimManager != null)
			event.setCoordinate(dimManager.getCoordinate());
	}
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onReset(ResetSkyEffectEvent event) {
		StellarDimensionManager dimManager = StellarDimensionManager.get(event.getWorld());
		if(dimManager != null)
			event.setSkyEffect(dimManager.getSkySet());
	}
}
