package continuum.multipart.mod;

import continuum.essentials.mod.CTMod;
import continuum.essentials.mod.ObjectLoader;
import continuum.multipart.crash.ApiCrashCallable;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "ctmultipart", name = "Continuum: Multipart", version = "0.1.0")
public class Multipart_Mod extends CTMod<Multipart_OH, Multipart_EH>
{
	public Multipart_Mod()
	{
		super(Multipart_OH.INSTANCE, new ObjectLoader[0]);
		this.getObjectHolder().setCTMultipart(this);
		FMLCommonHandler.instance().registerCrashCallable(new ApiCrashCallable());
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
		Multipart_Proxies.INSTANCE.pre(this);
	}
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
	{
		super.init(event);
		Multipart_Proxies.INSTANCE.init(this);
	}
	
	@Mod.EventHandler
	public void post(FMLPostInitializationEvent event)
	{
		super.post(event);
		Multipart_Proxies.INSTANCE.post(this);
	}
}
