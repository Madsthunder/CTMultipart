package continuum.api.multipart;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;

import continuum.api.multipart.boundingboxes.MultipartAABB;
import continuum.api.multipart.event.MultipartEvent.AABBExceptionsEvent;
import continuum.api.multipart.implementations.Multipart;
import continuum.essentials.helpers.NBTHelper;
import continuum.essentials.tileentity.CTTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class TileEntityMultiblock extends CTTileEntity implements Iterable<MultipartInfo>
{
	private final List<MultipartInfo> info = Lists.newArrayList();
	
	public TileEntityMultiblock()
	{
		super(true);
	}
	
	@Override
	public void setWorldObj(World world)
	{
		super.setWorldObj(world);
		for(MultipartInfo info : this)
			if(info.hasTileEntity()) info.getTileEntity().setWorldObj(world);
	}
	
	public Boolean boxIntersectsMultipart(Multipart exclude, AxisAlignedBB box, Boolean useExclude, Boolean useExceptions)
	{
		if(useExceptions)
		{
			AABBExceptionsEvent event = new AABBExceptionsEvent(this, new ArrayList<AxisAlignedBB>(), exclude, box);
			MinecraftForge.EVENT_BUS.post(event);
			return this.boxIntersectsMultipart(exclude, box, useExclude, event.allowed);
		}
		for(Multipart multipart : this.getStoredMultiparts())
			if(!useExclude || multipart != exclude) for(AxisAlignedBB aabb : this.getCollisionBoxes(multipart))
				if((aabb instanceof MultipartAABB ? ((MultipartAABB)aabb).permanent : true) && box.intersectsWith(aabb)) return true;
		return false;
	}
	
	public Boolean boxIntersectsMultipart(Multipart exclude, AxisAlignedBB box, Boolean useExclude, List<AxisAlignedBB> allowed)
	{
		for(Multipart multipart : this.getStoredMultiparts())
			if(!useExclude || multipart != exclude) for(AxisAlignedBB aabb : this.getCollisionBoxes(multipart))
				if(!allowed.contains(aabb) && (aabb instanceof MultipartAABB ? ((MultipartAABB)aabb).permanent : true) && box.intersectsWith(aabb)) return true;
		return false;
	}
	
	public List<Multipart> getStoredMultiparts()
	{
		List<Multipart> multiparts = Lists.newArrayList();
		Multipart multipart;
		for(MultipartInfo info : this)
			if(!multiparts.contains(multipart = info.getMultipart())) multiparts.add(multipart);
		return multiparts;
	}
	
	public List<AxisAlignedBB> getCollisionBoxes(Multipart multipart)
	{
		List<AxisAlignedBB> boxes = Lists.newArrayList();
		for(MultipartInfo info : this.findInfoForMultipart(multipart))
			boxes.addAll(info.getMultipart().getCollisionBoxes(info));
		return boxes;
	}
	
	public List<MultipartInfo> findInfoForMultipart(Multipart multipart)
	{
		List<MultipartInfo> infoList = Lists.newArrayList();
		for(MultipartInfo info : this)
			if(info.getMultipart() == multipart) infoList.add(info);
		return infoList;
	}
	
	public MultipartInfo findInfoForEntity(TileEntity entity)
	{
		for(MultipartInfo info : this)
			if(info.getTileEntity() == entity) return info;
		return null;
	}
	
	public Multipart findMultipartForEntity(TileEntity entity)
	{
		MultipartInfo info = this.findInfoForEntity(entity);
		if(info != null) return info.getMultipart();
		return null;
	}
	
	@Override
	public NBTTagCompound writeItemsToNBT()
	{
		return NBTHelper.writeList(NBTTagCompound.class, "multiparts", this);
	}
	
	@Override
	public void readItemsFromNBT(NBTTagCompound compound)
	{
		for(NBTTagCompound compound1 : NBTHelper.increment(NBTTagCompound.class, compound.getTagList("multiparts", 10)))
			this.info.add(MultipartInfo.readFromNBT(this, compound1));
	}
	
	public List<MultipartInfo> getAllInfo()
	{
		return Lists.newArrayList(this.info);
	}
	
	public List<MultipartInfo> getAllDataOfBlockInstance(Class<? extends Block> clasz)
	{
		ArrayList<MultipartInfo> infoList = Lists.newArrayList();
		for(MultipartInfo info : this)
			if(clasz.isInstance(info.getBlock())) infoList.add(info);
		return infoList;
	}
	
	public List<MultipartInfo> getAllDataOfTileEntityInstance(Class<? extends TileEntity> clasz)
	{
		ArrayList<MultipartInfo> infoList = Lists.newArrayList();
		for(MultipartInfo info : this)
			if(clasz.isInstance(info.getTileEntity())) infoList.add(info);
		return infoList;
	}
	
	public MultipartInfo addMultipartInfoToList(Multipart multipart, IBlockState state, TileEntity entity)
	{
		if(state != null)
		{
			MultipartInfo info = new MultipartInfo(UUID.randomUUID(), this, multipart, state.getBlock().getMetaFromState(state), entity);
			if(info.hasTileEntity())
			{
				info.getTileEntity().setWorldObj(this.getWorld());
				info.getTileEntity().setPos(this.getPos());
			}
			this.info.add(info);
			return info;
		}
		return null;
	}
	
	private MultipartInfo addMultipartInfoToList(UUID uuid, Multipart multipart, IBlockState state, TileEntity entity)
	{
		MultipartInfo info = new MultipartInfo(uuid, this, multipart, state.getBlock().getMetaFromState(state), entity);
		this.info.add(info);
		return info;
	}
	
	public MultipartInfo removeMultipartFromList(MultipartInfo info)
	{
		this.info.remove(info);
		if(this.info.size() == 1)
		{
			World world = this.getWorld();
			BlockPos pos = this.getPos();
			MultipartInfo data1 = this.info.get(0);
			world.removeTileEntity(pos);
			NBTTagCompound compound = new NBTTagCompound();
			if(data1.hasTileEntity()) data1.getTileEntity().writeToNBT(compound);
			world.setBlockState(pos, data1.getState());
			world.setTileEntity(pos, data1.getTileEntity());
			TileEntity entity = world.getTileEntity(pos);
			if(entity != null) entity.readFromNBT(compound);
		}
		else if(this.info.isEmpty()) this.worldObj.setBlockToAir(this.pos);
		this.worldObj.checkLight(this.pos);
		return info;
	}
	
	public Integer getLight()
	{
		Integer light = 0;
		Integer i;
		for(MultipartInfo info : this)
			if((i = info.getLightValue()) > light) light = i;
		return light;
	}
	
	@Override
	public void onDataPacket(NetworkManager manager, SPacketUpdateTileEntity packet)
	{
		if(this.shouldSyncPackets) this.info.clear();
		super.onDataPacket(manager, packet);
	}
	
	@Override
	public boolean canRenderBreaking()
	{
		return true;
	}
	
	@Override
	public boolean hasFastRenderer()
	{
		return true;
	}
	
	@Override
	public Iterator<MultipartInfo> iterator()
	{
		return this.info.iterator();
	}
}