package continuum.multipart.mod;

import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import continuum.api.microblock.Microblock;
import continuum.api.microblock.MicroblockOverlap;
import continuum.api.microblock.TileEntityMicroblock;
import continuum.api.microblock.material.MicroblockMaterial;
import continuum.api.multipart.Multipart;
import continuum.api.multipart.MultipartEvent.AABBExceptionsEvent;
import continuum.api.multipart.MultipartState;
import continuum.api.multipart.MultipartStateList;
import continuum.api.multipart.TileEntityMultiblock;
import continuum.essentials.block.ICuboid;
import continuum.essentials.events.DebugInfoEvent;
import continuum.essentials.hooks.BlockHooks;
import continuum.multipart.blocks.BlockCornered;
import continuum.multipart.blocks.BlockLayered;
import continuum.multipart.blocks.BlockMultiblock;
import continuum.multipart.enums.CoverCuboid;
import continuum.multipart.enums.DefaultMicroblock;
import continuum.multipart.enums.ILayeredCuboid;
import continuum.multipart.enums.PanelCuboid;
import continuum.multipart.enums.SlabCuboid;
import continuum.multipart.items.ItemMicroblock;
import continuum.multipart.multiparts.MultipartFlowerPot;
import continuum.multipart.multiparts.MultipartMicroblock;
import continuum.multipart.multiparts.MutipartTorch;
import continuum.multipart.registry.MicroblockOverlapRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.fml.common.registry.RegistryBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber
public class Multipart_EH
{
	private static final Multipart_EH INSTANCE = new Multipart_EH();
	public static final ResourceLocation MULTIBLOCK = new ResourceLocation("ctmultipart", "multiblock");
	
	@SubscribeEvent
	public static void onRegistriesGet(RegistryEvent.NewRegistry event)
	{
		new RegistryBuilder().setName(new ResourceLocation("ctmultipart", "multiparts")).setType(Multipart.class).setIDRange(0, Integer.MAX_VALUE >> 5).addCallback(Multipart_Callbacks.MULTIPARTS).create();
		new RegistryBuilder().setName(new ResourceLocation("ctmultipart", "microblocks")).setType(Microblock.class).setIDRange(0, Integer.MAX_VALUE >> 5).addCallback(Multipart_Callbacks.MICROBLOCKS).create();
		new RegistryBuilder().setName(new ResourceLocation("ctmultipart", "microblockmaterials")).setType(MicroblockMaterial.class).setIDRange(0, Integer.MAX_VALUE >> 5).create().register(MicroblockMaterial.defaultMaterial);
	}
	
	@SubscribeEvent
	public static void onBlockObjectsRegister(RegistryEvent.Register<Block> event)
	{
		if(GameRegistry.findRegistry(Multipart.class) != null)
		{
			event.getRegistry().register((Multipart_OH.I.multiblock = new BlockMultiblock()).setUnlocalizedName(MULTIBLOCK.getResourcePath()).setRegistryName(MULTIBLOCK));
			GameRegistry.registerTileEntity(TileEntityMultiblock.class, MULTIBLOCK.toString());
			CapabilityManager.INSTANCE.register(MultipartStateList.class, new IStorage<MultipartStateList>()
			{
				@Override
				public NBTBase writeNBT(Capability capability, MultipartStateList instance, EnumFacing side)
				{
					return instance.serializeNBT();
				}
				
				@Override
				public void readNBT(Capability capability, MultipartStateList instance, EnumFacing side, NBTBase nbt)
				{
					instance.deserializeNBT((NBTTagList)nbt);
				}
			}, MultipartStateList.class);
		}
		if(GameRegistry.findRegistry(Microblock.class) != null)
			GameRegistry.registerTileEntity(TileEntityMicroblock.class, "ctmultipart:microblock");
	}
	
	@SubscribeEvent
	public static void onMultipartObjectsRegister(RegistryEvent.Register<Multipart> event)
	{
		event.getRegistry().register(new MultipartFlowerPot());
		event.getRegistry().register(new MutipartTorch());
	}
	
	@SubscribeEvent
	public static void onMicroblockObjectsRegister(RegistryEvent.Register<Microblock> event)
	{
		event.getRegistry().registerAll(Iterables.toArray(DefaultMicroblock.defaultMicroblocks, Microblock.class));
		MicroblockOverlapRegistry overlapRegistry = event.getRegistry().getSlaveMap(MicroblockOverlap.OVERLAPREGISTRY, MicroblockOverlapRegistry.class);
		if(overlapRegistry != null)
			MinecraftForge.EVENT_BUS.post(new RegistryEvent.Register(MicroblockOverlap.OVERLAPREGISTRY, overlapRegistry));
	}
	
	@SubscribeEvent
	public static void onMicroblockOverlapObjectsRegister(RegistryEvent.Register<MicroblockOverlap> event)
	{
		event.getRegistry().register(new MicroblockOverlap(DefaultMicroblock.COVER, DefaultMicroblock.STRIP, Predicates.alwaysTrue()));
	}
	
