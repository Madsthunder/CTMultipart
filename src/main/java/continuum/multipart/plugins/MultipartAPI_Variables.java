package continuum.multipart.plugins;

import continuum.api.multipart.Multipart;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.common.registry.Registries;

public class MultipartAPI_Variables
{
	public static final FMLControlledNamespacedRegistry<Multipart> multiparts = Registries.createRegistry(Multipart.class, 0, Integer.MAX_VALUE >> 5);
}
