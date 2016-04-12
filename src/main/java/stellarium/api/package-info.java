/**
 * 2.1-2.2 changelog
 *  - Introduced Sky Renderer Type to control sky render type via configuration.
 *     This will be added for each configuration for dimension compatible with it.
 *  - Added calculateDispersionFactor to the sky provider.
 *  - Added per dimension resource to control resources on specific dimension.
 * */
@API(apiVersion = "2.2", owner = "stellarsky", provides = "stellarsky|API")
package stellarium.api;

import net.minecraftforge.fml.common.API;
