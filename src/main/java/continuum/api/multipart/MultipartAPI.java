package continuum.api.multipart;

import continuum.essentials.mod.APIMethodMirrorable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.common.registry.Registries;

public class MultipartAPI
{
	public static final FMLControlledNamespacedRegistry<MicroblockTextureEntry> microblockTextureRegistry = Registries.createRegistry(MicroblockTextureEntry.class, new ResourceLocation("ctmultipart", "null"), 0, Integer.MAX_VALUE >> 5);

	@APIMethodMirrorable
	public static FMLControlledNamespacedRegistry<Multipart> getMultipartRegistry()
	{
		return null;
	}
	
	public void registerMicroblock(MicroblockTextureEntry microblock)
	{
		microblockTextureRegistry.register(microblock);
	}

	@APIMethodMirrorable
	public static boolean apiActive()
	{
		return false;
	}
}
