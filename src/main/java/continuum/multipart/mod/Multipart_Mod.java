package continuum.multipart.mod;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import continuum.api.multipart.MultipartAPI;
import continuum.essentials.mod.CTMod;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class Multipart_Mod extends DummyModContainer
{
	private CTMod<Multipart_OH, Multipart_EH> mod;
	
	public Multipart_Mod()
	{
		super(new ModMetadata());
		ModMetadata metadata = this.getMetadata();
		metadata.modId = "ctmultipart";
		metadata.name = "Continuum: Multipart";
		metadata.version = "0.1.0";
		System.out.println(MultipartAPI.apiActive());
	}
	
    @Override
    public boolean registerBus(EventBus bus, LoadController controller)
    {
    	bus.register(this);
    	return true;
    }
    
    @Subscribe
    public void construction(FMLConstructionEvent event)
    {
    	this.mod = new CTMod(Multipart_OH.getHolder(this), new Multipart_EH(), Multipart_Loaders.getLoaders());
    	this.mod.getEventHandler().setMod(this.mod);
    	this.mod.construction(event);
    }
    
    @Subscribe
    public void pre(FMLPreInitializationEvent event)
    {
    	this.mod.pre(event);
    }
    
    @Subscribe
    public void init(FMLInitializationEvent event)
    {
    	this.mod.init(event);
    }
    
    @Subscribe
    public void post(FMLPostInitializationEvent event)
    {
    	this.mod.post(event);
    }
}
