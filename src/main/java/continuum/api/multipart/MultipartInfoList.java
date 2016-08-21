package continuum.api.multipart;

import java.util.List;
import java.util.concurrent.Callable;

import com.google.common.collect.Lists;

import continuum.essentials.hooks.NBTHooks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;

public class MultipartInfoList implements INBTSerializable<NBTTagList>
{
    @CapabilityInject(MultipartInfoList.class)
	public static Capability<MultipartInfoList> MULTIPARTINFO_LIST_CAPABILITY;
    
	private final List<MultipartInfo> info = Lists.newArrayList();
	
	@Override
	public NBTTagList serializeNBT()
	{
		return NBTHooks.compileList(NBTTagCompound.class, this.info);
	}
	
	@Override
	public void deserializeNBT(NBTTagList list)
	{
		this.info.clear();
		for(NBTTagCompound compound : NBTHooks.increment(NBTTagCompound.class, list))
			this.info.add(MultipartInfo.readFromNBT(null, compound));
	}
}
