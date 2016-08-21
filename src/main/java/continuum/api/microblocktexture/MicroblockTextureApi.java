package continuum.api.microblocktexture;

import continuum.essentials.mod.APIMethodMirrorable;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;

public class MicroblockTextureApi
{
	@APIMethodMirrorable
	public static FMLControlledNamespacedRegistry<MicroblockTextureEntry> getMicroblockTextureRegistry()
	{
		return null;
	}

	@APIMethodMirrorable
	public static boolean apiActive()
	{
		return false;
	}
}
