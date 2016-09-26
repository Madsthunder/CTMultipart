package continuum.multipart.blocks;

import java.util.List;

import com.google.common.collect.Lists;

import continuum.api.microblock.Microblock;
import continuum.api.microblock.MicroblockStateImpl;
import continuum.api.microblock.TileEntityMicroblock;
import continuum.api.microblock.compat.MultipartMicroblock;
import continuum.api.microblock.texture.MicroblockMaterial;
import continuum.essentials.hooks.BlockHooks;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockMicroblockBase extends BlockContainer
{
	private final Microblock microblock;
	private Material tempMaterial;
	
	public BlockMicroblockBase(Microblock microblock)
	{
		super(Material.ROCK);
		this.microblock = microblock;
		this.setRegistryName("microblock" + microblock.getName());
		this.setUnlocalizedName("microblock" + microblock.getName());
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
			return ((TileEntityMicroblock)tile).getMaterial().getSound();
		return super.getSoundType(state, world, pos, entity);
	}
	
	@Override
	public boolean addLandingEffects(IBlockState state, WorldServer world, BlockPos pos, IBlockState uselessState, EntityLivingBase entity, int particles)
	{
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileEntityMicroblock)
		{
			BlockHooks.createLandingEffects(world, new Vec3d(entity.posX, entity.posY, entity.posZ), ((TileEntityMicroblock)tile).getMaterial().getBlockState(), particles);
			return true;
		}
		return super.addLandingEffects(state, world, pos, uselessState, entity, particles);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean addHitEffects(IBlockState state, World world, RayTraceResult result, ParticleManager manager)
	{
		TileEntity tile = world.getTileEntity(result.getBlockPos());
		if(tile instanceof TileEntityMicroblock)
		{
			BlockHooks.createHitEffects(manager, world, result, this.microblock.getSelectionBox(state), ((TileEntityMicroblock)tile).getMaterial().getBlockState());
			return true;
		}
		return super.addHitEffects(state, world, result, manager);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager)
	{
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileEntityMicroblock)
		{
			BlockHooks.createDestroyEffects(manager, world, pos, ((TileEntityMicroblock)tile).getMaterial().getBlockState());
			return true;
		}
		return super.addDestroyEffects(world, pos, manager);
	}
	
	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean harvest)
	{
		TileEntity entity = world.getTileEntity(pos);
		return super.removedByPlayer(state, world, pos, player, harvest);
	}
	
	@Override
	public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World world, BlockPos pos)
	{
		TileEntity entity = world.getTileEntity(pos);
		if(entity instanceof TileEntityMicroblock)
		{
			MicroblockMaterial material = ((TileEntityMicroblock)entity).getMaterial();
			this.tempMaterial = material.getBlockState().getMaterial();
			this.setHardness(material.getBlockState().getBlockHardness(world, pos));
			this.setHarvestLevel(material.getTool(), material.getHarvestLevel());
			float strength = ForgeHooks.blockStrength(state, player, world, pos);
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
		if(entity instanceof TileEntityMicroblock)
		{
			ItemStack stack = new ItemStack(this);
			stack.setTagCompound(MicroblockMaterial.writeToNBT(((TileEntityMicroblock)entity).getMaterial()));
			list.add(stack);
		}
		return list;
	}
	
	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess access, BlockPos pos)
	{
		TileEntity entity = access.getTileEntity(pos);
		if(entity instanceof TileEntityMicroblock) state = new MicroblockStateImpl(state, this.microblock, ((TileEntityMicroblock)entity).getMaterial());
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
			}
		}
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
	{
		return EnumBlockRenderType.MODEL;
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess access, BlockPos pos)
	{
		return this.microblock.getSelectionBox(state);
	}
	
	public final Microblock getMicroblock()
	{
		return this.microblock;
	}
}
