package continuum.api.multipart;

import java.util.List;
import java.util.UUID;

import continuum.api.multipart.implementations.Multipart;
import continuum.api.multipart.state.MultiblockStateImpl;
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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MultipartInfo implements INBTSerializable<NBTTagCompound>
{
	private final UUID uuid;
	private final TileEntityMultiblock source;
	private final Multipart multipart;
	private Integer meta;
	private TileEntity entity;
	
	public MultipartInfo(UUID uuid, TileEntityMultiblock multiblock, Multipart multipart, IBlockState state)
	{
		this(uuid, multiblock, multipart, state.getBlock().getMetaFromState(state));
	}
	
	public MultipartInfo(UUID uuid, TileEntityMultiblock multiblock, Multipart multipart, Integer meta)
	{
		this(uuid, multiblock, multipart, meta, null);
	}
	
	public MultipartInfo(UUID uuid, TileEntityMultiblock multiblock, Multipart multipart, IBlockState state, TileEntity entity)
	{
		this(uuid, multiblock, multipart, state.getBlock().getMetaFromState(state), entity);
	}
	
	public MultipartInfo(UUID uuid, TileEntityMultiblock sourceTile, Multipart multipart, Integer meta, TileEntity entity)
	{
		this.uuid = uuid;
		this.source = sourceTile;
		this.multipart = multipart;
		this.meta = meta;
		this.entity = entity;
	}
	
	public List<ICuboid> getSelectableBoxes()
	{
		return this.getMultipart().getCuboids(this);
	}
	
	public List<AxisAlignedBB> getCollisonBoxes()
	{
		return this.getMultipart().getCollisionBoxes(this);
	}
	
	public IBlockState getActualState()
	{
		return this.getActualState(false);
	}
	
	public IBlockState getActualState(Boolean addImpl)
	{
		IBlockState state = this.getMultipart().getMultipartState(this);
		if(addImpl && state instanceof StateImplementation) state = new MultiblockStateImpl((StateImplementation)state, this.source, this);
		return state;
	}
	
	public IBlockState getExtendedState()
	{
		return this.getExtendedState(false);
	}
	
	public IBlockState getExtendedState(Boolean addImpl)
	{
		IBlockState state = this.getMultipart().getMultipartRenderState(this);
		if(addImpl && state instanceof StateImplementation) state = new MultiblockStateImpl((StateImplementation)state, this.source, this);
		return state;
	}
	
	public void onPlaced(EntityLivingBase entity, ItemStack stack)
	{
		this.getMultipart().onMultipartPlaced(this, entity, stack);
	}
	
	public Boolean onActivated(EntityPlayer player, EnumHand hand, ItemStack stack, EnumFacing side, float hitX, float hitY, float hitZ)
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
	
	public Integer getLightValue()
	{
		return this.getMultipart().getLightValue(this);
	}
	
	public Boolean isSideSolid(EnumFacing side)
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
	
	public Float getHardness()
	{
		return this.getMultipart().getHardness(this);
	}
	
	public Integer getHarvestLevel()
	{
		return this.getMultipart().getHarvestLevel(this);
	}
	
	public String getTool()
	{
		return this.getMultipart().getTool(this);
	}
	
	public Boolean addLandingEffects(EntityLivingBase entity, Integer particles)
	{
		return this.getMultipart().addLandingEffects(this, entity, particles);
	}
	
	@SideOnly(Side.CLIENT)
	public Boolean addHitEffects(RayTraceResult result, ParticleManager manager)
	{
		return this.getMultipart().addHitEffects(this, result, manager);
	}
	
	@SideOnly(Side.CLIENT)
	public Boolean addDestroyEffects(ParticleManager manager)
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
	
	public TileEntityMultiblock getSourceTile()
	{
		return this.source;
	}
	
	public World getWorld()
	{
		return this.getSourceTile().getWorld();
	}
	
	public BlockPos getPos()
	{
		return this.getSourceTile().getPos();
	}
	
	public UUID getUUID()
	{
		return this.uuid;
	}
	
	public Multipart getMultipart()
	{
		return this.multipart;
	}
	
	public Block getBlock()
	{
		return this.getMultipart().getBlock();
	}
	
	public Integer getMetadata()
	{
		return this.meta;
	}
	
	public Integer setMetadata(Integer meta)
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
		if(state.getBlock() == this.getBlock()) this.setMetadata(state.getBlock().getMetaFromState(state));
		return this.getState();
	}
	
	public TileEntity getTileEntity()
	{
		return this.entity;
	}
	
	public Boolean hasTileEntity()
	{
		return this.getTileEntity() != null;
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
			compound.setByte("metadata", this.getMetadata().byteValue());
			return compound;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return new NBTTagCompound();
		}
	}
	
	public static MultipartInfo readFromNBT(TileEntityMultiblock source, NBTTagCompound compound)
	{
		if(compound.hasUniqueId("uuid"))
		{
			UUID uuid = compound.getUniqueId("uuid");
			Multipart multipart = CTMultipart_API.getMultipart(new ResourceLocation(compound.getString("multipart")));
			if(multipart != CTMultipart_API.getDefaultMultipart())
			{
				Integer meta = compound.getInteger("metadata");
				NBTTagCompound nbt = compound.getCompoundTag("nbt");
				if(nbt.hasKey("id"))
					return new MultipartInfo(uuid, source, multipart, meta, TileEntity.func_190200_a(source.getWorld(), nbt));
				else
					return new MultipartInfo(uuid, source, multipart, meta);
			}
		}
		return null;
	}
	
	@Override
	public String toString()
	{
		return "MultipartData:{source=" + this.getSourceTile() + ", multipart=" + this.getMultipart() + ", uuid=" + this.getUUID() + ", meta=" + this.getMetadata() + ", tile=" + this.getTileEntity() + "}";
	}
	
	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
	}
}