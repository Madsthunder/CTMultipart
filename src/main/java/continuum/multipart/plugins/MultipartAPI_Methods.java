package continuum.multipart.plugins;

import continuum.api.microblock.texture.MicroblockTextureEntry;
import continuum.api.multipart.Multipart;
import continuum.essentials.mod.APIMethodReflectable;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;

public class MultipartAPI_Methods
{
	@APIMethodReflectable(clasz = "continuum.api.multipart.MultipartApi", method = "getMultipartRegistry")
	public static FMLControlledNamespacedRegistry<Multipart> getMultipartRegistry()
	{
		return MultipartAPI_Variables.multiparts;
	}
	
	@APIMethodReflectable(clasz = "continuum.api.multipart.MultipartApi", method = "apiActive")
	public static boolean multipartApiActive()
	{
		return true;
	}
	
	@APIMethodReflectable(clasz = "continuum.api.microblock.texture.MicroblockTextureApi", method = "getMicroblockTextureRegistry")
	public static FMLControlledNamespacedRegistry<MicroblockTextureEntry> getMicroblockTextureRegistry()
	{
		return MultipartAPI_Variables.microblockTextureRegistry;
	}
	
	@APIMethodReflectable(clasz = "continuum.api.microblock.texture.MicroblockTextureApi", method = "apiActive")
	public static boolean microblockTextureApiActive()
	{
		return true;
	}
}
