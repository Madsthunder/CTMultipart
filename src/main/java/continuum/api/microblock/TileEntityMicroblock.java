package continuum.api.microblock;

import java.util.HashMap;

import continuum.api.microblock.texture.MicroblockTextureApi;
import continuum.api.microblock.texture.MicroblockTextureEntry;
import continuum.essentials.tileentity.TileEntitySyncable;
import continuum.multipart.plugins.MultipartAPI_Variables;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class TileEntityMicroblock extends TileEntitySyncable
{
	private MicroblockTextureEntry entry = MicroblockTextureEntry.defaultTexture;
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
		this.setEntry(new ResourceLocation(compound.getString("entry")));
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
	
	public void setEntry(ResourceLocation name)
	{
		if(MicroblockTextureApi.apiActive())
			this.entry = MicroblockTextureApi.getMicroblockTextureRegistry().getObject(name);
		else
			this.entry = MicroblockTextureEntry.defaultTexture;
	}
}