	@SubscribeEvent
	public static void onMicroblockMaterialObjectsRegister(RegistryEvent.Register<MicroblockMaterial> event)
	{
		event.getRegistry().register(new MicroblockMaterial.All("stone", Blocks.STONE, "blocks/stone"));
		event.getRegistry().register(new MicroblockMaterial.All("granite", Blocks.STONE, 1, "blocks/stone_granite"));
		event.getRegistry().register(new MicroblockMaterial.All("granite_smooth", Blocks.STONE, 2, "blocks/stone_granite_smooth"));
		event.getRegistry().register(new MicroblockMaterial.All("diorite", Blocks.STONE, 3, "blocks/stone_diorite"));
		event.getRegistry().register(new MicroblockMaterial.All("diorite_smooth", Blocks.STONE, 4, "blocks/stone_diorite_smooth"));
		event.getRegistry().register(new MicroblockMaterial.All("andesite", Blocks.STONE, 5, "blocks/stone_andesite"));
		event.getRegistry().register(new MicroblockMaterial.All("andeite_smooth", Blocks.STONE, 6, "blocks/stone_andesite_smooth"));
		event.getRegistry().register(new MicroblockMaterial.All("dirt", Blocks.DIRT, "blocks/dirt"));
		event.getRegistry().register(new MicroblockMaterial.All("coarse_dirt", Blocks.DIRT, 1, "blocks/coarse_dirt"));
		event.getRegistry().register(new MicroblockMaterial.TopBottom("podzol", Blocks.DIRT, 2, "blocks/dirt_podzol_top", "blocks/dirt", "blocks/dirt_podzol_side"));
		event.getRegistry().register(new MicroblockMaterial.All("cobblestone", Blocks.COBBLESTONE, "blocks/cobblestone"));
		event.getRegistry().register(new MicroblockMaterial.All("mossy_cobblestone", Blocks.MOSSY_COBBLESTONE, "blocks/cobblestone_mossy"));
		event.getRegistry().register(new MicroblockMaterial.All("oak_planks", Blocks.PLANKS, "blocks/planks_oak"));
		event.getRegistry().register(new MicroblockMaterial.All("spruce_planks", Blocks.PLANKS, 1, "blocks/planks_spruce"));
		event.getRegistry().register(new MicroblockMaterial.All("birch_planks", Blocks.PLANKS, 2, "blocks/planks_birch"));
		event.getRegistry().register(new MicroblockMaterial.All("jungle_planks", Blocks.PLANKS, 3, "blocks/planks_jungle"));
		event.getRegistry().register(new MicroblockMaterial.All("acacia_planks", Blocks.PLANKS, 4, "blocks/planks_acacia"));
		event.getRegistry().register(new MicroblockMaterial.All("dark_oak_planks", Blocks.PLANKS, 5, "blocks/planks_big_oak"));
		event.getRegistry().register(new MicroblockMaterial.All("sand", Blocks.SAND, "blocks/sand"));
		event.getRegistry().register(new MicroblockMaterial.All("red_sand", Blocks.SAND, 1, "blocks/red_sand"));
		event.getRegistry().register(new MicroblockMaterial.All("gravel", Blocks.GRAVEL, "blocks/gravel"));
		event.getRegistry().register(new MicroblockMaterial.All("coal_ore", Blocks.COAL_ORE, "blocks/coal_ore"));
		event.getRegistry().register(new MicroblockMaterial.All("iron_ore", Blocks.IRON_ORE, "blocks/iron_ore"));
		event.getRegistry().register(new MicroblockMaterial.All("lapis_ore", Blocks.LAPIS_ORE, "blocks/lapis_ore"));
		event.getRegistry().register(new MicroblockMaterial.All("gold_ore", Blocks.GOLD_ORE, "blocks/gold_ore"));
		event.getRegistry().register(new MicroblockMaterial.All("redstone_ore", Blocks.REDSTONE_ORE, "blocks/redstone_ore"));
		event.getRegistry().register(new MicroblockMaterial.All("emerald_ore", Blocks.EMERALD_ORE, "blocks/emerald_ore"));
		event.getRegistry().register(new MicroblockMaterial.All("diamond_ore", Blocks.DIAMOND_ORE, "blocks/diamond_ore"));
		event.getRegistry().register(new MicroblockMaterial.All("nether_quartz_ore", Blocks.QUARTZ_ORE, "blocks/quartz_ore"));
		event.getRegistry().register(new MicroblockMaterial.Pillar("oak_log", Blocks.LOG, "blocks/log_oak_top", "blocks/log_oak"));
		event.getRegistry().register(new MicroblockMaterial.Pillar("spruce_log", Blocks.LOG, 1, "blocks/log_spruce_top", "blocks/log_spruce"));
		event.getRegistry().register(new MicroblockMaterial.Pillar("birch_log", Blocks.LOG, 2, "blocks/log_birch_top", "blocks/log_birch"));
		event.getRegistry().register(new MicroblockMaterial.Pillar("jungle_log", Blocks.LOG, 3, "blocks/log_jungle_top", "blocks/log_jungle"));
		event.getRegistry().register(new MicroblockMaterial.Pillar("acacia_log", Blocks.LOG2, "blocks/log_acacia_top", "blocks/log_acacia"));
		event.getRegistry().register(new MicroblockMaterial.Pillar("dark_oak_log", Blocks.LOG2, 1, "blocks/log_big_oak_top", "blocks/log_big_oak"));
		event.getRegistry().register(new MicroblockMaterial.All("sponge", Blocks.SPONGE, "blocks/sponge"));
		event.getRegistry().register(new MicroblockMaterial.All("wet_sponge", Blocks.SPONGE, 1, "blocks/sponge_wet"));
		event.getRegistry().register(new MicroblockMaterial.All("glass", Blocks.GLASS, "blocks/glass"));
		event.getRegistry().register(new MicroblockMaterial.All("white_glass", Blocks.STAINED_GLASS, 0, "blocks/glass_white"));
		event.getRegistry().register(new MicroblockMaterial.All("orange_glass", Blocks.STAINED_GLASS, 1, "blocks/glass_orange"));
		event.getRegistry().register(new MicroblockMaterial.All("magenta_glass", Blocks.STAINED_GLASS, 2, "blocks/glass_magenta"));
		event.getRegistry().register(new MicroblockMaterial.All("light_blue_glass", Blocks.STAINED_GLASS, 3, "blocks/glass_light_blue"));
		event.getRegistry().register(new MicroblockMaterial.All("yellow_glass", Blocks.STAINED_GLASS, 4, "blocks/glass_yellow"));
		event.getRegistry().register(new MicroblockMaterial.All("lime_glass", Blocks.STAINED_GLASS, 5, "blocks/glass_lime"));
		event.getRegistry().register(new MicroblockMaterial.All("pink_glass", Blocks.STAINED_GLASS, 6, "blocks/glass_pink"));
		event.getRegistry().register(new MicroblockMaterial.All("gray_glass", Blocks.STAINED_GLASS, 7, "blocks/glass_gray"));
		event.getRegistry().register(new MicroblockMaterial.All("silver_glass", Blocks.STAINED_GLASS, 8, "blocks/glass_silver"));
		event.getRegistry().register(new MicroblockMaterial.All("cyan_glass", Blocks.STAINED_GLASS, 9, "blocks/glass_cyan"));
		event.getRegistry().register(new MicroblockMaterial.All("purple_glass", Blocks.STAINED_GLASS, 10, "blocks/glass_purple"));
		event.getRegistry().register(new MicroblockMaterial.All("blue_glass", Blocks.STAINED_GLASS, 11, "blocks/glass_blue"));
		event.getRegistry().register(new MicroblockMaterial.All("brown_glass", Blocks.STAINED_GLASS, 12, "blocks/glass_brown"));
		event.getRegistry().register(new MicroblockMaterial.All("green_glass", Blocks.STAINED_GLASS, 13, "blocks/glass_green"));
		event.getRegistry().register(new MicroblockMaterial.All("red_glass", Blocks.STAINED_GLASS, 14, "blocks/glass_red"));
		event.getRegistry().register(new MicroblockMaterial.All("black_glass", Blocks.STAINED_GLASS, 15, "blocks/glass_black"));
		event.getRegistry().register(new MicroblockMaterial.All("coal_block", Blocks.COAL_BLOCK, "blocks/coal_block"));
		event.getRegistry().register(new MicroblockMaterial.All("iron_block", Blocks.IRON_BLOCK, "blocks/iron_block"));
		event.getRegistry().register(new MicroblockMaterial.All("lapis_block", Blocks.LAPIS_BLOCK, "blocks/lapis_block"));
		event.getRegistry().register(new MicroblockMaterial.All("gold_block", Blocks.GOLD_BLOCK, "blocks/gold_block"));
		event.getRegistry().register(new MicroblockMaterial.All("redstone_block", Blocks.REDSTONE_BLOCK, "blocks/redstone_block"));
		event.getRegistry().register(new MicroblockMaterial.All("emerald_block", Blocks.EMERALD_BLOCK, "blocks/emerald_block"));
		event.getRegistry().register(new MicroblockMaterial.All("diamond_block", Blocks.DIAMOND_BLOCK, "blocks/diamond_block"));
		event.getRegistry().register(new MicroblockMaterial.TopBottom("sandstone", Blocks.SANDSTONE, "blocks/sandstone_top", "blocks/sandstone_bottom", "blocks/sandstone_normal"));
		event.getRegistry().register(new MicroblockMaterial.TopBottom("chiseled_sandstone", Blocks.SANDSTONE, 1, "blocks/sandstone_top", "blocks/sandstone_bottom", "blocks/sandstone_carved"));
		event.getRegistry().register(new MicroblockMaterial.TopBottom("smooth_sandstone", Blocks.SANDSTONE, 2, "blocks/sandstone_top", "blocks/sandstone_bottom", "blocks/sandstone_smooth"));
		event.getRegistry().register(new MicroblockMaterial.TopBottom("red_sandstone", Blocks.RED_SANDSTONE, "blocks/red_sandstone_top", "blocks/red_sandstone_bottom", "blocks/red_sandstone_normal"));
		event.getRegistry().register(new MicroblockMaterial.TopBottom("red_chiseled_sandstone", Blocks.RED_SANDSTONE, 1, "blocks/red_sandstone_top", "blocks/red_sandstone_bottom", "blocks/red_sandstone_carved"));
		event.getRegistry().register(new MicroblockMaterial.TopBottom("red_smooth_sandstone", Blocks.RED_SANDSTONE, 2, "blocks/red_sandstone_top", "blocks/red_sandstone_bottom", "blocks/red_sandstone_smooth"));
		event.getRegistry().register(new MicroblockMaterial.All("white_wool", Blocks.WOOL, "blocks/wool_colored_white"));
		event.getRegistry().register(new MicroblockMaterial.All("orange_wool", Blocks.WOOL, 1, "blocks/wool_colored_orange"));
		event.getRegistry().register(new MicroblockMaterial.All("magenta_wool", Blocks.WOOL, 2, "blocks/wool_colored_magenta"));
		event.getRegistry().register(new MicroblockMaterial.All("light_blue_wool", Blocks.WOOL, 3, "blocks/wool_colored_light_blue"));
		event.getRegistry().register(new MicroblockMaterial.All("yellow_wool", Blocks.WOOL, 4, "blocks/wool_colored_yellow"));
		event.getRegistry().register(new MicroblockMaterial.All("lime_wool", Blocks.WOOL, 5, "blocks/wool_colored_lime"));
		event.getRegistry().register(new MicroblockMaterial.All("pink_wool", Blocks.WOOL, 6, "blocks/wool_colored_pink"));
		event.getRegistry().register(new MicroblockMaterial.All("gray_wool", Blocks.WOOL, 7, "blocks/wool_colored_gray"));
		event.getRegistry().register(new MicroblockMaterial.All("light_gray_wool", Blocks.WOOL, 8, "blocks/wool_colored_silver"));
		event.getRegistry().register(new MicroblockMaterial.All("cyan_wool", Blocks.WOOL, 9, "blocks/wool_colored_cyan"));
		event.getRegistry().register(new MicroblockMaterial.All("purple_wool", Blocks.WOOL, 10, "blocks/wool_colored_purple"));
		event.getRegistry().register(new MicroblockMaterial.All("blue_wool", Blocks.WOOL, 11, "blocks/wool_colored_blue"));
		event.getRegistry().register(new MicroblockMaterial.All("brown_wool", Blocks.WOOL, 12, "blocks/wool_colored_brown"));
		event.getRegistry().register(new MicroblockMaterial.All("green_wool", Blocks.WOOL, 13, "blocks/wool_colored_green"));
		event.getRegistry().register(new MicroblockMaterial.All("red_wool", Blocks.WOOL, 14, "blocks/wool_colored_red"));
		event.getRegistry().register(new MicroblockMaterial.All("black_wool", Blocks.WOOL, 15, "blocks/wool_colored_black"));
		event.getRegistry().register(new MicroblockMaterial.All("ice", Blocks.ICE, "blocks/ice"));
		event.getRegistry().register(new MicroblockMaterial.All("packed_ice", Blocks.PACKED_ICE, "blocks/ice_packed"));
		event.getRegistry().register(new MicroblockMaterial.All("snow", Blocks.SNOW, "blocks/snow"));
		event.getRegistry().register(new MicroblockMaterial.All("clay", Blocks.CLAY, "blocks/clay"));
		event.getRegistry().register(new MicroblockMaterial.Orientable("pumpkin", Blocks.PUMPKIN, "blocks/pumpkin_top", "blocks/pumpkin_face_off", "blocks/pumpkin_side"));
		event.getRegistry().register(new MicroblockMaterial.Orientable("lit_pumpkin", Blocks.LIT_PUMPKIN, "blocks/pumpkin_top", "blocks/pumpkin_face_on", "blocks/pumpkin_side"));
		event.getRegistry().register(new MicroblockMaterial.All("netherrack", Blocks.NETHERRACK, "blocks/netherrack"));
		event.getRegistry().register(new MicroblockMaterial.All("soul_sand", Blocks.SOUL_SAND, "blocks/soul_sand"));
		event.getRegistry().register(new MicroblockMaterial.All("glowstone", Blocks.GLOWSTONE, "blocks/glowstone"));
		event.getRegistry().register(new MicroblockMaterial.All("stone_bricks", Blocks.STONEBRICK, "blocks/stonebrick"));
		event.getRegistry().register(new MicroblockMaterial.All("mossy_stone_bricks", Blocks.STONEBRICK, 1, "blocks/stonebrick_mossy"));
		event.getRegistry().register(new MicroblockMaterial.All("cracked_stone_bricks", Blocks.STONEBRICK, 2, "blocks/stonebrick_cracked"));
		event.getRegistry().register(new MicroblockMaterial.All("chiseled_stone_bricks", Blocks.STONEBRICK, 3, "blocks/stonebrick_carved"));
		event.getRegistry().register(new MicroblockMaterial.TopBottom("mycelium", Blocks.MYCELIUM, "blocks/mycelium_top", "blocks/dirt", "blocks/mycelium_side"));
		event.getRegistry().register(new MicroblockMaterial.All("nether_bricks", Blocks.NETHER_BRICK, "blocks/nether_brick"));
		event.getRegistry().register(new MicroblockMaterial.All("end_stone", Blocks.END_STONE, "blocks/end_stone"));
	}
	
