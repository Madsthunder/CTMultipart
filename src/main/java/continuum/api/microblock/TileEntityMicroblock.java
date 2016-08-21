package continuum.api.microblock;

import java.util.HashMap;

import continuum.api.multipart.MicroblockTextureEntry;
import continuum.api.multipart.MultipartAPI;
import continuum.essentials.tileentity.TileEntitySyncable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class TileEntityMicroblock extends TileEntitySyncable
{
	private MicroblockTextureEntry entry = MultipartAPI.microblockTextureRegistry.getDefaultValue();
	public static final HashMap<BlockPos, MicroblockTextureEntry> cache = new HashMap<BlockPos, MicroblockTextureEntry>();
	
	public TileEntityMicroblock()
	{
		super(true);
	}
	
	public TileEntityMicroblock(MicroblockTextureEntry entry)
	{
		this.entry = entry;
	}
	
	@Override
	public NBTTagCompound writeItemsToNBT()
	{
		NBTTagCompound compound = new NBTTagCompound();
		if(entry != null) compound.setString("entry", this.entry.getRegistryName().toString());
		return compound;
	}
	
	@Override
	public void readItemsFromNBT(NBTTagCompound compound)
	{
		this.setEntry(MultipartAPI.microblockTextureRegistry.getObject(new ResourceLocation(compound.getString("entry"))));
	}
	
	public void onDataPacket(NetworkManager manager, SPacketUpdateTileEntity packet)
	{
		if(this.shouldSyncTags)
		{
			super.onDataPacket(manager, packet);
			if(this.worldObj != null && this.worldObj.isRemote && this.pos != null) this.worldObj.markBlockRangeForRenderUpdate(this.pos, this.pos);
		}
	}
	
	public MicroblockTextureEntry getEntry()
	{
		return this.entry;
	}
	
	public void setEntry(MicroblockTextureEntry entry)
	{
		this.entry = entry;
	}
}
