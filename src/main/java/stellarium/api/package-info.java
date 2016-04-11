/**
 * API for Stellar Sky.
 * 2.0 changelog
 *  - Introduced World provider replacer(IWorldProviderReplacer) to handle types of WorldProvider.
 *  - Removed SkyProviderGetter.
 *  - Introduced stellar world provider interface to let world provider interact with sky provider.
 *  - Added more comments(javadocs) to explain this API.
 * */
@API(apiVersion = "2.0", owner = "stellarsky", provides = "stellarsky|API")
package stellarium.api;

import net.minecraftforge.fml.common.API;
