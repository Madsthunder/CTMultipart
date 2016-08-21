package continuum.multipart.plugins;

import continuum.api.multipart.Multipart;
import continuum.essentials.mod.APIMethodReflectable;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;

public class MultipartAPI_Methods
{
	@APIMethodReflectable(clasz = "continuum.api.multipart.MultipartAPI", method = "getMultipartRegistry")
	public static FMLControlledNamespacedRegistry<Multipart> getMultipartRegistry()
	{
		return MultipartAPI_Variables.multiparts;
	}
	
	@APIMethodReflectable(clasz = "continuum.api.multipart.MultipartAPI", method = "apiActive")
	public static boolean apiActive()
	{
		return true;
	}
}
