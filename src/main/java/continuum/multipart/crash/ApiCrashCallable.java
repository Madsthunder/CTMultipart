package continuum.multipart.crash;

import continuum.api.microblock.Microblock;
import continuum.api.microblock.texture.MicroblockMaterial;
import net.minecraftforge.fml.common.ICrashCallable;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ApiCrashCallable implements ICrashCallable
{
	@Override
	public String call() throws Exception
	{
		return "Multiparts: " + (GameRegistry.findRegistry(Microblock.class) != null ? "En" : "Dis") + "abled, Microblocks: " + (GameRegistry.findRegistry(MicroblockMaterial.class) != null ? "En" : "Dis") + "abled";
	}
	
	@Override
	public String getLabel()
	{
		return "\n Multipart APIs";
	}
}
