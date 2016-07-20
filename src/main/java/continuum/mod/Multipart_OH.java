package continuum.multipart.mod;

import java.util.HashMap;

import continuum.core.mod.Core_EH;
import continuum.core.mod.Core_OH;
import continuum.essentials.mod.CTMod;
import continuum.essentials.mod.ObjectHolder;
import continuum.multipart.blocks.BlockAxised;
import continuum.multipart.blocks.BlockCornered;
import continuum.multipart.blocks.BlockLayered;
import continuum.multipart.blocks.BlockMultipart;
import continuum.multipart.client.models.ModelMicroblock;
import continuum.multipart.client.models.ModelMultipart;
import continuum.multipart.client.state.StateMapperMicroblock;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Multipart_OH implements ObjectHolder
{
	private static Multipart_OH holder;
	
	static Multipart_OH getHolder()
	{
		return holder == null ? new Multipart_OH(Multipart_Mod.class.getAnnotation(Mod.class)) : holder;
	}
	
	private Multipart_OH(Mod mod)
	{
		this.mod = mod;
	}
	
	@Override
	public String getModid()
	{
		return this.mod.modid();
	}
	
	@Override
	public String getName()
	{
		return this.mod.name();
	}
	
	@Override
	public String getVersion()
	{
		return this.mod.version();
	}
	
	public final Mod mod;
	public CreativeTabs microblocks;
	public BlockMultipart multipart;
	public BlockLayered slab;
	public BlockLayered panel;
	public BlockLayered cover;
	public BlockAxised pillar;
	public BlockAxised post;
	public BlockAxised strip;
	public BlockCornered notch;
	public BlockCornered corner;
	public BlockCornered nook;
	@SideOnly(value = Side.CLIENT)
	public HashMap<String, ResourceLocation> microblockLocations;
	@SideOnly(value = Side.CLIENT)
	public StateMapperMicroblock microblockSM;
	@SideOnly(value = Side.CLIENT)
	public ModelMicroblock microblockModel;
	public ModelMultipart multipartModel;
	public SoundEvent microblockSound;
}
