package continuum.api.multipart;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import continuum.api.multipart.boundingboxes.CollidableAABB;
import continuum.api.multipart.state.MultiblockStateImpl;
import continuum.essentials.block.CuboidSelector;
import continuum.essentials.block.IBlockBoundable;
import continuum.essentials.block.ICuboid;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer.StateImplementation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockMultipart extends Block implements IBlockBoundable
{
	private AxisAlignedBB bounds = this.FULL_BLOCK_AABB;
	private Material tempMaterial;
	public static MultipartInfo currentData;
	
	public BlockMultipart()
	{
		super(Material.ROCK);
		this.setTickRandomly(true);
	}
	
	@Override
	public SoundType getSoundType()
	{
		return currentData == null ? SoundType.STONE : currentData.getSoundType();
	}
	
	@Override
	public Material getMaterial(IBlockState state)
	{
		if(this.tempMaterial == null)
			return super.getMaterial(state);
		else
			return this.tempMaterial;
	}
	
	@Override
	public int getLightValue(IBlockState state)
	{
		return this.lightValue;
	}
	
	@Override
	public boolean addLandingEffects(IBlockState state, WorldServer world, BlockPos pos, IBlockState uselessState, EntityLivingBase entity, int particles)
	{
		RayTraceResult result = world.rayTraceBlocks(new Vec3d(entity.posX, entity.posY, entity.posZ), new Vec3d(entity.posX, entity.posY - 3, entity.posZ));
		if(result != null && result.hitInfo instanceof MultipartInfo)
		{
			currentData = (MultipartInfo)result.hitInfo;
			return currentData.addLandingEffects(entity, particles);
		}
		return super.addLandingEffects(state, world, pos, uselessState, entity, particles);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean addHitEffects(IBlockState state, World world, RayTraceResult result, ParticleManager manager)
	{
		if(result.hitInfo instanceof MultipartInfo)
		{
			currentData = (MultipartInfo)result.hitInfo;
			return currentData.addHitEffects(result, manager);
		}
		return super.addHitEffects(state, world, result, manager);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager)
	{
		if(Minecraft.getMinecraft().objectMouseOver != null)
			if(Minecraft.getMinecraft().objectMouseOver.hitInfo instanceof MultipartInfo)
			{
				currentData = (MultipartInfo)Minecraft.getMinecraft().objectMouseOver.hitInfo;
				return currentData.addDestroyEffects(manager);
			}
			else
				return super.addDestroyEffects(world, pos, manager);
		else
			return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random random)
	{
		TileEntity entity = world.getTileEntity(pos);
		if(entity instanceof TileEntityMultiblock)
		{
			TileEntityMultiblock multipart = (TileEntityMultiblock)entity;
			List<MultipartInfo> infoList = multipart.getAllInfo();
			if(!infoList.isEmpty())
			{
				MultipartInfo info = infoList.get(random.nextInt(infoList.size()));
				if(info.getBlock().getTickRandomly()) info.getBlock().randomDisplayTick(info.getState(), world, pos, random);
			}
		}
	}
	
	@Override
	public int getLightValue(IBlockState state, IBlockAccess access, BlockPos pos)
	{
		IBlockState state1 = access.getBlockState(pos);
		if(state1.getBlock() != this) return state1.getLightValue(access, pos);
		TileEntity entity = access.getTileEntity(pos);
		if(entity instanceof TileEntityMultiblock) return ((TileEntityMultiblock)entity).getLight();
		return state.getLightValue();
	}
	
	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}
	
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean harvest)
	{
		TileEntity entity = world.getTileEntity(pos);
		if(entity instanceof TileEntityMultiblock)
		{
			MultipartInfo info = this.getSelectedMultipart(player, world);
			currentData = info;
			if(info != null) ((TileEntityMultiblock)entity).removeMultipartFromList(info).breakMultipart(world, pos, player);
			world.notifyBlockUpdate(pos, state, state, 2);
		}
		return false;
	}
	
	@Override
	public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World world, BlockPos pos)
	{
		MultipartInfo info = this.getSelectedMultipart(player, world);
		if(info != null)
		{
			currentData = info;
			this.setSoundType(info.getMultipart().getBlock().getSoundType());
			this.tempMaterial = info.getMaterial();
			Float strength = getBlockStrength(world, pos, player, info);
			this.tempMaterial = null;
			return strength;
		}
		return 0F;
	}
	
	public static Float getBlockStrength(World world, BlockPos pos, EntityPlayer player, MultipartInfo info)
	{
		Float hardness = info.getHardness();
		if(hardness < 0.0F) return 0.0F;
		if(!canHarvestMultipart(world, pos, player, info))
			return player.getDigSpeed(info.getState(), pos) / hardness / 100F;
		else
			return player.getDigSpeed(info.getState(), pos) / hardness / 30F;
	}
	
	public static Boolean canHarvestMultipart(World world, BlockPos pos, EntityPlayer player, MultipartInfo info)
	{
		IBlockState state = info.getActualState();
		if(info.getMaterial().isToolNotRequired()) return true;
		ItemStack stack = player.inventory.getCurrentItem();
		String tool = info.getTool();
		if(stack == null || tool == null) return player.canHarvestBlock(state);
		int toolLevel = stack.getItem().getHarvestLevel(stack, tool);
		if(toolLevel < 0) return player.canHarvestBlock(state);
		return toolLevel >= info.getHarvestLevel();
	}
	
	public MultipartInfo getSelectedMultipart(EntityPlayer player, IBlockAccess access)
	{
		RayTraceResult result = player.rayTrace(player.capabilities.isCreativeMode ? 4.5D : 5D, 1F);
		if(result != null && access.getBlockState(result.getBlockPos()).getBlock() == this) return (MultipartInfo)result.hitInfo;
		return null;
	}
	
	@Override
	public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d finish)
	{
		TileEntityMultiblock multipart = (TileEntityMultiblock)world.getTileEntity(pos);
		ArrayList<ICuboid> cuboids = Lists.newArrayList();
		for(MultipartInfo info : multipart.getAllInfo())
			for(ICuboid cuboid : info.getSelectableBoxes())
				cuboids.add(cuboid.copy().addExtraData(info));
		return CuboidSelector.getSelectionBox(this, world, pos, start, finish, cuboids);
	}
	
	@Override
	public RayTraceResult rayTrace(BlockPos pos, Vec3d start, Vec3d end, AxisAlignedBB boundingBox)
	{
		Vec3d vec3d = start.subtract((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
		Vec3d vec3d1 = end.subtract((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
		RayTraceResult raytraceresult = boundingBox.calculateIntercept(vec3d, vec3d1);
		return raytraceresult == null ? null : new RayTraceResult(raytraceresult.hitVec.addVector((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()), raytraceresult.sideHit, pos);
	}
	
	@Override
	public void setBlockBounds(AxisAlignedBB aabb)
	{
		this.bounds = aabb;
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state)
	{
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(World worldIn, IBlockState state)
	{
		return new TileEntityMultiblock();
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess access, BlockPos pos)
	{
		return this.bounds;
	}
	
	@Override
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB box, List<AxisAlignedBB> list, Entity entity)
	{
		TileEntityMultiblock source = (TileEntityMultiblock)world.getTileEntity(pos);
		for(MultipartInfo info : source.getAllInfo())
			for(AxisAlignedBB aabb : info.getCollisonBoxes())
				if(!(aabb instanceof CollidableAABB && !((CollidableAABB)aabb).collidable) && this.intersects(box, info.getBlock(), aabb = aabb.offset(pos))) list.add(aabb);
	}
	
	private boolean intersects(AxisAlignedBB box, Block block, AxisAlignedBB aabb)
	{
		Boolean i = box.intersectsWith(aabb);
		if(i)
		{
			Double j = box.calculateYOffset(aabb, 100);
			Double k = box.calculateYOffset(aabb.setMaxY(aabb.maxY + 1), 100);
			if(j != k) this.setSoundType(block.getSoundType());
		}
		return i;
	}
	
	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess access, BlockPos pos)
	{
		TileEntity entity = access.getTileEntity(pos);
		if(entity instanceof TileEntityMultiblock && state instanceof StateImplementation) return new MultiblockStateImpl((StateImplementation)state, (TileEntityMultiblock)entity, null);
		return state;
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack stack, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		MultipartInfo info = this.getSelectedMultipart(player, world);
		if(info != null) return info.onActivated(player, hand, stack, facing, hitX, hitY, hitZ);
		return super.onBlockActivated(world, pos, state, player, hand, stack, facing, hitX, hitY, hitZ);
	}
	
	public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face)
	{
		return false;
	}
	
	@Override
	public BlockRenderLayer getBlockLayer()
	{
		return BlockRenderLayer.TRANSLUCENT;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
	{
		return true;
	}
}
