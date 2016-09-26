package continuum.api.multipart;

import java.util.List;
import java.util.UUID;

import continuum.essentials.block.ICuboid;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer.StateImplementation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MultipartState<M extends Multipart> implements INBTSerializable<NBTTagCompound>
{
	private final UUID uuid;
	private final MultipartStateList infoList;
	private final M multipart;
	private int meta;
	private TileEntity entity;
	
	public MultipartState(UUID uuid, MultipartStateList infoList, M multipart, IBlockState state)
	{
		this(uuid, infoList, multipart, state.getBlock().getMetaFromState(state));
	}
	
	public MultipartState(UUID uuid, MultipartStateList infoList, M multipart, int meta)
	{
		this(uuid, infoList, multipart, meta, null);
	}
	
	public MultipartState(UUID uuid, MultipartStateList infoList, M multipart, IBlockState state, TileEntity entity)
	{
		this(uuid, infoList, multipart, state.getBlock().getMetaFromState(state), entity);
	}
	
	public MultipartState(UUID uuid, MultipartStateList infoList, M multipart, int meta, TileEntity entity)
	{
		this.uuid = uuid;
		this.infoList = infoList;
		this.multipart = multipart;
		this.meta = meta;
		this.entity = entity;
	}
	
	public List<ICuboid> getSelectableCuboids()
	{
		return this.getMultipart().getSelectableCuboids(this);
	}
	
	public List<AxisAlignedBB> getCollisonBoxes()
	{
		return this.getMultipart().getCollisionBoxes(this);
	}
	
	public IBlockState getActualState()
	{
		return this.getActualState(false);
	}
	
	public IBlockState getActualState(boolean addImpl)
	{
		IBlockState state = this.getMultipart().getMultipartState(this);
		if(addImpl && state instanceof StateImplementation)
			state = new MultiblockStateImpl((StateImplementation)state, this.infoList, this);
		return state;
	}
	
	public IBlockState getExtendedState()
	{
		return this.getExtendedState(false);
	}
	
	public IBlockState getExtendedState(boolean addImpl)
	{
		IBlockState state = this.getMultipart().getMultipartRenderState(this);
		if(addImpl && state instanceof StateImplementation)
			state = new MultiblockStateImpl((StateImplementation)state, this.infoList, this);
		return state;
	}
	
	public void onPlaced(EntityLivingBase entity, ItemStack stack)
	{
		this.getMultipart().onMultipartPlaced(this, entity, stack);
	}
	
	public boolean onActivated(EntityPlayer player, EnumHand hand, ItemStack stack, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		return this.getMultipart().onMultipartActivated(this, player, hand, stack, side, hitX, hitY, hitZ);
	}
	
	public void onNeighborChanged(Block neighbor)
	{
		this.getMultipart().onNeighborChange(this, neighbor);
	}
	
	public void breakMultipart(World world, BlockPos pos, EntityPlayer player)
	{
		this.getMultipart().breakMultipart(this, player);
	}
	
	public int getLightValue()
	{
		return this.getMultipart().getLightValue(this);
	}
	
	public boolean isSideSolid(EnumFacing side)
	{
		return this.getMultipart().isSideSolid(this, side);
	}
	
	public ItemStack getPickBlock()
	{
		return this.getMultipart().getPickBlock(this);
	}
	
	public Material getMaterial()
	{
		return this.getMultipart().getMaterial(this);
	}
	
	public float getHardness()
	{
		return this.getMultipart().getHardness(this);
	}
	
	public int getHarvestLevel()
	{
		return this.getMultipart().getHarvestLevel(this);
	}
	
	public String getTool()
	{
		return this.getMultipart().getTool(this);
	}
	
	public boolean addLandingEffects(WorldServer world, EntityLivingBase entity, int particles)
	{
		return this.getMultipart().addLandingEffects(this, world, entity, particles);
	}
	
	@SideOnly(Side.CLIENT)
	public boolean addHitEffects(RayTraceResult result, ParticleManager manager)
	{
		return this.getMultipart().addHitEffects(this, result, manager);
	}
	
	@SideOnly(Side.CLIENT)
	public boolean addDestroyEffects(ParticleManager manager)
	{
		return this.getMultipart().addDestroyEffects(this, manager);
	}
	
	public SoundType getSoundType()
	{
		return this.getMultipart().getSoundType(this);
	}
	
	public IBlockState getSourceState()
	{
		return this.getWorld().getBlockState(this.getPos());
	}
	
	public MultipartStateList getInfoList()
	{
		return this.infoList;
	}
	
	public World getWorld()
	{
		return this.getInfoList().getWorld();
	}
	
	public BlockPos getPos()
	{
		return this.getInfoList().getPos();
	}
	
	public UUID getUUID()
	{
		return this.uuid;
	}
	
	public M getMultipart()
	{
		return this.multipart;
	}
	
	public Block getBlock()
	{
		return this.getMultipart().getBlock();
	}
	
	public int getMetadata()
	{
		return this.meta;
	}
	
	public int setMetadata(int meta)
	{
		this.meta = meta;
		return meta;
	}
	
	public IBlockState getState()
	{
		return this.getBlock().getStateFromMeta(this.getMetadata());
	}
	
	public IBlockState setState(IBlockState state)
	{
		if(state.getBlock() == this.getBlock())
			this.setMetadata(state.getBlock().getMetaFromState(state));
		return this.getState();
	}
	
	public TileEntity getTileEntity()
	{
		return this.entity;
	}
	
	public boolean hasTileEntity()
	{
		return this.getTileEntity() != null;
	}
	
	public boolean canRenderInLayer(BlockRenderLayer layer)
	{
		return this.getMultipart().canRenderInLayer(this, layer);
	}
	
	@Override
	public NBTTagCompound serializeNBT()
	{
		try
		{
			NBTTagCompound compound = new NBTTagCompound();
			compound.setString("multipart", this.getMultipart().getRegistryName().toString());
			compound.setUniqueId("uuid", this.getUUID());
			compound.setTag("nbt", this.hasTileEntity() ? this.getTileEntity().writeToNBT(new NBTTagCompound()) : new NBTTagCompound());
			compound.setByte("metadata", (byte)this.getMetadata());
			return compound;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return new NBTTagCompound();
		}
	}
	
	public static MultipartState readFromNBT(MultipartStateList infoList, NBTTagCompound compound)
	{
		IForgeRegistry<Multipart> multipartRegistry = GameRegistry.findRegistry(Multipart.class);
		if(multipartRegistry != null && compound.hasUniqueId("uuid"))
		{
			UUID uuid = compound.getUniqueId("uuid");
			Multipart multipart = multipartRegistry.getValue(new ResourceLocation(compound.getString("multipart")));
			if(multipart != null)
			{
				int meta = compound.getInteger("metadata");
				NBTTagCompound nbt = compound.getCompoundTag("nbt");
				if(nbt.hasKey("id"))
					return new MultipartState(uuid, infoList, multipart, meta, TileEntity.func_190200_a(infoList.getWorld(), nbt));
				else
					return new MultipartState(uuid, infoList, multipart, meta);
			}
		}
		return null;
	}
	
	@Override
	public String toString()
	{
		return "MultipartData:{source=" + this.getInfoList() + ", multipart=" + this.getMultipart() + ", uuid=" + this.getUUID() + ", meta=" + this.getMetadata() + ", tile=" + this.getTileEntity() + "}";
	}
	
	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
	}
	
	public int getIndex()
	{
		return this.getInfoList().indexOf(this);
	}
}