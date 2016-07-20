package continuum.api.multipart.capabilities;

import java.util.List;

import com.google.common.collect.Lists;

import continuum.api.multipart.MultipartInfo;
import continuum.essentials.helpers.NBTHelper;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class MulltiblockCapability implements ICapabilitySerializable<NBTTagList>
{
	private final List<MultipartInfo> info = Lists.newArrayList();
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return false;
	}
	
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public NBTTagList serializeNBT()
	{
		return NBTHelper.compileList(NBTTagCompound.class, this.info);
	}
	
	@Override
	public void deserializeNBT(NBTTagList list)
	{
		this.info.clear();
		for(NBTTagCompound compound : NBTHelper.increment(NBTTagCompound.class, list))
			this.info.add(MultipartInfo.readFromNBT(null, compound));
	}
}
