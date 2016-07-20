package continuum.multipart.mod;

import continuum.essentials.mod.CTMod;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.Name(value = "CTMultipart")
@IFMLLoadingPlugin.MCVersion(value = "1.9")
@IFMLLoadingPlugin.TransformerExclusions(value = "continuum.multipart")
@IFMLLoadingPlugin.SortingIndex(value = 2000)
@Mod(modid = "CTMultipart", name = "Continuum: Multipart", version = "0.1.0")
public class Multipart_Mod extends CTMod<Multipart_OH, Multipart_EH>
{
	public Multipart_Mod()
	{
		super(Multipart_OH.getHolder(), new Multipart_EH(), Multipart_Loaders.getLoaders());
		this.getEventHandler().setMod(this);
	}
	
	@Mod.EventHandler
	public void construction(FMLConstructionEvent event)
	{
		super.construction(event);
	}
	
	@Mod.EventHandler
	public void pre(FMLPreInitializationEvent event)
	{
		super.pre(event);
	}
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
	{
		super.init(event);
	}
	
	@Mod.EventHandler
	public void post(FMLPostInitializationEvent event)
	{
		super.post(event);
	}
}
