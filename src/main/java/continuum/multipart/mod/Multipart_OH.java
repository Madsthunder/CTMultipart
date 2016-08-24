package continuum.multipart.mod;

import java.util.HashMap;

import continuum.essentials.mod.ObjectHolder;
import continuum.multipart.blocks.BlockAxised;
import continuum.multipart.blocks.BlockCornered;
import continuum.multipart.blocks.BlockLayered;
import continuum.multipart.blocks.BlockMultiblock;
import continuum.multipart.client.models.ModelMicroblock;
import continuum.multipart.client.models.ModelMultipart;
import continuum.multipart.client.state.StateMapperMicroblock;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Multipart_OH implements ObjectHolder
{
	private static Multipart_OH holder;
	
	public static Multipart_OH getObjectHolder()
	{
		return holder == null ? new Multipart_OH() : holder;
	}
	
	private final Mod mod;
	private Multipart_Mod ctmultipart;
	
	private Multipart_OH()
	{
		this.mod = Multipart_Mod.class.getAnnotation(Mod.class);
	}
	
	void setCTMultipart(Multipart_Mod ctmultipart)
	{
		this.ctmultipart = ctmultipart;
	}
	
	public Multipart_Mod getCTMultipart()
	{
		return this.ctmultipart;
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
	
	public CreativeTabs microblocks;
	public BlockMultiblock multipart;
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
}
