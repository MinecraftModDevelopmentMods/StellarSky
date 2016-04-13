/**
 * API for Stellar Sky.
 * 2.1-2.3 changelog
 *  - Introduced Sky Renderer Type to control sky render type via configuration.
 *     This will be added for each configuration for dimension compatible with it.
 *  - Added calculateDispersionFactor to the sky provider.
 *  - Added per dimension resource to control resources on specific dimension.
 *  - Added Light pollution rate.
 * */
@API(apiVersion = "2.2", owner = "stellarsky", provides = "stellarsky|API")
package stellarium.api;

import cpw.mods.fml.common.API;
