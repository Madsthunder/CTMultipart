package continuum.multipart.mod;

import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.Lists;

import continuum.api.microblock.IMicroblock;
import continuum.api.multipart.Multipart;
import continuum.api.multipart.MultipartInfo;
import continuum.api.multipart.MultipartAPI;
import continuum.api.multipart.TileEntityMultiblock;
import continuum.essentials.events.DebugInfoEvent;
import continuum.essentials.hooks.BlockHooks;
import continuum.essentials.mod.CTMod;
import continuum.multipart.blocks.BlockCornered;
import continuum.multipart.blocks.BlockLayered;
import continuum.multipart.blocks.BlockMultiblock;
import continuum.multipart.enums.CoverCuboid;
import continuum.multipart.enums.EnumMicroblockType.EnumPlaceType;
import continuum.multipart.enums.ILayeredCuboid;
import continuum.multipart.enums.PanelCuboid;
import continuum.multipart.enums.SlabCuboid;
import continuum.multipart.items.ItemMicroblock;
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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
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
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Multipart_EH
{
	private static CTMod<Multipart_OH, Multipart_EH> mod;
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public <T extends Comparable<T>>void onDebugInfoGet(DebugInfoEvent event)
	{
		RayTraceResult result = Minecraft.getMinecraft().objectMouseOver;
		if(event.getInfoSide() == DebugInfoEvent.EnumSide.RIGHT && result != null && result.typeOfHit == Type.BLOCK && result.getBlockPos() != null && result.hitInfo instanceof MultipartInfo)
		{
			List<String> list = event.getDebugInfo();
			if(list.remove(mod.getObjectHolder().multipart.getRegistryName().toString()))
			{
				MultipartInfo info = (MultipartInfo)result.hitInfo;
				World world = Minecraft.getMinecraft().theWorld;
				BlockPos pos = result.getBlockPos();
				IBlockState state = world.getWorldType() == WorldType.DEBUG_WORLD ? info.getState() : info.getActualState();
				list.add("(Multipart) " + info.getMultipart().getRegistryName());
                for(Entry<IProperty<?>, Comparable<?>> entry : state.getProperties().entrySet())
                {
                    IProperty property = entry.getKey();
                    Comparable value = entry.getValue();
                    String s = property.getName(value);
                    if (Boolean.TRUE.equals(value))
                        s = TextFormatting.GREEN + s;
                    else if (Boolean.FALSE.equals(value))
                        s = TextFormatting.RED + s;
                    list.add(property.getName() + ": " + s);
                }
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onItemRightClick(RightClickItem event)
	{
		if(MultipartAPI.apiActive())
		{
			ItemStack stack = event.getItemStack();
			EntityPlayer player = event.getEntityPlayer();
			RayTraceResult result = player.rayTrace(player.capabilities.isCreativeMode ? 5D : 4.5D, 1F);
			World world = event.getWorld();
			BlockPos pos = result.getBlockPos().offset(result.sideHit);
			Block possibleBlock = Block.getBlockFromItem(stack.getItem());
			if(possibleBlock == null) possibleBlock = ForgeRegistries.BLOCKS.getValue(stack.getItem().getRegistryName());
			Multipart prevEntry;
			Multipart possibleEntry;
			if(possibleBlock != null && (possibleEntry = MultipartAPI.getMultipartRegistry().getObject(stack.getItem().getRegistryName())) != MultipartAPI.getMultipartRegistry().getDefaultValue())
			{
				Vec3d hitPos = result.hitVec.subtract(pos.getX(), pos.getY(), pos.getZ());
				IBlockState prevBlock = world.getBlockState(pos);
				TileEntity prevTile = world.getTileEntity(pos);
				IBlockState possibleState = possibleBlock.onBlockPlaced(world, pos, result.sideHit, (float)hitPos.xCoord, (float)hitPos.yCoord, (float)hitPos.zCoord, stack.getMetadata(), player);
				if(prevBlock.getBlock() instanceof BlockMultiblock)
					attemptToPlace(world, pos, player, stack, Lists.newArrayList(), result, possibleEntry, possibleState);
				else if((prevEntry = MultipartAPI.getMultipartRegistry().getObject(prevBlock.getBlock().getRegistryName())) != null)
				{
					List<BlockSnapshot> snapshots = BlockHooks.setBlockStateWithSnapshots(world, pos, mod.getObjectHolder().multipart.getDefaultState());
					((TileEntityMultiblock)world.getTileEntity(pos)).addMultipartInfoToList(prevEntry, prevBlock, prevTile);
					attemptToPlace(world, pos, player, stack, snapshots, result, possibleEntry, possibleState);
				}
			}
		}
	}
	
	public static Boolean attemptToPlace(World world, BlockPos pos, EntityPlayer player, ItemStack stack, List<BlockSnapshot> snapshots, RayTraceResult result, Multipart multipart, IBlockState state)
	{
		TileEntity entity = world.getTileEntity(pos);
		TileEntityMultiblock multiblock;
		if(MultipartAPI.apiActive() && entity instanceof TileEntityMultiblock && multipart.canPlaceIn(world, pos, state, multiblock = (TileEntityMultiblock)entity, result))
		{
			TileEntity entity1 = state.getBlock().createTileEntity(world, state);
			setTileNBT(world, player, pos, stack, multiblock, entity1);
			multiblock.addMultipartInfoToList(multipart, state, entity1).onPlaced(player, stack);
			if(player instanceof EntityPlayerMP) ((EntityPlayerMP)player).connection.sendPacket(multiblock.getUpdatePacket());
			SoundType sound = state.getBlock().getSoundType(mod.getObjectHolder().multipart.getDefaultState(), world, pos, player);
			world.playSound(player, pos, sound.getPlaceSound(), SoundCategory.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
			player.swingArm(EnumHand.MAIN_HAND);
			world.checkLight(pos);
			world.markBlockRangeForRenderUpdate(pos.add(-8, -8, -8), pos.add(8, 8, 8));
			if(!player.capabilities.isCreativeMode) --stack.stackSize;
			return true;
		}
		else
			BlockHooks.restoreBlockSnapshots(world, snapshots);
		return false;
	}
	
	public static Boolean setTileNBT(World world, EntityPlayer player, BlockPos pos, ItemStack stack, TileEntityMultiblock multipart, TileEntity entity)
	{
		MinecraftServer minecraftserver = world.getMinecraftServer();
		if(minecraftserver == null)
			return false;
		else
		{
			if(stack.hasTagCompound() && stack.getTagCompound().hasKey("BlockEntityTag", 10)) if(entity != null)
			{
				if(!world.isRemote && entity.onlyOpsCanSetNbt() && (pos == null || !minecraftserver.getPlayerList().canSendCommands(player.getGameProfile()))) return false;
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
					multipart.markDirty();
					return true;
				}
			}
			return false;
		}
	}
	
	@SubscribeEvent
	public void onExceptionsGet(continuum.api.multipart.MultipartEvent.AABBExceptionsEvent event)
	{
		if(event.getMultipart() instanceof IMicroblock)
		{
			AxisAlignedBB box = event.getBox();
			EnumFacing exclude = tryFindFacing(box);
			if(exclude != null)
			{
				IMicroblock microblock = (IMicroblock)event.getMultipart();
				List<AxisAlignedBB> list = event.allowed;
				if(microblock.getType().getPlaceType() == EnumPlaceType.LAYERED)
				{
					addAllCuboids(exclude, SlabCuboid.values(), list);
					addAllCuboids(exclude, PanelCuboid.values(), list);
					addAllCuboids(exclude, CoverCuboid.values(), list);
				}
			}
		}
	}
	
	public static void addAllCuboids(EnumFacing exclude, ILayeredCuboid[] cuboids, List<AxisAlignedBB> list)
	{
		for(ILayeredCuboid cuboid : cuboids)
			if(cuboid.getSide() != exclude) list.add(cuboid.getSelectableCuboid());
	}
	
	public static EnumFacing tryFindFacing(AxisAlignedBB box)
	{
		for(SlabCuboid cuboid : SlabCuboid.values())
			if(cuboid.getSelectableCuboid() == box) return cuboid.getSide();
		for(PanelCuboid cuboid : PanelCuboid.values())
			if(cuboid.getSelectableCuboid() == box) return cuboid.getSide();
		for(CoverCuboid cuboid : CoverCuboid.values())
			if(cuboid.getSelectableCuboid() == box) return cuboid.getSide();
		return null;
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onBlockHighlight(DrawBlockHighlightEvent event)
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
						default :
							mod.getLogger().warn(side == null ? result + " has no side?" : result + " has no axis?");
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
	
	static void setMod(CTMod<Multipart_OH, Multipart_EH> mod)
	{
		Multipart_EH.mod = mod;
	}
}
