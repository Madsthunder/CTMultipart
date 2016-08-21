package continuum.api.multipart;

import continuum.essentials.mod.APIMethodMirrorable;
import continuum.multipart.plugins.MultipartAPI_Variables;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;

public class MultipartApi
{
	@APIMethodMirrorable
	public static FMLControlledNamespacedRegistry<Multipart> getMultipartRegistry()
	{
		return null;
	}

	@APIMethodMirrorable
	public static boolean apiActive()
	{
		return false;
	}
}
