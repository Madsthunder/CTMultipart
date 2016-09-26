package continuum.api.multipart;

import java.util.List;

import com.google.common.collect.Lists;

import continuum.essentials.block.ICuboid;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class Multipart implements IForgeRegistryEntry<Multipart>
{
	/**
	 * @param access
	 *            The {@link IBlockAccess}.
	 * @param pos
	 *            The {@link BlockPos} of the TileEntityMultiblock.
	 * @param state
	 *            The {@link IBlockState} returned by
	 *            {@link Block#onBlockPlaced}.
	 * @param multiblock
	 *            The container for all of the existing {@link MultipartState}
	 *            instances.
	 * @param result
	 *            Hit information.
	 * @return Whether this {@link Multipart} can be placed in the provided
	 *         TileEntityMicroblock.
	 */
	public abstract boolean canPlaceIn(IBlockAccess access, BlockPos pos, IBlockState state, MultipartStateList infoList, RayTraceResult result);
	
	public abstract Block getBlock();
	
	public Item getItem()
	{
		return GameData.getBlockItemMap().get(this.getBlock());
	};
	
	/**
	 * @param info
	 *            Information that could be useful.
	 * @return A list of ICuboid instances that can be selected to show this
	 *         Multipart instance
	 */
	public abstract List<ICuboid> getSelectableCuboids(MultipartState info);
	
	/**
	 * 
	 * @param info
	 *            Information that could be useful.
	 * @return A list of collision boxes.
	 */
	public List<AxisAlignedBB> getCollisionBoxes(MultipartState<?> info)
	{
		List<AxisAlignedBB> boxes = Lists.newArrayList();
		for(ICuboid cuboid : info.getSelectableCuboids())
			boxes.add(cuboid.getSelectableCuboid());
		return boxes;
	}
	
	/**
	 * Basically a variation of {@link Block#getActualState}.
	 * 
	 * @param info
	 *            Information that could be useful.
	 * @return The 'actual' state of this {@link Multipart}.
	 */
	public IBlockState getMultipartState(MultipartState info)
	{
		return info.getState();
	}
	
	/**
	 * Basically a variation of {@link Block#getExtendedState}
	 * 
	 * @param info
	 *            Information that could be useful.
	 * @return The 'extended' state of this {@link Multipart}.
	 */
	public IBlockState getMultipartRenderState(MultipartState info)
	{
		return info.getActualState();
	}
	
	/**
	 * Called after this {@link Multipart} is added to a
	 * {@link MultipartStateList} instance.
	 * 
	 * @param info
	 *            Information that could be useful.
	 * @param entity
	 *            The {@link EntityLivingBase} placing this {@link Multipart}.
	 * @param stack
	 *            The {@link ItemStack} the {@link EntityLivingBase} is using.
	 */
	public void onMultipartPlaced(MultipartState info, EntityLivingBase entity, ItemStack stack)
	{
	}
	
	public boolean onMultipartActivated(MultipartState info, EntityPlayer player, EnumHand hand, ItemStack stack, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		return false;
	}
	
	public void onNeighborChange(MultipartState info, Block neighborBlock)
	{
	}
	
	public void breakMultipart(MultipartState info, EntityPlayer player)
	{
		if(!player.isCreative())
			Block.spawnAsEntity(info.getWorld(), info.getPos(), info.getPickBlock());
	}
	
	public int getLightValue(MultipartState info)
	{
		return info.getActualState().getLightValue();
	}
	
	public boolean isSideSolid(MultipartState info, EnumFacing side)
	{
		return false;
	}
	
	public ItemStack getPickBlock(MultipartState info)
	{
		return new ItemStack(info.getBlock());
	}
	
	public Material getMaterial(MultipartState info)
	{
		return info.getActualState().getMaterial();
	}
	
	public float getHardness(MultipartState info)
	{
		return info.getActualState().getBlockHardness(info.getWorld(), info.getPos());
	}
	
	public int getHarvestLevel(MultipartState info)
	{
		return info.getBlock().getHarvestLevel(info.getActualState());
	}
	
	public String getTool(MultipartState info)
	{
		return info.getBlock().getHarvestTool(info.getActualState());
	}
	
	public boolean addLandingEffects(MultipartState info, WorldServer world, EntityLivingBase entity, int particles)
	{
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	public boolean addHitEffects(MultipartState info, RayTraceResult result, ParticleManager manager)
	{
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	public boolean addDestroyEffects(MultipartState info, ParticleManager manager)
	{
		return false;
	}
	
	public SoundType getSoundType(MultipartState info)
	{
		return info.getBlock().getSoundType();
	}
	
	@SideOnly(Side.CLIENT)
	public boolean canRenderInLayer(MultipartState info, BlockRenderLayer layer)
	{
		return this.getRenderLayer(info) == layer;
	}
	
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer(MultipartState info)
	{
		return BlockRenderLayer.SOLID;
	}
	
	@Override
	public final Multipart setRegistryName(ResourceLocation name)
	{
		return this;
	}
	
	@Override
	public final ResourceLocation getRegistryName()
	{
		return this.getBlock().getRegistryName();
	}
	
	@Override
	public final Class<? super Multipart> getRegistryType()
	{
		return Multipart.class;
	}
	
	@Override
	public final int hashCode()
	{
		return this.getRegistryName().hashCode();
	}
}