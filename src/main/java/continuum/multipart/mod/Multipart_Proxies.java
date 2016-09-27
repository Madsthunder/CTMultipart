package continuum.multipart.mod;

import com.google.common.base.Functions;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

import continuum.api.microblock.Microblock;
import continuum.api.microblock.MicroblockOverlap;
import continuum.api.microblock.TESRMicroblockBase;
import continuum.api.microblock.TileEntityMicroblockBase;
import continuum.api.microblock.material.MicroblockMaterial;
import continuum.api.microblock.material.MicroblockMaterialCapability;
import continuum.api.multipart.TESRMultiblockBase;
import continuum.api.multipart.TileEntityMultiblockBase;
import continuum.core.mod.CTCore_OH;
import continuum.essentials.client.state.StateMapperStatic;
import continuum.essentials.mod.Proxy;
import continuum.essentials.util.CreativeTab;
import continuum.multipart.client.model.ModelMicroblock;
import continuum.multipart.client.model.ModelMultiblock;
import continuum.multipart.client.state.StateMapperMicroblock;
import continuum.multipart.enums.DefaultMicroblock;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;

public final class Multipart_Proxies
{
	public static final Proxy INSTANCE = FMLLaunchHandler.side() == Side.CLIENT ? new Client() : new Common();
	
	private static class Common extends Proxy<Multipart_Mod>
	{
		@Override
		public void pre(Multipart_Mod mod)
		{
			IForgeRegistry<Microblock> microblockRegistry = GameRegistry.findRegistry(Microblock.class);
			IForgeRegistry<MicroblockMaterial> microblockMaterialRegistry = GameRegistry.findRegistry(MicroblockMaterial.class);
			if(microblockRegistry != null && microblockMaterialRegistry != null)
			{
				ItemStack stack = new ItemStack(DefaultMicroblock.SLAB.getBlock(), 1);
				if(stack.hasCapability(MicroblockMaterialCapability.MICROBLOCKMATERIAL, null))
					stack.getCapability(MicroblockMaterialCapability.MICROBLOCKMATERIAL, null).setMaterial(Iterables.find(microblockMaterialRegistry, Predicates.not(Predicates.equalTo(MicroblockMaterial.defaultMaterial))));
				Multipart_OH.I.microblocks = new CreativeTab("microblocks", stack);
				for(Microblock microblock : microblockRegistry)
					microblock.getBlock().setCreativeTab(Multipart_OH.I.microblocks);
			}
		}
	}
	private static class Client extends Common
	{
		@Override
		public void pre(Multipart_Mod mod)
		{
			super.pre(mod);
			Multipart_OH.I.microblockSM = new StateMapperMicroblock();
			CTCore_OH.models.put(new ResourceLocation(Multipart_OH.I.getModid(), "models/block/microblock"), Functions.constant(Multipart_OH.I.microblockModel = new ModelMicroblock()));
			CTCore_OH.models.put(new ResourceLocation(Multipart_OH.I.getModid(), "models/block/multiblock"), Functions.constant(Multipart_OH.I.multiblockModel = new ModelMultiblock()));
			IForgeRegistry<Microblock> microblockRegistry = GameRegistry.findRegistry(Microblock.class);
			if(microblockRegistry != null)
			{
				for(Microblock microblock : microblockRegistry)
				{
					Block block = microblock.getBlock();
					ModelLoader.setCustomStateMapper(block, Multipart_OH.I.microblockSM);
					Item item = Item.getItemFromBlock(block);
					if(item != null)
						ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(new ResourceLocation(Multipart_OH.I.getModid(), "microblock"), "normal"));
				}
				IForgeRegistry<MicroblockOverlap> microblockOverlapRegistry = microblockRegistry.getSlaveMap(Multipart_Callbacks.MICROBLOCK_OVERLAPS, IForgeRegistry.class);
				if(microblockOverlapRegistry != null)
					microblockOverlapRegistry.register(new MicroblockOverlap(DefaultMicroblock.STRIP, DefaultMicroblock.COVER));
			}
			if(Multipart_OH.I.multiblock != null)
				ModelLoader.setCustomStateMapper(Multipart_OH.I.multiblock, StateMapperStatic.create(new ModelResourceLocation(new ResourceLocation(Multipart_OH.I.getModid(), "multiblock"), "normal")));
		}
		
		@Override
		public void init(Multipart_Mod mod)
		{
			super.init(mod);
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMultiblockBase.class, new TESRMultiblockBase());
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMicroblockBase.class, new TESRMicroblockBase());
		}
	}
}
