package continuum.api.multipart;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import continuum.api.multipart.MultipartEvent.AABBExceptionsEvent;
import continuum.essentials.hooks.NBTHooks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MultipartStateList extends ArrayList<MultipartState> implements ICapabilitySerializable<NBTTagList>
{
	@CapabilityInject(MultipartStateList.class)
	public static Capability<MultipartStateList> MULTIPARTINFOLIST;
	private final TileEntity entity;
	
	public MultipartStateList(TileEntity entity)
	{
		this.entity = entity;
	}
	
	@Override
	public NBTTagList serializeNBT()
	{
		NBTTagList list = new NBTTagList();
		for(MultipartState info : this)
			list.appendTag(info.serializeNBT());
		return list;
	}
	
	@Override
	public void deserializeNBT(NBTTagList list)
	{
		this.clear();
		for(NBTTagCompound compound : NBTHooks.increment(NBTTagCompound.class, list))
			this.add(MultipartState.readFromNBT(null, compound));
	}
	
	public World getWorld()
	{
		return this.entity.getWorld();
	}
	
	public BlockPos getPos()
	{
		return this.entity.getPos();
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return capability == MULTIPARTINFOLIST;
	}
	
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		return (T)this;
	}
	
	@Override
	public boolean add(MultipartState info)
	{
		if(super.add(info))
		{
			if(info.hasTileEntity())
			{
				info.getTileEntity().setWorldObj(this.getWorld());
				info.getTileEntity().setPos(this.getPos());
			}
			return true;
		}
		else
			return false;
	}
	
	@Override
	public boolean remove(Object obj)
	{
		boolean removed = super.remove(obj);
		this.handleRemoval();
		return removed;
	}
	
	@Override
	public MultipartState remove(int index)
	{
		MultipartState info = super.remove(index);
		this.handleRemoval();
		return info;
	}
	
	public void handleRemoval()
	{
		World world = this.getWorld();
		BlockPos pos = this.getPos();
		if(this.size() == 1)
		{
			MultipartState data1 = this.get(0);
			world.removeTileEntity(pos);
			NBTTagCompound compound = new NBTTagCompound();
			if(data1.hasTileEntity())
				data1.getTileEntity().writeToNBT(compound);
			world.setBlockState(pos, data1.getState());
			world.setTileEntity(pos, data1.getTileEntity());
			TileEntity entity = world.getTileEntity(pos);
			if(entity != null)
				entity.readFromNBT(compound);
		}
		else if(this.isEmpty())
			world.setBlockToAir(pos);
		world.checkLight(pos);
	}
	
	public int getLightValue()
	{
		int light = 0;
		int i;
		for(MultipartState info : this)
			if((i = info.getLightValue()) > light)
				light = i;
		return light;
	}
	
	public boolean boxIntersectsList(Multipart exclude, AxisAlignedBB box, boolean useExclude, boolean useExceptions)
	{
		if(useExceptions)
		{
			AABBExceptionsEvent event = new AABBExceptionsEvent(this, Lists.newArrayList(), exclude, box);
			MinecraftForge.EVENT_BUS.post(event);
			return this.boxIntersectsMultipart(exclude, box, useExclude, event.allowed);
		}
		for(Multipart multipart : this.getStoredMultiparts())
			if(!useExclude || multipart != exclude)
				for(AxisAlignedBB aabb : this.getCollisionBoxes(multipart))
					if((aabb instanceof PermanentAABB ? ((PermanentAABB)aabb).permanent : true) && box.intersectsWith(aabb))
						return true;
		return false;
	}
	
	public boolean boxIntersectsMultipart(Multipart exclude, AxisAlignedBB box, boolean useExclude, List<AxisAlignedBB> allowed)
	{
		for(Multipart multipart : this.getStoredMultiparts())
			if(!useExclude || multipart != exclude)
				for(AxisAlignedBB aabb : this.getCollisionBoxes(multipart))
					if(!allowed.contains(aabb) && (aabb instanceof PermanentAABB ? ((PermanentAABB)aabb).permanent : true) && box.intersectsWith(aabb))
						return true;
		return false;
	}
	
	public List<AxisAlignedBB> getCollisionBoxes(Multipart multipart)
	{
		List<AxisAlignedBB> boxes = Lists.newArrayList();
		for(MultipartState info : this.findInfoForMultipart(multipart))
			boxes.addAll(info.getMultipart().getCollisionBoxes(info));
		return boxes;
	}
	
	@SideOnly(Side.CLIENT)
	public HashSet<MultipartState> getInfoToRenderInLayer(BlockRenderLayer layer)
	{
		HashSet<MultipartState> infoList = Sets.newHashSet();
		for(MultipartState info : this)
			if(info.canRenderInLayer(layer))
				infoList.add(info);
		return infoList;
	}
	
	public HashSet<MultipartState> getAllInfoOfBlockInstance(Class clasz)
	{
		HashSet<MultipartState> infoList = Sets.newHashSet();
		for(MultipartState info : this)
			if(clasz.isInstance(info.getBlock()))
				infoList.add(info);
		return infoList;
	}
	
	public HashSet<MultipartState> getAllInfoOfTileEntityInstance(Class clasz)
	{
		HashSet<MultipartState> infoList = Sets.newHashSet();
		for(MultipartState info : this)
			if(clasz.isInstance(info.getTileEntity()))
				infoList.add(info);
		return infoList;
	}
	
	public <V extends Multipart> HashSet<MultipartState<V>> getAllInfoOfMultipartInstance(Class<V> clasz)
	{
		HashSet<MultipartState<V>> infoList = Sets.newHashSet();
		for(MultipartState info : this)
			if(clasz.isInstance(info.getMultipart()))
				infoList.add(info);
		return infoList;
	}
	
	public MultipartState findInfoForEntity(TileEntity entity)
	{
		for(MultipartState info : this)
			if(info.getTileEntity() == entity)
				return info;
		return null;
	}
	
	public Multipart findMultipartForEntity(TileEntity entity)
	{
		MultipartState info = this.findInfoForEntity(entity);
		if(info != null)
			return info.getMultipart();
		return null;
	}
	
	public List<MultipartState> findInfoForMultipart(Multipart multipart)
	{
		List<MultipartState> infoList = Lists.newArrayList();
		for(MultipartState info : this)
			if(info.getMultipart() == multipart)
				infoList.add(info);
		return infoList;
	}
	
	public List<Multipart> getStoredMultiparts()
	{
		List<Multipart> multiparts = Lists.newArrayList();
		Multipart multipart;
		for(MultipartState info : this)
			multiparts.add(info.getMultipart());
		return multiparts;
	}
	
	public void setWorld(World world)
	{
		for(MultipartState info : this)
			if(info.hasTileEntity())
				info.getTileEntity().setWorldObj(world);
	}
	
	public void setPos(BlockPos pos)
	{
		for(MultipartState info : this)
			if(info.hasTileEntity())
				info.getTileEntity().setPos(pos);
	}
}
