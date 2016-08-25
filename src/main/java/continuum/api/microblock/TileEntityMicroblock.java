package continuum.api.microblock;

import java.util.HashMap;

import continuum.api.microblock.texture.MicroblockMaterial;
import continuum.api.microblock.texture.MicroblockMaterialApi;
import continuum.essentials.tileentity.TileEntitySyncable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class TileEntityMicroblock extends TileEntitySyncable
{
	private MicroblockMaterial entry = MicroblockMaterial.defaultTexture;
	public static final HashMap<BlockPos, MicroblockMaterial> cache = new HashMap<BlockPos, MicroblockMaterial>();
	
	public TileEntityMicroblock()
	{
		super(true);
	}
	
	public TileEntityMicroblock(MicroblockMaterial entry)
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
	
	public MicroblockMaterial getEntry()
	{
		return this.entry;
	}
	
	public void setEntry(ResourceLocation name)
	{
		if(MicroblockMaterialApi.apiActive())
			this.entry = MicroblockMaterialApi.getMicroblockMaterialRegistry().getObject(name);
		else
			this.entry = MicroblockMaterial.defaultTexture;
	}
}
