package continuum.api.microblock;

import java.util.HashMap;

import continuum.api.microblock.texture.MicroblockMaterial;
import continuum.essentials.tileentity.TileEntitySyncable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry;

public class TileEntityMicroblock extends TileEntitySyncable
{
	private MicroblockMaterial material = MicroblockMaterial.defaultMaterial;
	
	public TileEntityMicroblock()
	{
		
	}
	
	public TileEntityMicroblock(MicroblockMaterial material)
	{
		this.material = material;
	}
	
	@Override
	public NBTTagCompound writeItemsToNBT()
	{
		NBTTagCompound compound = new NBTTagCompound();
		if(material != null) compound.setString("material", this.material.getRegistryName().toString());
		return compound;
	}
	
	@Override
	public void readItemsFromNBT(NBTTagCompound compound)
	{
		this.setMaterial(new ResourceLocation(compound.getString("material")));
	}
	
	public void onDataPacket(NetworkManager manager, SPacketUpdateTileEntity packet)
	{
		if(this.syncTags)
		{
			super.onDataPacket(manager, packet);
			if(this.worldObj != null && this.worldObj.isRemote && this.pos != null) this.worldObj.markBlockRangeForRenderUpdate(this.pos, this.pos);
		}
	}
	
	public MicroblockMaterial getMaterial()
	{
		return this.material;
	}
	
	public void setMaterial(ResourceLocation name)
	{
		IForgeRegistry<MicroblockMaterial> microblockMaterialRegistry = GameRegistry.findRegistry(MicroblockMaterial.class);
		MicroblockMaterial material = microblockMaterialRegistry == null ? MicroblockMaterial.defaultMaterial : microblockMaterialRegistry.getValue(name);
		this.material = material == null ? MicroblockMaterial.defaultMaterial : material;
	}
}
