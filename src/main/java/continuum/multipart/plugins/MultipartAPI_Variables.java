package continuum.multipart.plugins;

import continuum.api.microblock.texture.MicroblockTextureEntry;
import continuum.api.multipart.Multipart;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.common.registry.Registries;

public class MultipartAPI_Variables
{
	public static final FMLControlledNamespacedRegistry<Multipart> multiparts = Registries.createRegistry(Multipart.class, 0, Integer.MAX_VALUE >> 5);
	public static final FMLControlledNamespacedRegistry<MicroblockTextureEntry> microblockTextureRegistry = Registries.createRegistry(MicroblockTextureEntry.class, new ResourceLocation("air"), 0, Integer.MAX_VALUE >> 5);
}
