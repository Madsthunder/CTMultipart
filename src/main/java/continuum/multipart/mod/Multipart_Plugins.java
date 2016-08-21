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
	@Override
	public String[] getASMTransformerClass()
	{
		return new String[] {  };
	}

	@Override
	public String getModContainerClass()
	{
		return "continuum.multipart.mod.Multipart_Mod";
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
		
	}

	@Override
	public Void call() throws Exception
	{
		ApiPlugin.putAPIPackages(Sets.newHashSet("continuum.api.multipart", "continuum.api.microblocktexture"), Sets.newHashSet("continuum.multipart.plugins.MultipartAPI_Methods"));
		ApiPlugin.putAPIClass("MultipartApi", "continuum.api.multipart.MultipartApi");
		ApiPlugin.putAPIClass("MicroblockTextureApi", "continuum.api.microblocktexture.MicroblockTextureApi");
		return null;
	}
}
