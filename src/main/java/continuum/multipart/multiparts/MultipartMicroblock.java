package continuum.multipart.multiparts;

import java.util.List;

import com.google.common.collect.Lists;

import continuum.api.microblock.IMicroblock;
import continuum.api.microblock.IMicroblockType;
import continuum.api.microblock.MicroblockStateImpl;
import continuum.api.microblock.TileEntityMicroblock;
import continuum.api.microblock.texture.MicroblockMaterial;
import continuum.api.multipart.MultiblockStateImpl;
import continuum.api.multipart.Multipart;
import continuum.api.multipart.MultipartInfo;
import continuum.api.multipart.TileEntityMultiblock;
import continuum.essentials.block.ICuboid;
import continuum.essentials.hooks.BlockHooks;
import continuum.multipart.blocks.BlockAxised;
import continuum.multipart.mod.Multipart_EH;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer.StateImplementation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class MultipartMicroblock<MB> extends Multipart
{
	public static MicroblockMaterial currentEntry;
	private final Block block;
	private final IMicroblock microblock;
	
	public MultipartMicroblock(Block block, IMicroblock microblock)
	{
		this.block = block;
		this.microblock = microblock;
	}
	
	@Override
	public boolean canPlaceIn(IBlockAccess access, BlockPos pos, IBlockState state, TileEntityMultiblock multipart, RayTraceResult result)
	{
		return !multipart.boxIntersectsMultipart(this, this.getCuboidFromState(state).getSelectableCuboid(), false, true);
	}
	
	@Override
	public boolean addLandingEffects(MultipartInfo info, WorldServer world, EntityLivingBase entity, int particles)
	{
		BlockHooks.createLandingEffects(world, new Vec3d(entity.posX, entity.posY, entity.posZ), info.getTileEntity() instanceof TileEntityMicroblock ? ((TileEntityMicroblock)info.getTileEntity()).getEntry().getBaseState() : Blocks.AIR.getDefaultState(), particles);
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean addHitEffects(MultipartInfo info, RayTraceResult result, ParticleManager manager)
	{
		BlockHooks.createHitEffects(manager, info.getWorld(), result, this.getCuboidFromState(info.getActualState()).getSelectableCuboid(), info.getTileEntity() instanceof TileEntityMicroblock ? ((TileEntityMicroblock)info.getTileEntity()).getEntry().getBaseState() : Blocks.AIR.getDefaultState());
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean addDestroyEffects(MultipartInfo info, ParticleManager manager)
	{
		BlockHooks.createDestroyEffects(manager, info.getWorld(), info.getPos(), info.getTileEntity() instanceof TileEntityMicroblock ? ((TileEntityMicroblock)info.getTileEntity()).getEntry().getBaseState() : Blocks.AIR.getDefaultState());
		return true;
	}
	
	@Override
	public Block getBlock()
	{
		return this.block;
	}
	
	public IMicroblock getMicroblock()
	{
		return this.microblock;
	}
	
	public IMicroblockType getMicroblockType()
	{
		return this.getMicroblock().getType();
	}
	
	public Integer getMeta(IBlockState state)
	{
		return this.getBlock().getMetaFromState(state);
	}
	
	public ICuboid getCuboidFromState(IBlockState state)
	{
		return this.getMicroblockType().getCuboids()[this.getMeta(state)];
	}
	
	public Item getItem()
	{
		return Item.getItemFromBlock(this.getBlock());
	}
	
	@Override
	public List<AxisAlignedBB> getCollisionBoxes(MultipartInfo info)
	{
		return Lists.newArrayList(this.getCuboidFromState(info.getState()).getSelectableCuboid());
	}
	
	@Override
	public List<ICuboid> getCuboids(MultipartInfo info)
	{
		return Lists.newArrayList(this.getCuboidFromState(info.getState()));
	}
	
	@Override
	public ItemStack getPickBlock(MultipartInfo info)
	{
		if(info.getTileEntity() instanceof TileEntityMicroblock)
		{
			ItemStack stack = new ItemStack(this.getItem(), 1, 0, new NBTTagCompound());
			stack.setTagInfo("BlockEntityTag", ((TileEntityMicroblock)info.getTileEntity()).writeItemsToNBT());
			return stack;
		}
		return null;
	}
	
	@Override
	public IBlockState getMultipartState(MultipartInfo info)
	{
		return info.setState(BlockAxised.fixBlockState(info.getState()));
	}
	
	@Override
	public IBlockState getMultipartRenderState(MultipartInfo info)
	{
		IBlockState state = info.getState();
		TileEntity entity = info.getTileEntity();
		if(entity instanceof TileEntityMicroblock && state instanceof StateImplementation) state = new MicroblockStateImpl((StateImplementation)state, ((TileEntityMicroblock)entity).getEntry());
		return state;
	}
	
	@Override
	public void onMultipartPlaced(MultipartInfo info, EntityLivingBase entity, ItemStack stack)
	{
		if(stack != null && stack.hasTagCompound() && info.getTileEntity() instanceof TileEntityMicroblock)
		{
			TileEntityMicroblock tile = (TileEntityMicroblock)info.getTileEntity();
			tile.readItemsFromNBT(stack.getTagCompound().getCompoundTag("BlockEntityTag"));
			currentEntry = tile.getEntry();
		}
	}
	
	@Override
	public SoundType getSoundType(MultipartInfo info)
	{
		System.out.println(((TileEntityMicroblock)info.getTileEntity()).getEntry());
		return info.getTileEntity() instanceof TileEntityMicroblock ? ((TileEntityMicroblock)info.getTileEntity()).getEntry().getSound() : SoundType.STONE;
	}
	
	@Override
	public SoundType getSoundType(ItemStack stack)
	{
		return stack != null ? MicroblockMaterial.readFromNBT(stack.getTagCompound()).getSound() : SoundType.STONE;
	}
	
	@Override
	public Material getMaterial(MultipartInfo info)
	{
		IBlockState state = info.getExtendedState();
		if(state instanceof MultiblockStateImpl) if((state = ((MultiblockStateImpl)state).getImplementation()) instanceof MicroblockStateImpl) return (currentEntry = ((MicroblockStateImpl)state).entry).getBaseState().getMaterial();
		return this.getBlock().getMaterial(state);
	}
	
	@Override
	public boolean onMultipartActivated(MultipartInfo info, EntityPlayer player, EnumHand hand, ItemStack stack, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if(stack != null)
		{
			Block block =  Block.getBlockFromItem(stack.getItem());
			if(block instanceof IMicroblock)
			{
				IMicroblock microblock = (IMicroblock)block;
				IBlockState state1 = block.onBlockPlaced(info.getWorld(), info.getPos().offset(side), side, hitX, hitY, hitZ, stack.getMetadata(), player);
				return Multipart_EH.attemptToPlace(info.getWorld(), info.getPos().offset(side), player, stack, Lists.newArrayList(), player.rayTrace(player.capabilities.isCreativeMode ? 4.5D : 5D, 1F), info.getMultipart(), state1);
			}
		}
		return false;
	}
	
	@Override
	public float getHardness(MultipartInfo info)
	{
		IBlockState state = info.getExtendedState();
		if(state instanceof MicroblockStateImpl) return ((MicroblockStateImpl)state).entry.getBaseState().getBlockHardness(info.getWorld(), info.getPos());
		return 0F;
	}
	
	@Override
	public String getTool(MultipartInfo info)
	{
		IBlockState state = info.getExtendedState();
		if(state instanceof MicroblockStateImpl) return ((MicroblockStateImpl)state).entry.getTool();
		return "";
	}
	
	@Override
	public int getHarvestLevel(MultipartInfo info)
	{
		IBlockState state = info.getExtendedState();
		if(state instanceof MicroblockStateImpl) return ((MicroblockStateImpl)state).entry.getHarvestLevel();
		return 0;
	}
	
	@Override
	public int getLightValue(MultipartInfo info)
	{
		if(info.getTileEntity() instanceof TileEntityMicroblock)
			return ((TileEntityMicroblock)info.getTileEntity()).getEntry().getLight();
		return 0;
	}
}
