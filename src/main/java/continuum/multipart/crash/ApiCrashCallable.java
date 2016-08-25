package continuum.multipart.crash;

import continuum.api.microblock.texture.MicroblockTextureApi;
import continuum.api.multipart.MultipartApi;
import net.minecraftforge.fml.common.ICrashCallable;

public class ApiCrashCallable implements ICrashCallable
{
	
	@Override
	public String call() throws Exception
	{
		return "Multiparts: " + (MultipartApi.apiActive() ? "En" : "Dis") + "abled, Microblocks: " + (MicroblockTextureApi.apiActive() ? "En" : "Dis") + "abled";
	}
	
	@Override
	public String getLabel()
	{
		return "\n Multipart APIs";
	}
	
}
