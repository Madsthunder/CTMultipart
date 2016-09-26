package continuum.multipart.proxy;

import com.google.common.base.Functions;

import continuum.api.microblock.Microblock;
import continuum.api.microblock.MicroblockOverlap;
import continuum.api.multipart.TESRMultiblockBase;
import continuum.api.multipart.TileEntityMultiblock;
import continuum.core.mod.CTCore_OH;
import continuum.essentials.client.state.StateMapperStatic;
import continuum.multipart.client.model.ModelMicroblock;
import continuum.multipart.client.model.ModelMultiblock;
import continuum.multipart.client.state.StateMapperMicroblock;
import continuum.multipart.enums.DefaultMicroblock;
import continuum.multipart.mod.Multipart_Callbacks;
import continuum.multipart.mod.Multipart_Mod;
import continuum.multipart.mod.Multipart_OH;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry;

public class ClientProxy extends CommonProxy
{
	ClientProxy()
	{
	}
	
	@Override
	public void pre(Multipart_Mod mod)
	{
		super.pre(mod);
		Multipart_OH holder = mod.getObjectHolder();
		holder.microblockSM = new StateMapperMicroblock();
		CTCore_OH.models.put(new ResourceLocation(holder.getModid(), "models/block/microblock"), Functions.constant(holder.microblockModel = new ModelMicroblock(mod)));
		CTCore_OH.models.put(new ResourceLocation(holder.getModid(), "models/block/multiblock"), Functions.constant(holder.multiblockModel = new ModelMultiblock()));
		IForgeRegistry<Microblock> microblockRegistry = GameRegistry.findRegistry(Microblock.class);
		if(microblockRegistry != null)
		{
			for(Microblock microblock : microblockRegistry)
			{
				Block block = microblock.getBlock();
				ModelLoader.setCustomStateMapper(block, holder.microblockSM);
				Item item = Item.getItemFromBlock(block);
				if(item != null)
					ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(new ResourceLocation(holder.getModid(), "microblock"), "normal"));
			}
			IForgeRegistry<MicroblockOverlap> microblockOverlapRegistry = microblockRegistry.getSlaveMap(Multipart_Callbacks.MICROBLOCK_OVERLAPS, IForgeRegistry.class);
			if(microblockOverlapRegistry != null)
				microblockOverlapRegistry.register(new MicroblockOverlap(DefaultMicroblock.STRIP, DefaultMicroblock.COVER));
		}
		if(holder.multiblock != null)
			ModelLoader.setCustomStateMapper(holder.multiblock, StateMapperStatic.create(new ModelResourceLocation(new ResourceLocation(holder.getModid(), "multiblock"), "normal")));
	}
	
	@Override
	public void init(Multipart_Mod mod)
	{
		super.init(mod);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMultiblock.class, new TESRMultiblockBase());
	}
}
