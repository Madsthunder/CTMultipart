package continuum.multipart.mod;

import continuum.essentials.mod.CTMod;
import continuum.multipart.crash.ApiCrashCallable;
import net.minecraft.crash.CrashReport;
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
		super(Multipart_OH.getObjectHolder(), Multipart_EH.getEventHandler(), Multipart_Loaders.getObjectLoaders());
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
    	this.getLogger().info(CrashReport.makeCrashReport(new Throwable(), "urmum").getCompleteReport());
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
