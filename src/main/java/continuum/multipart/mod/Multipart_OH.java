package continuum.multipart.mod;

import continuum.api.microblock.Microblock;
import continuum.api.microblock.texture.MicroblockMaterial;
import continuum.api.multipart.Multipart;
import continuum.essentials.mod.ObjectHolder;
import continuum.multipart.blocks.BlockMultiblock;
import continuum.multipart.client.model.ModelMicroblock;
import continuum.multipart.client.model.ModelMultiblock;
import continuum.multipart.client.state.StateMapperMicroblock;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Multipart_OH implements ObjectHolder
{
	public static final Multipart_OH INSTANCE = new Multipart_OH();
	
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
	public BlockMultiblock multiblock;
	@SideOnly(value = Side.CLIENT)
	public StateMapperMicroblock microblockSM;
	@SideOnly(value = Side.CLIENT)
	public ModelMicroblock microblockModel;
	public ModelMultiblock multiblockModel;
}
