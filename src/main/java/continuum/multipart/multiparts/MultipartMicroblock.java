package continuum.multipart.multiparts;

import java.util.List;

import com.google.common.collect.Lists;

import continuum.api.microblock.Microblock;
import continuum.api.microblock.MicroblockStateImpl;
import continuum.api.microblock.TileEntityMicroblockBase;
import continuum.api.microblock.material.MicroblockMaterialCapability;
import continuum.api.multipart.MultiblockStateImpl;
import continuum.api.multipart.Multipart;
import continuum.api.multipart.MultipartState;
import continuum.api.multipart.MultipartStateList;
import continuum.essentials.block.ICuboid;
import continuum.essentials.block.StaticCuboid;
import continuum.essentials.hooks.BlockHooks;
import continuum.multipart.blocks.BlockAxised;
import continuum.multipart.blocks.BlockCornered;
import continuum.multipart.blocks.BlockLayered;
import continuum.multipart.enums.DefaultMicroblock;
import continuum.multipart.items.ItemMicroblock;
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
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MultipartMicroblock extends Multipart
{
	private final Microblock microblock;
	
	public MultipartMicroblock(Microblock microblock)
	{
		this.microblock = microblock;
	}
	
	@Override
	public boolean canPlaceIn(IBlockAccess access, BlockPos pos, IBlockState state, MultipartStateList infoList, RayTraceResult result)
	{
		return !infoList.boxIntersectsList(this, this.getMicroblock().getSelectionBox(state), false, true);
	}
	
	@Override
	public boolean addLandingEffects(MultipartState info, WorldServer world, EntityLivingBase entity, int particles)
	{
		BlockHooks.createLandingEffects(world, new Vec3d(entity.posX, entity.posY, entity.posZ), info.hasTileEntity() && info.getTileEntity().hasCapability(MicroblockMaterialCapability.MICROBLOCKMATERIAL, null) ? info.getTileEntity().getCapability(MicroblockMaterialCapability.MICROBLOCKMATERIAL, null).getMaterial().getBlockState() : Blocks.AIR.getDefaultState(), particles);
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean addHitEffects(MultipartState info, RayTraceResult result, ParticleManager manager)
	{
		BlockHooks.createHitEffects(manager, info.getWorld(), result, this.getMicroblock().getSelectionBox(info.getState()), info.hasTileEntity() && info.getTileEntity().hasCapability(MicroblockMaterialCapability.MICROBLOCKMATERIAL, null) ? info.getTileEntity().getCapability(MicroblockMaterialCapability.MICROBLOCKMATERIAL, null).getMaterial().getBlockState() : Blocks.AIR.getDefaultState());
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean addDestroyEffects(MultipartState info, ParticleManager manager)
	{
		BlockHooks.createDestroyEffects(manager, info.getWorld(), info.getPos(), info.hasTileEntity() && info.getTileEntity().hasCapability(MicroblockMaterialCapability.MICROBLOCKMATERIAL, null) ? info.getTileEntity().getCapability(MicroblockMaterialCapability.MICROBLOCKMATERIAL, null).getMaterial().getBlockState() : Blocks.AIR.getDefaultState());
		return true;
	}
	
	@Override
	public Block getBlock()
	{
		return this.getMicroblock().getBlock();
	}
	
	public Microblock getMicroblock()
	{
		return this.microblock;
	}
	
	public int getMeta(IBlockState state)
	{
		return this.getBlock().getMetaFromState(state);
	}
	
	public Item getItem()
	{
		return Item.getItemFromBlock(this.getBlock());
	}
	
	@Override
	public List<AxisAlignedBB> getCollisionBoxes(MultipartState info)
	{
		return Lists.newArrayList(this.getMicroblock().getSelectionBox(info.getState()));
	}
	
	@Override
	public List<ICuboid> getSelectableCuboids(MultipartState info)
	{
		return Lists.newArrayList(new StaticCuboid(this.getMicroblock().getSelectionBox(info.getState())));
	}
	
	@Override
	public ItemStack getPickBlock(MultipartState info)
	{
		if(info.hasTileEntity() && info.getTileEntity().hasCapability(MicroblockMaterialCapability.MICROBLOCKMATERIAL, null))
		{
			ItemStack stack = new ItemStack(this.getItem(), 1, 0, new NBTTagCompound());
			if(stack.hasCapability(MicroblockMaterialCapability.MICROBLOCKMATERIAL, null))
			{
				stack.getCapability(MicroblockMaterialCapability.MICROBLOCKMATERIAL, null).setMaterial(info.getTileEntity().getCapability(MicroblockMaterialCapability.MICROBLOCKMATERIAL, null).getMaterial());
				return stack;
			}
		}
		return null;
	}
	
	@Override
	public IBlockState getMultipartState(MultipartState info)
	{
		return info.setState(BlockAxised.fixBlockState(info.getState()));
	}
	
	@Override
	public IBlockState getMultipartRenderState(MultipartState info)
	{
		IBlockState state = info.getState();
		if(info.hasTileEntity() && info.getTileEntity().hasCapability(MicroblockMaterialCapability.MICROBLOCKMATERIAL, null))
			state = new MicroblockStateImpl(state, this.getMicroblock(), info.getTileEntity().getCapability(MicroblockMaterialCapability.MICROBLOCKMATERIAL, null).getMaterial());
		return state;
	}
	
	@Override
	public void onMultipartPlaced(MultipartState info, EntityLivingBase entity, ItemStack stack)
	{
		if(stack != null && stack.hasCapability(MicroblockMaterialCapability.MICROBLOCKMATERIAL, null) && info.hasTileEntity() && info.getTileEntity().hasCapability(MicroblockMaterialCapability.MICROBLOCKMATERIAL, null))
			info.getTileEntity().getCapability(MicroblockMaterialCapability.MICROBLOCKMATERIAL, null).setMaterial(stack.getCapability(MicroblockMaterialCapability.MICROBLOCKMATERIAL, null).getMaterial());
	}
	
	@Override
	public SoundType getSoundType(MultipartState info)
	{
		return info.hasTileEntity() && info.getTileEntity().hasCapability(MicroblockMaterialCapability.MICROBLOCKMATERIAL, null) ? info.getTileEntity().getCapability(MicroblockMaterialCapability.MICROBLOCKMATERIAL, null).getMaterial().getSound() : SoundType.STONE;
	}
	
	@Override
	public Material getMaterial(MultipartState info)
	{
		IBlockState state = info.getExtendedState();
		if(state instanceof MultiblockStateImpl && (state = ((MultiblockStateImpl)state).getImplementation()) instanceof MicroblockStateImpl)
			return ((MicroblockStateImpl)state).getMicroblockMaterial().getBlockState().getMaterial();
		return this.getBlock().getMaterial(state);
	}
	
	@Override
	public boolean onMultipartActivated(MultipartState info, EntityPlayer player, EnumHand hand, ItemStack stack, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		IForgeRegistry<Multipart> multipartRegistry = GameRegistry.findRegistry(Multipart.class);
		if(multipartRegistry != null && stack != null && stack.getItem() instanceof ItemMicroblock)
		{
			Microblock microblock = ((ItemMicroblock)stack.getItem()).getMicroblock();
			if(DefaultMicroblock.isLayered(this.getMicroblock()) && DefaultMicroblock.isLayered(microblock) && side.getOpposite() == info.getState().getValue(BlockLayered.direction))
			{
				IBlockState state = microblock.getBlock().onBlockPlaced(info.getWorld(), info.getPos(), side, hitX, hitY, hitZ, stack.getMetadata(), player);
				if(state.getValue(BlockLayered.direction) == info.getState().getValue(BlockLayered.direction))
					state = state.withProperty(BlockLayered.direction, state.getValue(BlockLayered.direction).getOpposite());
				return Multipart_EH.attemptToPlace(info.getWorld(), info.getPos(), player, stack, Lists.newArrayList(), null, multipartRegistry.getValue(microblock.getBlock().getRegistryName()), state);
			}
			if(this.getMicroblock() == DefaultMicroblock.COVER)
			{
				EnumFacing direction = info.getState().getValue(BlockLayered.direction);
				if(side.getAxis() != direction.getAxis() && microblock == DefaultMicroblock.STRIP)
				{
					EnumFacing e1 = direction.ordinal() > side.ordinal() ? side : direction;
					EnumFacing e2 = direction.ordinal() > side.ordinal() ? direction : side;
					return Multipart_EH.attemptToPlace(info.getWorld(), info.getPos(), player, stack, Lists.newArrayList(), null, multipartRegistry.getValue(microblock.getBlock().getRegistryName()), microblock.getBlock().getDefaultState().withProperty(BlockAxised.direction1, e1).withProperty(BlockAxised.direction2, e2));
				}
			}
			if(this.getMicroblock() == DefaultMicroblock.CORNER)
				switch(side.getAxis())
				{
					case X :
						if(info.getState().getValue(BlockCornered.directionX) != side)
							return Multipart_EH.attemptToPlace(info.getWorld(), info.getPos(), player, stack, Lists.newArrayList(), null, multipartRegistry.getValue(microblock.getBlock().getRegistryName()), info.getState().withProperty(BlockCornered.directionX, side));
					case Y :
						if(info.getState().getValue(BlockCornered.directionY) != side)
							return Multipart_EH.attemptToPlace(info.getWorld(), info.getPos(), player, stack, Lists.newArrayList(), null, multipartRegistry.getValue(microblock.getBlock().getRegistryName()), info.getState().withProperty(BlockCornered.directionY, side));
					case Z :
						if(info.getState().getValue(BlockCornered.directionZ) != side)
							return Multipart_EH.attemptToPlace(info.getWorld(), info.getPos(), player, stack, Lists.newArrayList(), null, multipartRegistry.getValue(microblock.getBlock().getRegistryName()), info.getState().withProperty(BlockCornered.directionZ, side));
				}
		}
		return false;
	}
	
	@Override
	public float getHardness(MultipartState info)
	{
		IBlockState state = info.getExtendedState();
		if(state instanceof MicroblockStateImpl)
			return ((MicroblockStateImpl)state).getMicroblockMaterial().getBlockState().getBlockHardness(info.getWorld(), info.getPos());
		return 0F;
	}
	
	@Override
	public String getTool(MultipartState info)
	{
		IBlockState state = info.getExtendedState();
		if(state instanceof MicroblockStateImpl)
			return ((MicroblockStateImpl)state).getMicroblockMaterial().getTool();
		return "";
	}
	
	@Override
	public int getHarvestLevel(MultipartState info)
	{
		IBlockState state = info.getExtendedState();
		if(state instanceof MicroblockStateImpl)
			return ((MicroblockStateImpl)state).getMicroblockMaterial().getHarvestLevel();
		return 0;
	}
	
	@Override
	public int getLightValue(MultipartState info)
	{
		if(info.hasTileEntity() && info.getTileEntity().hasCapability(MicroblockMaterialCapability.MICROBLOCKMATERIAL, null))
			return info.getTileEntity().getCapability(MicroblockMaterialCapability.MICROBLOCKMATERIAL, null).getMaterial().getLight();
		return 0;
	}
	
	@Override
	public boolean canRenderInLayer(MultipartState info, BlockRenderLayer layer)
	{
		if(info.hasTileEntity() && info.getTileEntity().hasCapability(MicroblockMaterialCapability.MICROBLOCKMATERIAL, null))
			return info.getTileEntity().getCapability(MicroblockMaterialCapability.MICROBLOCKMATERIAL, null).getMaterial().canRenderInLayer(layer);
		return false;
	}
}
