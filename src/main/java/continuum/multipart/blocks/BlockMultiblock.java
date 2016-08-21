package continuum.multipart.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import continuum.api.multipart.CollidableAABB;
import continuum.api.multipart.MultiblockStateImpl;
import continuum.api.multipart.MultipartInfo;
import continuum.api.multipart.TileEntityMultiblock;
import continuum.essentials.block.CuboidSelector;
import continuum.essentials.block.IBlockBoundable;
import continuum.essentials.block.ICuboid;
import continuum.essentials.hooks.BlockHooks;
import continuum.essentials.hooks.ObjectHooks;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer.StateImplementation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleDigging;
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
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockMultiblock extends Block implements IBlockBoundable
{
	private AxisAlignedBB bounds = this.FULL_BLOCK_AABB;
	private Material tempMaterial;
	
	public BlockMultiblock()
	{
		super(Material.ROCK);
		this.setTickRandomly(true);
	}
	
	@Override
	public SoundType getSoundType(IBlockState state, World world, BlockPos pos, Entity entity)
	{
		RayTraceResult result = null;//entity instanceof EntityLivingBase ? ForgeHooks.rayTraceEyes((EntityLivingBase)entity, 5D) : null;
		try
		{
			result = Minecraft.getMinecraft().objectMouseOver;
			result = result.getBlockPos().equals(pos) ? result : null;
		}
		catch(Exception e)
		{
			
		}
		SoundType resultSound = result != null && result.hitInfo instanceof MultipartInfo ? ((MultipartInfo)result.hitInfo).getSoundType() : SoundType.STONE;
		Vec3d vec = entity == null ? null : new Vec3d(entity.posX, entity.posY, entity.posZ);
		SoundType blockSound = vec != null && (result = world.rayTraceBlocks(vec, vec.subtract(0, 3, 0))) != null && result.getBlockPos().equals(pos) && result.hitInfo instanceof MultipartInfo ? ((MultipartInfo)result.hitInfo).getSoundType() : SoundType.STONE;
		return new SoundType(result == null ? blockSound.getPitch() : resultSound.getPitch(), result == null ? blockSound.getPitch() : resultSound.getPitch(), resultSound.getBreakSound(), blockSound.getStepSound(), super.getSoundType().getPlaceSound(), resultSound.getHitSound(), blockSound.getFallSound());
	}
	
	@Override
	public Material getMaterial(IBlockState state)
	{
		return this.tempMaterial == null ? super.getMaterial(state) : this.tempMaterial;
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
			MultipartInfo info = (MultipartInfo)result.hitInfo;
			if(!info.addLandingEffects(world, entity, particles))
				BlockHooks.createLandingEffects(world, new Vec3d(entity.posX, entity.posY, entity.posZ), info.getActualState(), particles);
			return true;
		}
		return super.addLandingEffects(state, world, pos, uselessState, entity, particles);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean addHitEffects(IBlockState state, World world, RayTraceResult result, ParticleManager manager)
	{
		if(result != null && result.hitInfo instanceof MultipartInfo)
		{
			MultipartInfo info = (MultipartInfo)result.hitInfo;
			if(!info.addHitEffects(result, manager))
				BlockHooks.createHitEffects(manager, world, result, info.getSelectableBoxes().get(0).getSelectableCuboid(), state);
			return true;
		}
		return super.addHitEffects(state, world, result, manager);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager)
	{
		RayTraceResult result;
		if((result = Minecraft.getMinecraft().objectMouseOver) != null && result.hitInfo instanceof MultipartInfo)
		{
			MultipartInfo info = (MultipartInfo)result.hitInfo;
			if(!info.addDestroyEffects(manager))
				BlockHooks.createDestroyEffects(manager, world, pos, info.getActualState());
			return true;
		}
		return false;
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
				if(info.getBlock().getTickRandomly())
					info.getBlock().randomDisplayTick(info.getState(), world, pos, random);
			}
		}
	}
	
	@Override
	public int getLightValue(IBlockState state, IBlockAccess access, BlockPos pos)
	{
		IBlockState state1 = access.getBlockState(pos);
		if(state1.getBlock() != this)
			return state1.getLightValue(access, pos);
		TileEntity entity = access.getTileEntity(pos);
		if(entity instanceof TileEntityMultiblock)
			return ((TileEntityMultiblock)entity).getLight();
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
	
	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean harvest)
	{
		TileEntity entity = world.getTileEntity(pos);
		if(entity instanceof TileEntityMultiblock)
		{
			MultipartInfo info = this.getSelectedMultipart(player, world);
			if(info != null)
				((TileEntityMultiblock)entity).removeMultipartFromList(info).breakMultipart(world, pos, player);
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
			this.setSoundType(info.getMultipart().getBlock().getSoundType());
			this.tempMaterial = info.getMaterial();
			Float strength = getBlockStrength(world, pos, player, info);
			this.tempMaterial = null;
			return strength;
		}
		return 0;
	}
	
	public static float getBlockStrength(World world, BlockPos pos, EntityPlayer player, MultipartInfo info)
	{
		float hardness = info.getHardness();
		if(hardness < 0)
			return 0;
		if(!canHarvestMultipart(world, pos, player, info))
			return player.getDigSpeed(info.getState(), pos) / hardness / 100;
		else
			return player.getDigSpeed(info.getState(), pos) / hardness / 30;
	}
	
	public static boolean canHarvestMultipart(World world, BlockPos pos, EntityPlayer player, MultipartInfo info)
	{
		IBlockState state = info.getActualState();
		if(info.getMaterial().isToolNotRequired())
			return true;
		ItemStack stack = player.inventory.getCurrentItem();
		String tool = info.getTool();
		if(stack == null || tool == null)
			return player.canHarvestBlock(state);
		int toolLevel = stack.getItem().getHarvestLevel(stack, tool);
		if(toolLevel < 0)
			return player.canHarvestBlock(state);
		return toolLevel >= info.getHarvestLevel();
	}
	
	public MultipartInfo getSelectedMultipart(EntityPlayer player, IBlockAccess access)
	{
		RayTraceResult result = player.rayTrace(player.capabilities.isCreativeMode ? 4.5D : 5D, 1F);
		if(result != null && access.getBlockState(result.getBlockPos()).getBlock() == this)
			return (MultipartInfo)result.hitInfo;
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
		Vec3d vec0 = start.subtract(pos.getX(), pos.getY(), pos.getZ());
		Vec3d vec1 = end.subtract(pos.getX(), pos.getY(), pos.getZ());
		RayTraceResult result = boundingBox.calculateIntercept(vec0, vec1);
		return result == null ? null : new RayTraceResult(result.hitVec.addVector(pos.getX(), pos.getY(), pos.getZ()), result.sideHit, pos);
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
				if(!(aabb instanceof CollidableAABB && !((CollidableAABB)aabb).collidable) && box.intersectsWith(aabb = aabb.offset(pos)))
					list.add(aabb);
	}
	
	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess access, BlockPos pos)
	{
		TileEntity entity = access.getTileEntity(pos);
		if(entity instanceof TileEntityMultiblock && state instanceof StateImplementation)
		{
			return new MultiblockStateImpl((StateImplementation)state, (TileEntityMultiblock)entity, null);
		}
		return state;
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack stack, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		MultipartInfo info = this.getSelectedMultipart(player, world);
		if(info != null)
			return info.onActivated(player, hand, stack, facing, hitX, hitY, hitZ);
		return super.onBlockActivated(world, pos, state, player, hand, stack, facing, hitX, hitY, hitZ);
	}
	
	@Override
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
