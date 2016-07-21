package continuum.api.multipart.implementations;

import java.util.List;

import com.google.common.collect.Lists;

import continuum.api.multipart.MultipartInfo;
import continuum.api.multipart.TileEntityMultiblock;
import continuum.essentials.block.ICuboid;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class Multipart implements IForgeRegistryEntry<Multipart>
{
	public abstract boolean canPlaceIn(IBlockAccess access, BlockPos pos, IBlockState state, TileEntityMultiblock source, RayTraceResult result);
	
	public abstract List<ICuboid> getCuboids(MultipartInfo info);
	
	public List<AxisAlignedBB> getCollisionBoxes(MultipartInfo info)
	{
		List<AxisAlignedBB> boxes = Lists.newArrayList();
		for(ICuboid cuboid : info.getSelectableBoxes())
			boxes.add(cuboid.getSelectableCuboid());
		return boxes;
	}
	
	public IBlockState getMultipartState(MultipartInfo info)
	{
		return info.getState();
	}
	
	public IBlockState getMultipartRenderState(MultipartInfo info)
	{
		return info.getActualState();
	}
	
	public void onMultipartPlaced(MultipartInfo info, EntityLivingBase entity, ItemStack stack)
	{
		
	}
	
	public boolean onMultipartActivated(MultipartInfo info, EntityPlayer player, EnumHand hand, ItemStack stack, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		return false;
	}
	
	public void onNeighborChange(MultipartInfo info, Block neighborBlock)
	{
		
	}
	
	public void breakMultipart(MultipartInfo info, EntityPlayer player)
	{
		if(!player.isCreative()) Block.spawnAsEntity(info.getWorld(), info.getPos(), info.getPickBlock());
	}
	
	public int getLightValue(MultipartInfo info)
	{
		return info.getActualState().getLightValue();
	}
	
	public boolean isSideSolid(MultipartInfo info, EnumFacing side)
	{
		return false;
	}
	
	public ItemStack getPickBlock(MultipartInfo info)
	{
		return new ItemStack(info.getBlock());
	}
	
	public Material getMaterial(MultipartInfo info)
	{
		return info.getActualState().getMaterial();
	}
	
	public float getHardness(MultipartInfo info)
	{
		return info.getActualState().getBlockHardness(info.getWorld(), info.getPos());
	}
	
	public int getHarvestLevel(MultipartInfo info)
	{
		return info.getBlock().getHarvestLevel(info.getActualState());
	}
	
	public String getTool(MultipartInfo info)
	{
		return info.getBlock().getHarvestTool(info.getActualState());
	}
	
	public boolean addLandingEffects(MultipartInfo info, EntityLivingBase entity, int particles)
	{
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	public boolean addHitEffects(MultipartInfo info, RayTraceResult result, ParticleManager manager)
	{
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	public boolean addDestroyEffects(MultipartInfo info, ParticleManager manager)
	{
		return false;
	}
	
	@Override
	public final Multipart setRegistryName(ResourceLocation name)
	{
		//NOOP
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
	
	public abstract Block getBlock();
	
	public SoundType getSoundType(MultipartInfo info)
	{
		return info.getBlock().getSoundType();
	}
}