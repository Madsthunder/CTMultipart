package continuum.multipart.proxy;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

import continuum.api.microblock.Microblock;
import continuum.api.microblock.texture.MicroblockMaterial;
import continuum.essentials.mod.Proxy;
import continuum.essentials.util.CreativeTab;
import continuum.multipart.enums.DefaultMicroblock;
import continuum.multipart.mod.Multipart_Mod;
import continuum.multipart.mod.Multipart_OH;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy extends Proxy<Multipart_Mod>
{
	public static final Proxy INSTANCE = FMLLaunchHandler.side() == Side.CLIENT ? new ClientProxy() : new CommonProxy();
	
	CommonProxy()
	{
	}
	
	@Override
	public void pre(Multipart_Mod mod)
	{
		Multipart_OH holder = mod.getObjectHolder();
		IForgeRegistry<Microblock> microblockRegistry = GameRegistry.findRegistry(Microblock.class);
		IForgeRegistry<MicroblockMaterial> microblockMaterialRegistry = GameRegistry.findRegistry(MicroblockMaterial.class);
		if(microblockRegistry != null && microblockMaterialRegistry != null)
		{
			ItemStack stack = new ItemStack(DefaultMicroblock.SLAB.getBlock(), 1);
			MicroblockMaterial material = Iterables.find(microblockMaterialRegistry, Predicates.not(Predicates.equalTo(MicroblockMaterial.defaultMaterial)));
			NBTTagCompound compound = MicroblockMaterial.writeToNBT(material == null ? MicroblockMaterial.defaultMaterial : material);
			if(stack.hasTagCompound())
				stack.getTagCompound().merge(compound);
			else
				stack.setTagCompound(compound);
			holder.microblocks = new CreativeTab("ctmicroblocks", stack);
			for(Microblock microblock : microblockRegistry)
				microblock.getBlock().setCreativeTab(holder.microblocks);
		}
	}
	
	public void init(Multipart_Mod mod)
	{
	}
	
	public void post(Multipart_Mod mod)
	{
	}
}
