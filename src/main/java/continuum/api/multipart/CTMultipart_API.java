package continuum.api.multipart;

import continuum.api.multipart.implementations.Multipart;
import continuum.api.multipart.registry.MicroblockTextureEntry;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import net.minecraftforge.fml.common.registry.Registries;

public class CTMultipart_API
{
	private static final FMLControlledNamespacedRegistry<Multipart> multiparts = Registries.createRegistry(Multipart.class, 0, Integer.MAX_VALUE >> 5);
	public static final FMLControlledNamespacedRegistry<MicroblockTextureEntry> microblockTextureRegistry = Registries.createRegistry(MicroblockTextureEntry.class, new ResourceLocation("ctmultipart", "null"), 0, Integer.MAX_VALUE >> 5);
	
	public static void registerMultipart(Multipart multipart)
	{
		multiparts.register(multipart);
	}
	
	public static Multipart getDefaultMultipart()
	{
		return multiparts.getDefaultValue();
	}
	
	public static Multipart getMultipart(ResourceLocation location)
	{
		return multiparts.getObject(location);
	}
	
	public Multipart getMultipart(Block block)
	{
		return getMultipart(block.getRegistryName());
	}
	
	public static Integer getID(IForgeRegistryEntry entry)
	{
		ResourceLocation location = entry.getRegistryName();
		Class clasz = entry.getRegistryType();
		if(clasz == Multipart.class) return multiparts.getId(location);
		if(clasz == MicroblockTextureEntry.class) return microblockTextureRegistry.getId(location);
		return 0;
	}
	
	public void registerMicroblock(MicroblockTextureEntry microblock)
	{
		microblockTextureRegistry.register(microblock);
	}
}
