package continuum.api.microblock.texture;

import continuum.essentials.mod.APIMethodMirrorable;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;

public class MicroblockMaterialApi
{
	@APIMethodMirrorable
	public static FMLControlledNamespacedRegistry<MicroblockMaterial> getMicroblockMaterialRegistry()
	{
		return null;
	}

	@APIMethodMirrorable
	public static boolean apiActive()
	{
		return false;
	}
}
