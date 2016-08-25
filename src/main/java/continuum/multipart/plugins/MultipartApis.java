package continuum.multipart.plugins;

import continuum.api.microblock.texture.MicroblockMaterial;
import continuum.api.multipart.Multipart;
import continuum.essentials.mod.APIMethodReflectable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.common.registry.Registries;

public class MultipartApis
{
	public static final FMLControlledNamespacedRegistry<Multipart> multiparts = Registries.createRegistry(Multipart.class, 0, Integer.MAX_VALUE >> 5);
	public static final FMLControlledNamespacedRegistry<MicroblockMaterial> microblockTextureRegistry = Registries.createRegistry(MicroblockMaterial.class, new ResourceLocation("air"), 0, Integer.MAX_VALUE >> 5);
	
	@APIMethodReflectable(clasz = "continuum.api.multipart.MultipartApi", method = "getMultipartRegistry")
	public static FMLControlledNamespacedRegistry<Multipart> getMultipartRegistry()
	{
		return multiparts;
	}
	
	@APIMethodReflectable(clasz = "continuum.api.multipart.MultipartApi", method = "apiActive")
	public static boolean multipartApiActive()
	{
		return true;
	}
	
	@APIMethodReflectable(clasz = "continuum.api.microblock.texture.MicroblockMaterialApi", method = "getMicroblockMaterialRegistry")
	public static FMLControlledNamespacedRegistry<MicroblockMaterial> getMicroblockMaterialRegistry()
	{
		return microblockTextureRegistry;
	}
	
	@APIMethodReflectable(clasz = "continuum.api.microblock.texture.MicroblockMaterialApi", method = "apiActive")
	public static boolean microblockTextureApiActive()
	{
		return true;
	}
}
