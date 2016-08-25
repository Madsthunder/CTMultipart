package continuum.multipart.blocks;

import java.util.List;

import com.google.common.collect.Lists;

import continuum.api.microblock.IMicroblock;
import continuum.api.microblock.IMicroblockType;
import continuum.api.microblock.MicroblockStateImpl;
import continuum.api.microblock.TileEntityMicroblock;
import continuum.api.microblock.texture.MicroblockMaterial;
import continuum.api.multipart.Multipart;
import continuum.multipart.mod.Multipart_OH;
import continuum.multipart.multiparts.MultipartMicroblock;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer.StateImplementation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockMicroblockBase<MT extends IMicroblockType> extends BlockContainer implements IMicroblock<MT>
{
	private final MultipartMicroblock multipart;
	public MT type;
	private Material tempMaterial;
	
	public BlockMicroblockBase(Multipart_OH objectHolder, MT type)
	{
		super(Material.ROCK);
		this.multipart = new MultipartMicroblock(this, this);
		this.type = type;
		this.setRegistryName("microblock" + this.type.getName());
		this.setUnlocalizedName("microblock" + this.type.getName());
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
	public SoundType getSoundType(IBlockState state, World world, BlockPos pos, Entity entity)
	{
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileEntityMicroblock)
			return ((TileEntityMicroblock)tile).getEntry().getSound();
		return super.getSoundType(state, world, pos, entity);
	}
	
	@Override
	public boolean addLandingEffects(IBlockState state, WorldServer world, BlockPos pos, IBlockState uselessState, EntityLivingBase entity, int particles)
	{
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileEntityMicroblock) MultipartMicroblock.currentEntry = ((TileEntityMicroblock)tile).getEntry();
		return super.addLandingEffects(state, world, pos, uselessState, entity, particles);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean addHitEffects(IBlockState state, World world, RayTraceResult result, ParticleManager manager)
	{
		TileEntity entity = world.getTileEntity(result.getBlockPos());
		if(entity instanceof TileEntityMicroblock) MultipartMicroblock.currentEntry = ((TileEntityMicroblock)entity).getEntry();
		return super.addHitEffects(state, world, result, manager);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager)
	{
		TileEntity entity = world.getTileEntity(pos);
		if(entity instanceof TileEntityMicroblock) MultipartMicroblock.currentEntry = ((TileEntityMicroblock)entity).getEntry();
		return super.addDestroyEffects(world, pos, manager);
	}
	
	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean harvest)
	{
		TileEntity entity = world.getTileEntity(pos);
		if(entity instanceof TileEntityMicroblock) MultipartMicroblock.currentEntry = ((TileEntityMicroblock)entity).getEntry();
		return super.removedByPlayer(state, world, pos, player, harvest);
	}
	
	@Override
	public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World world, BlockPos pos)
	{
		TileEntity entity = world.getTileEntity(pos);
		if(entity instanceof TileEntityMicroblock)
		{
			MultipartMicroblock.currentEntry = ((TileEntityMicroblock)entity).getEntry();
			this.tempMaterial = MultipartMicroblock.currentEntry.getBaseState().getMaterial();
			this.setHardness(MultipartMicroblock.currentEntry.getBaseState().getBlockHardness(world, pos));
			this.setHarvestLevel(MultipartMicroblock.currentEntry.getTool(), MultipartMicroblock.currentEntry.getHarvestLevel());
			Float strength = ForgeHooks.blockStrength(state, player, world, pos);
			this.setHarvestLevel(null, 0);
			this.tempMaterial = null;
			return strength;
		}
		return 0;
	}
	
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
	{
		List<ItemStack> list = Lists.newArrayList();
		TileEntity entity = world.getTileEntity(pos);
		if(MultipartMicroblock.currentEntry != null)
		{
			ItemStack stack = new ItemStack(this);
			stack.setTagCompound(MicroblockMaterial.writeToNBT(MultipartMicroblock.currentEntry));
			list.add(stack);
		}
		return list;
	}
	
	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess access, BlockPos pos)
	{
		TileEntity entity = access.getTileEntity(pos);
		if(entity instanceof TileEntityMicroblock) state = new MicroblockStateImpl((StateImplementation)state, ((TileEntityMicroblock)entity).getEntry());
		return state;
	}
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult result, World world, BlockPos pos, EntityPlayer player)
	{
		TileEntity entity = world.getTileEntity(pos);
		if(entity instanceof TileEntityMicroblock)
		{
			ItemStack stack = new ItemStack(this);
			stack.setTagCompound(new NBTTagCompound());
			NBTTagCompound compound = new NBTTagCompound();
			compound.merge(((TileEntityMicroblock)entity).writeItemsToNBT());
			stack.setTagInfo("BlockEntityTag", compound);
			return stack;
		}
		return null;
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new TileEntityMicroblock();
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
	public boolean shouldSideBeRendered(IBlockState state, IBlockAccess access, BlockPos pos, EnumFacing side)
	{
		return true;
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
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack)
	{
		if(stack != null && stack.hasTagCompound())
		{
			TileEntity tile = world.getTileEntity(pos);
			if(tile instanceof TileEntityMicroblock)
			{
				((TileEntityMicroblock)tile).readItemsFromNBT(stack.getTagCompound().getCompoundTag("BlockEntityTag"));
				MultipartMicroblock.currentEntry = ((TileEntityMicroblock)tile).getEntry();
			}
		}
	}
	
	@Override
	public Multipart getMultipart()
	{
		return this.multipart;
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
	{
		return EnumBlockRenderType.MODEL;
	}
	
	@Override
	public MT getType()
	{
		return this.type;
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess access, BlockPos pos)
	{
		return this.getType().getCuboids()[state.getBlock().getMetaFromState(state)].getSelectableCuboid();
	}
}