	@SubscribeEvent
	public static void onCapabilitiesGet(AttachCapabilitiesEvent<TileEntity> event)
	{
		if(event.getObject() instanceof TileEntityMultiblock)
			event.addCapability(new ResourceLocation("ctmultipart", "multipartList"), new MultipartStateList(event.getObject()));
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static <T extends Comparable<T>> void onDebugInfoGet(DebugInfoEvent event)
	{
		if(Multipart_OH.I.multiblock != null)
		{
			RayTraceResult result = Minecraft.getMinecraft().objectMouseOver;
			if(event.getInfoSide() == DebugInfoEvent.EnumSide.RIGHT && result != null && result.typeOfHit == Type.BLOCK && result.getBlockPos() != null && result.hitInfo instanceof MultipartState)
			{
				List<String> list = event.getDebugInfo();
				if(list.remove(Multipart_OH.I.multiblock.getRegistryName().toString()))
				{
					MultipartState info = (MultipartState)result.hitInfo;
					World world = Minecraft.getMinecraft().theWorld;
					BlockPos pos = result.getBlockPos();
					IBlockState state = world.getWorldType() == WorldType.DEBUG_WORLD ? info.getState() : info.getActualState();
					list.add("(Multipart) " + info.getMultipart().getRegistryName());
					for(Entry<IProperty<?>, Comparable<?>> entry : state.getProperties().entrySet())
					{
						IProperty property = entry.getKey();
						Comparable value = entry.getValue();
						String s = property.getName(value);
						if(Boolean.TRUE.equals(value))
							s = TextFormatting.GREEN + s;
						else if(Boolean.FALSE.equals(value))
							s = TextFormatting.RED + s;
						list.add(property.getName() + ": " + s);
					}
				}
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onItemRightClick(RightClickItem event)
	{
		IForgeRegistry<Multipart> multipartRegistry = GameRegistry.findRegistry(Multipart.class);
		if(multipartRegistry != null)
		{
			ItemStack stack = event.getItemStack();
			EntityPlayer player = event.getEntityPlayer();
			RayTraceResult result = player.rayTrace(player.capabilities.isCreativeMode ? 5D : 4.5D, 1F);
			World world = event.getWorld();
			BlockPos pos = result.getBlockPos().offset(result.sideHit);
			Block possibleBlock = Block.getBlockFromItem(stack.getItem());
			if(possibleBlock == null)
				possibleBlock = ForgeRegistries.BLOCKS.getValue(stack.getItem().getRegistryName());
			Multipart prevEntry;
			Multipart possibleEntry;
			if(multipartRegistry != null && possibleBlock != null && (possibleEntry = multipartRegistry.getValue(stack.getItem().getRegistryName())) != null)
			{
				Vec3d hitPos = result.hitVec.subtract(pos.getX(), pos.getY(), pos.getZ());
				IBlockState prevBlock = world.getBlockState(pos);
				TileEntity prevTile = world.getTileEntity(pos);
				IBlockState possibleState = possibleBlock.onBlockPlaced(world, pos, result.sideHit, (float)hitPos.xCoord, (float)hitPos.yCoord, (float)hitPos.zCoord, stack.getMetadata(), player);
				if(prevTile != null && prevTile.hasCapability(MultipartStateList.MULTIPARTINFOLIST, null))
				{
					if(attemptToPlace(world, pos, player, stack, Lists.newArrayList(), result, possibleEntry, possibleState))
						player.swingArm(EnumHand.MAIN_HAND);
				}
				else if((prevEntry = multipartRegistry.getValue(prevBlock.getBlock().getRegistryName())) != null)
				{
					List<BlockSnapshot> snapshots = BlockHooks.setBlockStateWithSnapshots(world, pos, Multipart_OH.I.multiblock.getDefaultState());
					TileEntity entity = world.getTileEntity(pos);
					if(entity.hasCapability(MultipartStateList.MULTIPARTINFOLIST, null))
					{
						MultipartStateList infoList = entity.getCapability(MultipartStateList.MULTIPARTINFOLIST, null);
						infoList.add(new MultipartState(UUID.randomUUID(), infoList, prevEntry, prevBlock, prevTile));
						if(attemptToPlace(world, pos, player, stack, snapshots, result, possibleEntry, possibleState))
							player.swingArm(EnumHand.MAIN_HAND);
					}
				}
			}
		}
	}
	
	public static boolean attemptToPlace(World world, BlockPos pos, EntityPlayer player, ItemStack stack, List<BlockSnapshot> snapshots, RayTraceResult result, Multipart multipart, IBlockState state)
	{
		TileEntity entity = world.getTileEntity(pos);
		if(entity.hasCapability(MultipartStateList.MULTIPARTINFOLIST, null))
		{
			MultipartStateList infoList = entity.getCapability(MultipartStateList.MULTIPARTINFOLIST, null);
			if(multipart.canPlaceIn(world, pos, state, infoList, result))
			{
				TileEntity entity1 = state.getBlock().createTileEntity(world, state);
				setTileNBT(world, player, pos, stack, entity, entity1);
				MultipartState info = new MultipartState(UUID.randomUUID(), infoList, multipart, state, entity1);
				infoList.add(info);
				info.onPlaced(player, stack);
				entity.markDirty();
				// if(player instanceof EntityPlayerMP)
				// ((EntityPlayerMP)player).connection.sendPacket(entity.getUpdatePacket());
				SoundType sound = info.getSoundType();
				world.playSound(player, pos, sound.getPlaceSound(), SoundCategory.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
				world.checkLight(pos);
				world.markBlockRangeForRenderUpdate(pos.add(-8, -8, -8), pos.add(8, 8, 8));
				if(!player.capabilities.isCreativeMode)
					--stack.stackSize;
				return true;
			}
			else
				BlockHooks.restoreBlockSnapshots(world, snapshots);
		}
		else
			BlockHooks.restoreBlockSnapshots(world, snapshots);
		return false;
	}
	
	public static boolean setTileNBT(World world, EntityPlayer player, BlockPos pos, ItemStack stack, TileEntity multiblock, TileEntity entity)
	{
		MinecraftServer minecraftserver = world.getMinecraftServer();
		if(minecraftserver == null)
			return false;
		else
		{
			if(stack.hasTagCompound() && stack.getTagCompound().hasKey("BlockEntityTag", 10))
				if(entity != null)
				{
					if(!world.isRemote && entity.onlyOpsCanSetNbt() && (pos == null || !minecraftserver.getPlayerList().canSendCommands(player.getGameProfile())))
						return false;
					NBTTagCompound compound = new NBTTagCompound();
					NBTTagCompound compound1 = (NBTTagCompound)compound.copy();
					entity.writeToNBT(compound);
					NBTTagCompound compound2 = (NBTTagCompound)stack.getTagCompound().getTag("BlockEntityTag");
					compound.merge(compound2);
					compound.setInteger("x", pos.getX());
					compound.setInteger("y", pos.getY());
					compound.setInteger("z", pos.getZ());
					if(!compound.equals(compound1))
					{
						entity.readFromNBT(compound);
						multiblock.markDirty();
						return true;
					}
				}
			return false;
		}
	}
	
	@SubscribeEvent
	public static void onExceptionsGet(AABBExceptionsEvent event)
	{
		IForgeRegistry<Microblock> microblockRegistry = GameRegistry.findRegistry(Microblock.class);
		if(microblockRegistry != null && event.getMultipart() instanceof MultipartMicroblock)
		{
			AxisAlignedBB box = event.getBox();
			MultipartMicroblock multipart = (MultipartMicroblock)event.getMultipart();
			for(Microblock microblock : microblockRegistry)
				for(ICuboid cube : microblock.getCuboids())
					if(cube.getSelectableCuboid() == box)
					{
						multipart.getMicroblock().addExceptionsToList(cube, event.allowed);
						return;
					}
		}
	}
	
	public static void addAllCuboids(EnumFacing exclude, ILayeredCuboid[] cuboids, List<AxisAlignedBB> list)
	{
		for(ILayeredCuboid cuboid : cuboids)
			if(cuboid.getSide() != exclude)
				list.add(cuboid.getSelectableCuboid());
	}
	
	public static EnumFacing tryFindFacing(AxisAlignedBB box)
	{
		for(SlabCuboid cuboid : SlabCuboid.values())
			if(cuboid.getSelectableCuboid() == box)
				return cuboid.getSide();
		for(PanelCuboid cuboid : PanelCuboid.values())
			if(cuboid.getSelectableCuboid() == box)
				return cuboid.getSide();
		for(CoverCuboid cuboid : CoverCuboid.values())
			if(cuboid.getSelectableCuboid() == box)
				return cuboid.getSide();
		return null;
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onBlockHighlight(DrawBlockHighlightEvent event)
	{
		EntityPlayer player = event.getPlayer();
		ItemStack stack = player.getHeldItemMainhand();
		RayTraceResult result = event.getTarget();
		if(result != null && result.getBlockPos() != null && result.typeOfHit == Type.BLOCK && stack != null && stack.getItem() instanceof ItemMicroblock)
		{
			World world = event.getPlayer().getEntityWorld();
			BlockPos pos = result.getBlockPos();
			IBlockState state = world.getBlockState(pos);
			AxisAlignedBB aabb = state.getSelectedBoundingBox(world, pos);
			if(aabb != null)
			{
				EnumFacing side = result.sideHit;
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
				GlStateManager.color(0.0F, 0.0F, 0.0F, 0.4F);
				GlStateManager.glLineWidth(2.0F);
				GlStateManager.disableTexture2D();
				GlStateManager.depthMask(false);
				Double xOffset = -(player.lastTickPosX + (player.posX - player.lastTickPosX) * event.getPartialTicks());
				Double yOffset = -(player.lastTickPosY + (player.posY - player.lastTickPosY) * event.getPartialTicks());
				Double zOffset = -(player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.getPartialTicks());
				Tessellator tess = Tessellator.getInstance();
				VertexBuffer vb = tess.getBuffer();
				Axis axis = side.getAxis();
				Block block = ((ItemMicroblock)stack.getItem()).block;
				if(block instanceof BlockLayered)
				{
					if(axis == Axis.X)
					{
						AxisAlignedBB box = new AxisAlignedBB(aabb.minX, pos.getY(), pos.getZ(), aabb.maxX, pos.getY() + 1, pos.getZ() + 1).offset(xOffset, yOffset, zOffset);
						Double minCY = box.minY + 0.3125;
						Double minCZ = box.minZ + 0.3125;
						Double maxCY = box.maxY - 0.3125;
						Double maxCZ = box.maxZ - 0.3125;
						box = box.expandXyz(0.0020000000949949026D);
						Boolean w = side == EnumFacing.WEST;
						Double x = w ? box.minX : box.maxX;
						// Down
						vb.begin(3, DefaultVertexFormats.POSITION);
						vb.pos(x, box.minY, box.minZ).endVertex();
						vb.pos(x, minCY, minCZ).endVertex();
						vb.pos(x, minCY, maxCZ).endVertex();
						vb.pos(x, box.minY, box.maxZ).endVertex();
						vb.pos(x, box.minY, box.minZ).endVertex();
						tess.draw();
						// Up
						vb.begin(3, DefaultVertexFormats.POSITION);
						vb.pos(x, box.maxY, box.minZ).endVertex();
						vb.pos(x, maxCY, minCZ).endVertex();
						vb.pos(x, maxCY, maxCZ).endVertex();
						vb.pos(x, box.maxY, box.maxZ).endVertex();
						vb.pos(x, box.maxY, box.minZ).endVertex();
						tess.draw();
						// North
						vb.begin(3, DefaultVertexFormats.POSITION);
						vb.pos(x, box.minY, box.minZ).endVertex();
						vb.pos(x, minCY, minCZ).endVertex();
						vb.pos(x, maxCY, minCZ).endVertex();
						vb.pos(x, box.maxY, box.minZ).endVertex();
						vb.pos(x, box.minY, box.minZ).endVertex();
						tess.draw();
						// South
						vb.begin(3, DefaultVertexFormats.POSITION);
						vb.pos(x, box.maxY, box.maxZ).endVertex();
						vb.pos(x, maxCY, maxCZ).endVertex();
						vb.pos(x, minCY, maxCZ).endVertex();
						vb.pos(x, box.minY, box.maxZ).endVertex();
						vb.pos(x, box.maxY, box.maxZ).endVertex();
						tess.draw();
						// Layer That's Not Needed But Is Here Anyway
						vb.begin(3, DefaultVertexFormats.POSITION);
						vb.pos(x, minCY, minCZ);
						vb.pos(x, maxCY, minCZ);
						vb.pos(x, maxCY, maxCZ);
						vb.pos(x, minCY, maxCZ);
						vb.pos(x, minCY, minCZ);
						tess.draw();
					}
					if(axis == Axis.Y)
					{
						AxisAlignedBB box = new AxisAlignedBB(pos.getX(), aabb.minY, pos.getZ(), pos.getX() + 1, aabb.maxY, pos.getZ() + 1).offset(xOffset, yOffset, zOffset);
						Double minCX = box.minX + 0.3125;
						Double minCZ = box.minZ + 0.3125;
						Double maxCX = box.maxX - 0.3125;
						Double maxCZ = box.maxZ - 0.3125;
						box = box.expandXyz(0.0020000000949949026D);
						Boolean d = side == EnumFacing.DOWN;
						Double y = d ? box.minY : box.maxY;
						// North
						vb.begin(3, DefaultVertexFormats.POSITION);
						vb.pos(box.minX, y, box.minZ).endVertex();
						vb.pos(minCX, y, minCZ).endVertex();
						vb.pos(maxCX, y, minCZ).endVertex();
						vb.pos(box.maxX, y, box.minZ).endVertex();
						vb.pos(box.minX, y, box.minZ).endVertex();
						tess.draw();
						// South
						vb.begin(3, DefaultVertexFormats.POSITION);
						vb.pos(box.maxX, y, box.maxZ).endVertex();
						vb.pos(maxCX, y, maxCZ).endVertex();
						vb.pos(minCX, y, maxCZ).endVertex();
						vb.pos(box.minX, y, box.maxZ).endVertex();
						vb.pos(box.maxX, y, box.maxZ).endVertex();
						tess.draw();
						// West
						vb.begin(3, DefaultVertexFormats.POSITION);
						vb.pos(box.minX, y, box.minZ).endVertex();
						vb.pos(minCX, y, minCZ).endVertex();
						vb.pos(minCX, y, maxCZ).endVertex();
						vb.pos(box.minX, y, box.maxZ).endVertex();
						vb.pos(box.minX, y, box.minZ).endVertex();
						tess.draw();
						// East
						vb.begin(3, DefaultVertexFormats.POSITION);
						vb.pos(box.maxX, y, box.minZ).endVertex();
						vb.pos(maxCX, y, minCZ).endVertex();
						vb.pos(maxCX, y, maxCZ).endVertex();
						vb.pos(box.maxX, y, box.maxZ).endVertex();
						vb.pos(box.maxX, y, box.minZ).endVertex();
						tess.draw();
						// Layer That's Not Needed But Is Here Anyway
						vb.begin(3, DefaultVertexFormats.POSITION);
						vb.pos(minCX, y, minCZ);
						vb.pos(maxCX, y, minCZ);
						vb.pos(maxCX, y, maxCZ);
						vb.pos(minCX, y, maxCZ);
						vb.pos(minCX, y, minCZ);
						tess.draw();
					}
					if(axis == Axis.Z)
					{
						AxisAlignedBB box = new AxisAlignedBB(pos.getX(), pos.getY(), aabb.minZ, pos.getX() + 1, pos.getY() + 1, aabb.maxZ).offset(xOffset, yOffset, zOffset);
						Double minCX = box.minX + 0.3125;
						Double minCY = box.minY + 0.3125;
						Double maxCX = box.maxX - 0.3125;
						Double maxCY = box.maxY - 0.3125;
						box = box.expandXyz(0.0020000000949949026D);
						Boolean n = side == EnumFacing.NORTH;
						Double z = n ? box.minZ : box.maxZ;
						// Down
						vb.begin(3, DefaultVertexFormats.POSITION);
						vb.pos(box.minX, box.minY, z).endVertex();
						vb.pos(minCX, minCY, z).endVertex();
						vb.pos(maxCX, minCY, z).endVertex();
						vb.pos(box.maxX, box.minY, z).endVertex();
						vb.pos(box.minX, box.minY, z).endVertex();
						tess.draw();
						// Up
						vb.begin(3, DefaultVertexFormats.POSITION);
						vb.pos(box.minX, box.maxY, z).endVertex();
						vb.pos(minCX, maxCY, z).endVertex();
						vb.pos(maxCX, maxCY, z).endVertex();
						vb.pos(box.maxX, box.maxY, z).endVertex();
						vb.pos(box.minX, box.maxY, z).endVertex();
						tess.draw();
						// North
						vb.begin(3, DefaultVertexFormats.POSITION);
						vb.pos(box.minX, box.minY, z).endVertex();
						vb.pos(minCX, minCY, z).endVertex();
						vb.pos(minCX, maxCY, z).endVertex();
						vb.pos(box.minX, box.maxY, z).endVertex();
						vb.pos(box.minX, box.minY, z).endVertex();
						tess.draw();
						// South
						vb.begin(3, DefaultVertexFormats.POSITION);
						vb.pos(box.maxX, box.maxY, z).endVertex();
						vb.pos(maxCX, maxCY, z).endVertex();
						vb.pos(maxCX, minCY, z).endVertex();
						vb.pos(box.maxX, box.minY, z).endVertex();
						vb.pos(box.maxX, box.maxY, z).endVertex();
						tess.draw();
						// Layer That's Not Needed But Is Here Anyway
						vb.begin(3, DefaultVertexFormats.POSITION);
						vb.pos(minCX, minCY, z);
						vb.pos(minCX, maxCY, z);
						vb.pos(maxCX, maxCY, z);
						vb.pos(maxCX, minCY, z);
						vb.pos(minCX, minCY, z);
						tess.draw();
					}
				}
				if(block instanceof BlockCornered)
				{
					switch(axis)
					{
						case X :
							AxisAlignedBB boxX = new AxisAlignedBB(aabb.minX, pos.getY(), pos.getZ(), aabb.maxX, pos.getY() + 1, pos.getZ() + 1).offset(xOffset, yOffset, zOffset).expandXyz(0.0020000000949949026D);
							Double x = side == EnumFacing.WEST ? boxX.minX : boxX.maxX;
							/* Down North */ drawX(tess, x, boxX.minY, boxX.minZ, boxX.minY + 0.5, boxX.minZ + 0.5);
							/* Up North */ drawX(tess, x, boxX.maxY - 0.5, boxX.minZ, boxX.maxY, boxX.minZ + 0.5);
							/* Down South */ drawX(tess, x, boxX.minY, boxX.maxZ - 0.5, boxX.minY + 0.5, boxX.maxZ);
							/* Up South */ drawX(tess, x, boxX.maxY - 0.5, boxX.maxZ - 0.5, boxX.maxY, boxX.maxZ);
							break;
						case Y :
							AxisAlignedBB boxY = new AxisAlignedBB(pos.getX(), aabb.minY, pos.getZ(), pos.getX() + 1, aabb.maxY, pos.getZ() + 1).offset(xOffset, yOffset, zOffset).expandXyz(0.0020000000949949026D);
							Double y = side == EnumFacing.DOWN ? boxY.minY : boxY.maxY;
							/* North West */ drawY(tess, y, boxY.minX, boxY.minZ, boxY.minX + 0.5, boxY.minZ + 0.5);
							/* North East */ drawY(tess, y, boxY.maxX - 0.5, boxY.minZ, boxY.maxX, boxY.minZ + 0.5);
							/* South West */ drawY(tess, y, boxY.minX, boxY.maxZ - 0.5, boxY.minX + 0.5, boxY.maxZ);
							/* South East */ drawY(tess, y, boxY.maxX - 0.5, boxY.maxZ - 0.5, boxY.maxX, boxY.maxZ);
							break;
						case Z :
							AxisAlignedBB boxZ = new AxisAlignedBB(pos.getX(), pos.getY(), aabb.minZ, pos.getX() + 1, pos.getY() + 1, aabb.maxZ).offset(xOffset, yOffset, zOffset).expandXyz(0.0020000000949949026D);
							Double z = side == EnumFacing.NORTH ? boxZ.minZ : boxZ.maxZ;
							/* Down West */ drawZ(tess, z, boxZ.minX, boxZ.minY, boxZ.minX + 0.5, boxZ.minY + 0.5);
							/* Down East */ drawZ(tess, z, boxZ.maxX - 0.5, boxZ.minY, boxZ.maxX, boxZ.minY + 0.5);
							/* Up West */ drawZ(tess, z, boxZ.minX, boxZ.maxY - 0.5, boxZ.minX + 0.5, boxZ.maxY);
							/* Up East */ drawZ(tess, z, boxZ.maxX - 0.5, boxZ.maxY - 0.5, boxZ.maxX, boxZ.maxY);
							break;
						default:
							Multipart_OH.I.getCTMultipart().getLogger().warn(side == null ? result + " has no side?" : result + " has no axis?");
					}
				}
				GlStateManager.depthMask(true);
				GlStateManager.enableTexture2D();
				GlStateManager.disableBlend();
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	private static void drawX(Tessellator tess, Double x, Double minY, Double minZ, Double maxY, Double maxZ)
	{
		VertexBuffer vb = tess.getBuffer();
		vb.begin(3, DefaultVertexFormats.POSITION);
		vb.pos(x, minY, minZ).endVertex();
		vb.pos(x, minY, maxZ).endVertex();
		vb.pos(x, maxY, maxZ).endVertex();
		vb.pos(x, maxY, minZ).endVertex();
		vb.pos(x, minY, minZ).endVertex();
		tess.draw();
	}
	
	@SideOnly(Side.CLIENT)
	private static void drawY(Tessellator tess, Double y, Double minX, Double minZ, Double maxX, Double maxZ)
	{
		VertexBuffer vb = tess.getBuffer();
		vb.begin(3, DefaultVertexFormats.POSITION);
		vb.pos(minX, y, minZ).endVertex();
		vb.pos(minX, y, maxZ).endVertex();
		vb.pos(maxX, y, maxZ).endVertex();
		vb.pos(maxX, y, minZ).endVertex();
		vb.pos(minX, y, minZ).endVertex();
		tess.draw();
	}
	
	@SideOnly(Side.CLIENT)
	private static void drawZ(Tessellator tess, Double z, Double minX, Double minY, Double maxX, Double maxY)
	{
		VertexBuffer vb = tess.getBuffer();
		vb.begin(3, DefaultVertexFormats.POSITION);
		vb.pos(minX, minY, z).endVertex();
		vb.pos(minX, maxY, z).endVertex();
		vb.pos(maxX, maxY, z).endVertex();
		vb.pos(maxX, minY, z).endVertex();
		vb.pos(minX, minY, z).endVertex();
		tess.draw();
	}
}
