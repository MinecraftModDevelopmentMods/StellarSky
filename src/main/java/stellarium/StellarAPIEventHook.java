package stellarium;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import stellarapi.api.ICelestialCoordinate;
import stellarapi.api.ISkyEffect;
import stellarapi.api.StellarAPIReference;
import stellarapi.api.event.ConstructCelestialsEvent;
import stellarapi.api.event.ResetCoordinateEvent;
import stellarapi.api.event.ResetSkyEffectEvent;
import stellarium.world.StellarDimensionManager;

public class StellarAPIEventHook {
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onConstruct(ConstructCelestialsEvent event) {
		StellarDimensionManager dimManager = StellarDimensionManager.get(event.getWorld());
		if(dimManager != null) {
			StellarAPIReference.resetCoordinate(event.getWorld());
			StellarAPIReference.resetSkyEffect(event.getWorld());
			
			ICelestialCoordinate coordinate = StellarAPIReference.getCoordinate(event.getWorld());
			ISkyEffect sky = StellarAPIReference.getSkyEffect(event.getWorld());
			
			event.getCollections().addAll(dimManager.constructCelestials(coordinate, sky));
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
