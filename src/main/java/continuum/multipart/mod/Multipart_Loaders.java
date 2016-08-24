package continuum.multipart.mod;

import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Lists;

import continuum.api.microblock.TileEntityMicroblock;
import continuum.api.microblock.texture.MicroblockTextureApi;
import continuum.api.microblock.texture.MicroblockTextureEntry;
import continuum.api.multipart.Multipart;
import continuum.api.multipart.MultipartApi;
import continuum.api.multipart.TileEntityMultiblock;
import continuum.core.mod.CTCore_OH;
import continuum.essentials.mod.CTMod;
import continuum.essentials.mod.ObjectLoader;
import continuum.essentials.util.CreativeTab;
import continuum.multipart.blocks.BlockAxised;
import continuum.multipart.blocks.BlockCornered;
import continuum.multipart.blocks.BlockLayered;
import continuum.multipart.blocks.BlockMultiblock;
import continuum.multipart.client.models.ModelMicroblock;
import continuum.multipart.client.models.ModelMultipart;
import continuum.multipart.client.state.StateMapperMicroblock;
import continuum.multipart.enums.EnumMicroblockType;
import continuum.multipart.items.ItemMicroblock;
import continuum.multipart.multiparts.MultipartFlowerPot;
import continuum.multipart.multiparts.MutipartTorch;
import continuum.multipart.renderer.MultipartTESR;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Multipart_Loaders
{
	private static final List<ObjectLoader<Multipart_OH, Multipart_EH>> loaders = Lists.newArrayList(BlockLoader.I, ItemLoader.I, RegistryLoader.I, UtilityLoader.I, ClientLoader.I);
	
	static final ObjectLoader<Multipart_OH, Multipart_EH>[] getObjectLoaders()
	{
		return loaders.toArray(new ObjectLoader[0]);
	}
	
	private static class BlockLoader implements ObjectLoader<Multipart_OH, Multipart_EH>
	{
		private static final BlockLoader I = new BlockLoader();
		
		@Override
		public void pre(CTMod<Multipart_OH, Multipart_EH> mod)
		{
			Multipart_OH holder = mod.getObjectHolder();
			if(MultipartApi.apiActive())
			{
				ForgeRegistries.BLOCKS.register((holder.multipart = new BlockMultiblock()).setUnlocalizedName("multipart").setRegistryName("multipart"));
				GameRegistry.registerTileEntity(TileEntityMultiblock.class, "ctmultipart");
			}
			ForgeRegistries.BLOCKS.register(holder.slab = new BlockLayered(holder, EnumMicroblockType.SLAB));
			ForgeRegistries.BLOCKS.register(holder.panel = new BlockLayered(holder, EnumMicroblockType.PANEL));
			ForgeRegistries.BLOCKS.register(holder.cover = new BlockLayered(holder, EnumMicroblockType.COVER));
			ForgeRegistries.BLOCKS.register(holder.pillar = new BlockAxised(holder, EnumMicroblockType.PILLAR));
			ForgeRegistries.BLOCKS.register(holder.post = new BlockAxised(holder, EnumMicroblockType.POST));
			// ForgeRegistries.BLOCKS.register(holder.strip = new
			// BlockAxised(holder, EnumMicroblockType.STRIP));
			ForgeRegistries.BLOCKS.register(holder.notch = new BlockCornered(holder, EnumMicroblockType.NOTCH));
			ForgeRegistries.BLOCKS.register(holder.corner = new BlockCornered(holder, EnumMicroblockType.CORNER));
			ForgeRegistries.BLOCKS.register(holder.nook = new BlockCornered(holder, EnumMicroblockType.NOOK));
			GameRegistry.registerTileEntity(TileEntityMicroblock.class, "ctmicroblock");
		}
		
		@Override
		public String getName()
		{
			return "Blocks";
		}
	}
	
	private static class ClientLoader implements ObjectLoader<Multipart_OH, Multipart_EH>
	{
		private static final ClientLoader I = new ClientLoader();
		
		@SideOnly(Side.CLIENT)
		@Override
		public void construction(CTMod<Multipart_OH, Multipart_EH> mod)
		{
			Multipart_OH holder = mod.getObjectHolder();
			holder.microblockLocations = new HashMap<String, ResourceLocation>();
			for(EnumMicroblockType e : EnumMicroblockType.values())
				holder.microblockLocations.put(e.name().toLowerCase(), new ResourceLocation(holder.getModid(), "block/" + e.name().toLowerCase()));
		}
		
		@SideOnly(Side.CLIENT)
		@Override
		public void pre(CTMod<Multipart_OH, Multipart_EH> mod)
		{
			Multipart_OH holder = mod.getObjectHolder();
			holder.microblockSM = new StateMapperMicroblock(holder);
			CTCore_OH.models.put(new ResourceLocation(holder.getModid(), "models/block/microblock"), holder.microblockModel = new ModelMicroblock(mod));
			CTCore_OH.models.put(new ResourceLocation(holder.getModid(), "models/block/multipart"), holder.multipartModel = new ModelMultipart());
			this.setupModels(mod, holder.slab, holder.microblockSM);
			this.setupModels(mod, holder.panel, holder.microblockSM);
			this.setupModels(mod, holder.cover, holder.microblockSM);
			this.setupModels(mod, holder.pillar, holder.microblockSM);
			this.setupModels(mod, holder.post, holder.microblockSM);
			this.setupModels(mod, holder.strip, holder.microblockSM);
			this.setupModels(mod, holder.notch, holder.microblockSM);
			this.setupModels(mod, holder.corner, holder.microblockSM);
			this.setupModels(mod, holder.nook, holder.microblockSM);
		}
		
		@SideOnly(Side.CLIENT)
		@Override
		public void init(CTMod<Multipart_OH, Multipart_EH> mod)
		{
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMultiblock.class, new MultipartTESR());
		}
		
		@Override
		public String getName()
		{
			return "Client";
		}
		
		@Override
		public Side getSide()
		{
			return Side.CLIENT;
		}
		
		@SideOnly(Side.CLIENT)
		public <M extends Block, S extends IStateMapper> void setupModels(CTMod<Multipart_OH, Multipart_EH> mod, M microblock, S mapper)
		{
			Multipart_OH holder = mod.getObjectHolder();
			if(microblock != null)
			{
				ModelLoader.setCustomStateMapper(microblock, mapper);
				Item item = Item.getItemFromBlock(microblock);
				if(item != null) ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(holder.getModid() + ":microblock", "normal"));
			}
		}
	}
	
	private static class ItemLoader implements ObjectLoader<Multipart_OH, Multipart_EH>
	{
		private static final ItemLoader I = new ItemLoader();
		
		@Override
		public void pre(CTMod<Multipart_OH, Multipart_EH> mod)
		{
			ForgeRegistries.ITEMS.register(new ItemMicroblock(mod, EnumMicroblockType.SLAB));
			ForgeRegistries.ITEMS.register(new ItemMicroblock(mod, EnumMicroblockType.PANEL));
			ForgeRegistries.ITEMS.register(new ItemMicroblock(mod, EnumMicroblockType.COVER));
			ForgeRegistries.ITEMS.register(new ItemMicroblock(mod, EnumMicroblockType.PILLAR));
			ForgeRegistries.ITEMS.register(new ItemMicroblock(mod, EnumMicroblockType.POST));
			// ForgeRegistries.ITEMS.register(new ItemMicroblock(mod,
			// EnumMicroblockType.STRIP));
			ForgeRegistries.ITEMS.register(new ItemMicroblock(mod, EnumMicroblockType.NOTCH));
			ForgeRegistries.ITEMS.register(new ItemMicroblock(mod, EnumMicroblockType.CORNER));
			ForgeRegistries.ITEMS.register(new ItemMicroblock(mod, EnumMicroblockType.NOOK));
		}
		
		@Override
		public String getName()
		{
			return "Items";
		}
	}
	
	private static class RegistryLoader implements ObjectLoader<Multipart_OH, Multipart_EH>
	{
		private static final RegistryLoader I = new RegistryLoader();
		
		@Override
		public void construction(CTMod<Multipart_OH, Multipart_EH> mod)
		{
			System.out.println("microblocks=" + MicroblockTextureApi.apiActive());
			if(MicroblockTextureApi.apiActive())
				MicroblockTextureApi.getMicroblockTextureRegistry().register(MicroblockTextureEntry.defaultTexture);
		}
		
		@Override
		public void pre(CTMod<Multipart_OH, Multipart_EH> mod)
		{
			Multipart_OH holder = mod.getObjectHolder();
			if(MultipartApi.apiActive())
			{
				FMLControlledNamespacedRegistry<Multipart> registry = MultipartApi.getMultipartRegistry();
				registry.register(new MultipartFlowerPot());
				registry.register(new MutipartTorch());
				registry.register(holder.slab.getMultipart());
				registry.register(holder.panel.getMultipart());
				registry.register(holder.cover.getMultipart());
				registry.register(holder.pillar.getMultipart());
				registry.register(holder.post.getMultipart());
				//registry.register(holder.strip.getMultipart());
				registry.register(holder.notch.getMultipart());
				registry.register(holder.corner.getMultipart());
				registry.register(holder.nook.getMultipart());
			}
			if(MicroblockTextureApi.apiActive())
			{
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("stone", Blocks.STONE, "blocks/stone"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("granite", Blocks.STONE, 1, "blocks/stone_granite"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("granite_smooth", Blocks.STONE, 2, "blocks/stone_granite_smooth"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("diorite", Blocks.STONE, 3, "blocks/stone_diorite"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("diorite_smooth", Blocks.STONE, 4, "blocks/stone_diorite_smooth"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("andesite", Blocks.STONE, 5, "blocks/stone_andesite"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("andeite_smooth", Blocks.STONE, 6, "blocks/stone_andesite_smooth"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("dirt", Blocks.DIRT, "blocks/dirt"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("coarse_dirt", Blocks.DIRT, 1, "blocks/coarse_dirt"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("podzol", Blocks.DIRT, 2, "blocks/dirt_podzol_side", "blocks/dirt", "blocks/dirt_podzol_top"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("cobblestone", Blocks.COBBLESTONE, "blocks/cobblestone"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("mossy_cobblestone", Blocks.MOSSY_COBBLESTONE, "blocks/cobblestone_mossy"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("oak_planks", Blocks.PLANKS, "blocks/planks_oak"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("spruce_planks", Blocks.PLANKS, 1, "blocks/planks_spruce"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("birch_planks", Blocks.PLANKS, 2, "blocks/planks_birch"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("jungle_planks", Blocks.PLANKS, 3, "blocks/planks_jungle"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("acacia_planks", Blocks.PLANKS, 4, "blocks/planks_acacia"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("dark_oak_planks", Blocks.PLANKS, 5, "blocks/planks_big_oak"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("sand", Blocks.SAND, "blocks/sand"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("red_sand", Blocks.SAND, 1, "blocks/red_sand"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("gravel", Blocks.GRAVEL, "blocks/gravel"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("coal_ore", Blocks.COAL_ORE, "blocks/coal_ore"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("iron_ore", Blocks.IRON_ORE, "blocks/iron_ore"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("lapis_ore", Blocks.LAPIS_ORE, "blocks/lapis_ore"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("gold_ore", Blocks.GOLD_ORE, "blocks/gold_ore"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("redstone_ore", Blocks.REDSTONE_ORE, "blocks/redstone_ore"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("emerald_ore", Blocks.EMERALD_ORE, "blocks/emerald_ore"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("diamond_ore", Blocks.DIAMOND_ORE, "blocks/diamond_ore"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("nether_quartz_ore", Blocks.QUARTZ_ORE, "blocks/quartz_ore"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("oak_log", Blocks.LOG, "blocks/log_oak", "blocks/log_oak_top", "blocks/log_oak_top"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("spruce_log", Blocks.LOG, 1, "blocks/log_spruce", "blocks/log_spruce_top", "blocks/log_spruce_top"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("birch_log", Blocks.LOG, 2, "blocks/log_birch", "blocks/log_birch_top", "blocks/log_birch_top"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("jungle_log", Blocks.LOG, 3, "blocks/log_jungle", "blocks/log_jungle_top", "blocks/log_jungle_top"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("acacia_log", Blocks.LOG2, "blocks/log_acacia", "blocks/log_acacia_top", "blocks/log_acacia_top"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("dark_oak_log", Blocks.LOG2, 1, "blocks/log_big_oak", "blocks/log_big_oak_top", "blocks/log_big_oak_top"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("sponge", Blocks.SPONGE, "blocks/sponge"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("wet_sponge", Blocks.SPONGE, 1, "blocks/sponge_wet"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("glass", Blocks.GLASS, "blocks/glass"));
				// Maybe Another Day
				/**mod.holder.microblockRegistry.register(new MicroblockEntry("white_glass", Blocks.stained_glass.getStateFromMeta(0), new ResourceLocation("minecraft", "blocks/glass_white")));
				mod.holder.microblockRegistry.register(new MicroblockEntry("orange_glass", Blocks.stained_glass.getStateFromMeta(1), new ResourceLocation("minecraft", "blocks/glass_orange")));
				mod.holder.microblockRegistry.register(new MicroblockEntry("magenta_glass", Blocks.stained_glass.getStateFromMeta(2), new ResourceLocation("minecraft", "blocks/glass_magenta")));
				mod.holder.microblockRegistry.register(new MicroblockEntry("light_blue_glass", Blocks.stained_glass.getStateFromMeta(3), new ResourceLocation("minecraft", "blocks/glass_light_blue")));
				mod.holder.microblockRegistry.register(new MicroblockEntry("yellow_glass", Blocks.stained_glass.getStateFromMeta(4), new ResourceLocation("minecraft", "blocks/glass_yellow")));
				mod.holder.microblockRegistry.register(new MicroblockEntry("lime_glass", Blocks.stained_glass.getStateFromMeta(5), new ResourceLocation("minecraft", "blocks/glass_lime")));
				mod.holder.microblockRegistry.register(new MicroblockEntry("pink_glass", Blocks.stained_glass.getStateFromMeta(6), new ResourceLocation("minecraft", "blocks/glass_pink")));
				mod.holder.microblockRegistry.register(new MicroblockEntry("gray_glass", Blocks.stained_glass.getStateFromMeta(7), new ResourceLocation("minecraft", "blocks/glass_gray")));
				mod.holder.microblockRegistry.register(new MicroblockEntry("silver_glass", Blocks.stained_glass.getStateFromMeta(8), new ResourceLocation("minecraft", "blocks/glass_silver")));
				mod.holder.microblockRegistry.register(new MicroblockEntry("cyan_glass", Blocks.stained_glass.getStateFromMeta(9), new ResourceLocation("minecraft", "blocks/glass_cyan")));
				mod.holder.microblockRegistry.register(new MicroblockEntry("purple_glass", Blocks.stained_glass.getStateFromMeta(10), new ResourceLocation("minecraft", "blocks/glass_purple")));
				mod.holder.microblockRegistry.register(new MicroblockEntry("blue_glass", Blocks.stained_glass.getStateFromMeta(11), new ResourceLocation("minecraft", "blocks/glass_blue")));
				mod.holder.microblockRegistry.register(new MicroblockEntry("brown_glass", Blocks.stained_glass.getStateFromMeta(12), new ResourceLocation("minecraft", "blocks/glass_brown")));
				mod.holder.microblockRegistry.register(new MicroblockEntry("green_glass", Blocks.stained_glass.getStateFromMeta(13), new ResourceLocation("minecraft", "blocks/glass_green")));
				mod.holder.microblockRegistry.register(new MicroblockEntry("red_glass", Blocks.stained_glass.getStateFromMeta(14), new ResourceLocation("minecraft", "blocks/glass_red")));
				mod.holder.microblockRegistry.register(new MicroblockEntry("black_glass", Blocks.stained_glass.getStateFromMeta(15), new ResourceLocation("minecraft", "blocks/glass_black")));*/
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("coal_block", Blocks.COAL_BLOCK, "blocks/coal_block"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("iron_block", Blocks.IRON_BLOCK, "blocks/iron_block"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("lapis_block", Blocks.LAPIS_BLOCK, "blocks/lapis_block"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("gold_block", Blocks.GOLD_BLOCK, "blocks/gold_block"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("redstone_block", Blocks.REDSTONE_BLOCK, "blocks/redstone_block"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("emerald_block", Blocks.EMERALD_BLOCK, "blocks/emerald_block"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("diamond_block", Blocks.DIAMOND_BLOCK, "blocks/diamond_block"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("sandstone", Blocks.SANDSTONE, "blocks/sandstone_normal", "blocks/sandstone_bottom", "blocks/sandstone_top"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("chiseled_sandstone", Blocks.SANDSTONE, 1, "blocks/sandstone_carved", "blocks/sandstone_bottom", "blocks/sandstone_bottom"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("smooth_sandstone", Blocks.SANDSTONE, 2, "blocks/sandstone_smooth", "blocks/sandstone_bottom", "blocks/sandstone_bottom"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("red_sandstone", Blocks.RED_SANDSTONE, "blocks/red_sandstone_normal", "blocks/red_sandstone_bottom", "blocks/red_sandstone_top"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("red_chiseled_sandstone", Blocks.RED_SANDSTONE, 1, "blocks/red_sandstone_carved", "blocks/red_sandstone_bottom", "blocks/red_sandstone_bottom"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("red_smooth_sandstone", Blocks.RED_SANDSTONE, 2, "blocks/red_sandstone_smooth", "blocks/red_sandstone_bottom", "blocks/red_sandstone_bottom"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("white_wool", Blocks.WOOL, "blocks/wool_colored_white"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("orange_wool", Blocks.WOOL, 1, "blocks/wool_colored_orange"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("magenta_wool", Blocks.WOOL, 2, "blocks/wool_colored_magenta"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("light_blue_wool", Blocks.WOOL, 3, "blocks/wool_colored_light_blue"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("yellow_wool", Blocks.WOOL, 4, "blocks/wool_colored_yellow"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("lime_wool", Blocks.WOOL, 5, "blocks/wool_colored_lime"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("pink_wool", Blocks.WOOL, 6, "blocks/wool_colored_pink"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("gray_wool", Blocks.WOOL, 7, "blocks/wool_colored_gray"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("light_gray_wool", Blocks.WOOL, 8, "blocks/wool_colored_silver"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("cyan_wool", Blocks.WOOL, 9, "blocks/wool_colored_cyan"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("purple_wool", Blocks.WOOL, 10, "blocks/wool_colored_purple"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("blue_wool", Blocks.WOOL, 11, "blocks/wool_colored_blue"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("brown_wool", Blocks.WOOL, 12, "blocks/wool_colored_brown"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("green_wool", Blocks.WOOL, 13, "blocks/wool_colored_green"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("red_wool", Blocks.WOOL, 14, "blocks/wool_colored_red"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("black_wool", Blocks.WOOL, 15, "blocks/wool_colored_black"));
				// mod.holder.microblockRegistry.register(new MicroblockEntry("ice",
				// Blocks.ice, "blocks/ice"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("packed_ice", Blocks.PACKED_ICE, "blocks/ice_packed"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("snow", Blocks.SNOW, "blocks/snow"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("clay", Blocks.CLAY, "blocks/clay"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("pumpkin", Blocks.PUMPKIN, "blocks/pumpkin_side", "blocks/pumpkin_top", "blocks/pumpkin_top", "blocks/pumpkin_face_off"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("lit_pumpkin", Blocks.LIT_PUMPKIN, "blocks/pumpkin_side", "blocks/pumpkin_top", "blocks/pumpkin_top", "blocks/pumpkin_face_on"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("netherrack", Blocks.NETHERRACK, "blocks/netherrack"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("soul_sand", Blocks.SOUL_SAND, "blocks/soul_sand"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("glowstone", Blocks.GLOWSTONE, "blocks/glowstone"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("stone_bricks", Blocks.STONEBRICK, "blocks/stonebrick"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("mossy_stone_bricks", Blocks.STONEBRICK, 1, "blocks/stonebrick_mossy"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("cracked_stone_bricks", Blocks.STONEBRICK, 2, "blocks/stonebrick_cracked"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("chiseled_stone_bricks", Blocks.STONEBRICK, 3, "blocks/stonebrick_carved"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("mycelium", Blocks.MYCELIUM, "blocks/mycelium_side", "blocks/dirt", "blocks/mycelium_top"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("nether_bricks", Blocks.NETHER_BRICK, "blocks/nether_brick"));
				MicroblockTextureApi.getMicroblockTextureRegistry().register(new MicroblockTextureEntry("end_stone", Blocks.END_STONE, "blocks/end_stone"));}
		}
		
		@Override
		public String getName()
		{
			return "Registries";
		}
	}
	
	private static class UtilityLoader implements ObjectLoader<Multipart_OH, Multipart_EH>
	{
		private static final UtilityLoader I = new UtilityLoader();
		
		@Override
		public void pre(CTMod<Multipart_OH, Multipart_EH> mod)
		{
			if(MicroblockTextureApi.apiActive())
			{
				Multipart_OH holder = mod.getObjectHolder();
				ItemStack stack = new ItemStack(holder.slab, 1);
				if(stack.hasTagCompound())
						stack.getTagCompound().merge(MicroblockTextureEntry.writeToNBT(MicroblockTextureApi.getMicroblockTextureRegistry().getObjectById(1)));
				else
					stack.setTagCompound(MicroblockTextureEntry.writeToNBT(MicroblockTextureApi.getMicroblockTextureRegistry().getObjectById(1)));
				holder.microblocks = new CreativeTab("ctmicroblocks", stack);
				holder.slab.setCreativeTab(holder.microblocks);
				holder.panel.setCreativeTab(holder.microblocks);
				holder.cover.setCreativeTab(holder.microblocks);
				holder.pillar.setCreativeTab(holder.microblocks);
				holder.post.setCreativeTab(holder.microblocks);
				// mod.holder.strip.setCreativeTab(mod.holder.microblocks);
				holder.notch.setCreativeTab(holder.microblocks);
				holder.corner.setCreativeTab(holder.microblocks);
				holder.nook.setCreativeTab(holder.microblocks);
			}
		}
		
		@Override
		public String getName()
		{
			return "Utilities";
		}
	}
}
