package continuum.multipart.mod;

import java.util.Map;

import com.google.common.collect.Sets;

import continuum.core.plugins.transformers.ApiPlugin;
import net.minecraftforge.fml.relauncher.IFMLCallHook;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.SortingIndex(1000)
@IFMLLoadingPlugin.Name("CTMultipart")
@IFMLLoadingPlugin.MCVersion("1.10.2")
@IFMLLoadingPlugin.TransformerExclusions({ "org", "com", "joptsimple", "oshi" })
public class Multipart_Plugins implements IFMLLoadingPlugin, IFMLCallHook
{
	private static boolean multiparts = true;
	private static boolean microblocks = true;
	
	@Override
	public String[] getASMTransformerClass()
	{
		return new String[] {  };
	}

	@Override
	public String getModContainerClass()
	{
		return null;
	}

	@Override
	public String getSetupClass()
	{
		return "continuum.multipart.mod.Multipart_Plugins";
	}

	@Override
	public String getAccessTransformerClass()
	{
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data)
	{
		multiparts = !Boolean.valueOf(String.valueOf(data.get("disableMultiparts")));
		microblocks = !Boolean.valueOf(String.valueOf(data.get("disableMicroblocks")));
	}

	@Override
	public Void call() throws Exception
	{
		ApiPlugin.putAPIPackages(Sets.newHashSet("continuum.api.multipart", "continuum.api.microblock", "continuum.api.microblock.texture", "continuum.api.microblock.compat"), Sets.newHashSet("continuum.multipart.plugins.MultipartApis"));
		if(multiparts)
			ApiPlugin.putAPIClass("MultipartApi", "continuum.api.multipart.MultipartApi");
		if(microblocks)
		{
			ApiPlugin.putAPIClass("MicroblockApi", "continuum.api.microblock.MicroblockApi");
			ApiPlugin.putAPIClass("MicroblockMaterialApi", "continuum.api.microblock.texture.MicroblockMaterialApi");
		}
		if(multiparts && microblocks)
			ApiPlugin.putAPIClass("MultipartCompat", "continuum.api.microblock.compat.MultipartCompat");
		return null;
	}
}
