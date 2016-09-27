package continuum.api.microblock.material;

import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry;

public class MicroblockMaterialCapability implements ICapabilitySerializable<NBTTagString>
{
	@CapabilityInject(MicroblockMaterialCapability.class)
	public static Capability<MicroblockMaterialCapability> MICROBLOCKMATERIAL;
	
	private MicroblockMaterial material = MicroblockMaterial.defaultMaterial;
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return capability == MICROBLOCKMATERIAL;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		return (T)this;
	}

	@Override
	public NBTTagString serializeNBT()
	{
		return new NBTTagString(this.getMaterial().getRegistryName().toString());
	}

	@Override
	public void deserializeNBT(NBTTagString nbt)
	{
		IForgeRegistry<MicroblockMaterial> microblockMaterialRegistry = GameRegistry.findRegistry(MicroblockMaterial.class);
		if(microblockMaterialRegistry != null)
			this.setMaterial(microblockMaterialRegistry.getValue(new ResourceLocation(nbt.getString())));
	}
	
	public MicroblockMaterial getMaterial()
	{
		return this.material;
	}
	
	public void setMaterial(MicroblockMaterial material)
	{
		this.material = material == null ? MicroblockMaterial.defaultMaterial : material;
	}
}
