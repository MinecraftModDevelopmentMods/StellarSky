/**
 * API for Stellar Sky.
 * 2.4-2.5 changelog <p>
 *  - Introduced {@link ICelestialRenderer#renderSunriseSunsetEffect(net.minecraft.client.Minecraft, float[], float)}. <p>
 *  - Removed Per dimension resources from Stellar Sky, it moved to Stellar API. <p>
 *  - Rephrased Celestial Renderer to give actual sky color.
 * */
@API(apiVersion = "2.5", owner = "stellarsky", provides = "stellarsky|API")
package stellarium.api;

import cpw.mods.fml.common.API;
