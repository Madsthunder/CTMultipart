package continuum.api.multipart.registry;

import com.google.common.reflect.TypeToken;

import continuum.api.multipart.CTMultipart_API;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLContainer;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.InjectedModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;

public class MicroblockTextureEntry implements IForgeRegistryEntry<MicroblockTextureEntry>
{
	private final IBlockState baseState;
	private final ItemStack baseStack;
	private ResourceLocation particle = new ResourceLocation("missingno");
	private final ResourceLocation[] textures = new ResourceLocation[6];
	private TypeToken<MicroblockTextureEntry> token = new TypeToken<MicroblockTextureEntry>(getClass())
	{
	};
	private ResourceLocation registryName = null;
	
	public MicroblockTextureEntry(String name, Block block, String location)
	{
		this(name, block, 0, location);
	}
	
	public MicroblockTextureEntry(String name, Block block, Integer meta, String location)
	{
		this(name, block.getStateFromMeta(meta), new ItemStack(block, 1, meta), location);
	}
	
	public MicroblockTextureEntry(String name, IBlockState state, ItemStack stack, String location)
	{
		this.setRegistryName(name);
		this.baseState = state;
		this.baseStack = stack;
		ResourceLocation texture = new ResourceLocation(location);
		this.particle = texture;
		for(EnumFacing direction : EnumFacing.values())
			this.textures[direction.ordinal()] = texture;
	}
	
	public MicroblockTextureEntry(String name, Block block, String... locations)
	{
		this(name, block, 0, locations);
	}
	
	public MicroblockTextureEntry(String name, Block block, Integer meta, String... locations)
	{
		this(name, block.getStateFromMeta(meta), new ItemStack(block, 1, meta), locations);
	}
	
	public MicroblockTextureEntry(String name, IBlockState state, ItemStack stack, String... locations)
	{
		this.setRegistryName(name);
		this.baseState = state;
		this.baseStack = stack;
		if(locations.length >= 1)
		{
			this.particle = new ResourceLocation(locations[0]);
			for(Integer i = 1; i < 7; i++)
				this.textures[i - 1] = new ResourceLocation(locations[i >= locations.length || locations[i] == null ? 0 : i]);
		}
	}
	
	public ResourceLocation getParticleLocation()
	{
		return this.particle;
	}
	
	public ResourceLocation getLocationFromDirection(EnumFacing direction)
	{
		return this.getLocationFromIndex(direction.ordinal());
	}
	
	public ResourceLocation getLocationFromIndex(Integer index)
	{
		return this.textures[index];
	}
	
	public SoundType getSound()
	{
		return this.getBaseBlock().getSoundType();
	}
	
	public Integer getLight()
	{
		return this.getBaseState().getLightValue();
	}
	
	public String getTool()
	{
		return this.getBaseBlock().getHarvestTool(this.getBaseState());
	}
	
	public Integer getHarvestLevel()
	{
		return this.getBaseBlock().getHarvestLevel(this.getBaseState());
	}
	
	public String getDisplayName()
	{
		return this.getBaseStack().getDisplayName();
	}
	
	public Block getBaseBlock()
	{
		return this.getBaseState().getBlock();
	}
	
	public IBlockState getBaseState()
	{
		return this.baseState;
	}
	
	public Item getBaseItem()
	{
		return this.getBaseStack().getItem();
	}
	
	public ItemStack getBaseStack()
	{
		return this.baseStack;
	}
	
	@Override
	public String toString()
	{
		return "MicroblockEntry {" + getRegistryName().toString() + "}";
	}
	
	public final MicroblockTextureEntry setRegistryName(String name)
	{
		if(getRegistryName() != null) throw new IllegalStateException("Attempted to set registry name with existing registry name! New: " + name + " Old: " + getRegistryName());
		int index = name.lastIndexOf(':');
		String oldPrefix = index == -1 ? "" : name.substring(0, index);
		name = index == -1 ? name : name.substring(index + 1);
		ModContainer mc = Loader.instance().activeModContainer();
		String prefix = mc == null || (mc instanceof InjectedModContainer && ((InjectedModContainer)mc).wrappedContainer instanceof FMLContainer) ? "minecraft" : mc.getModId().toLowerCase();
		if(!oldPrefix.equals(prefix) && oldPrefix.length() > 0)
		{
			FMLLog.bigWarning("Dangerous alternative prefix `%s` for name `%s`, expected `%s` invalid registry invocation/invalid name?", oldPrefix, name, prefix);
			prefix = oldPrefix;
		}
		this.registryName = new ResourceLocation(prefix, name);
		return this;
	}
	
	public final MicroblockTextureEntry setRegistryName(ResourceLocation name)
	{
		return setRegistryName(name.toString());
	}
	
	private final MicroblockTextureEntry setRegistryName(String modID, String name)
	{
		return setRegistryName(modID + ":" + name);
	}
	
	public final ResourceLocation getRegistryName()
	{
		return this.registryName;
	}
	
	@Override
	public final Class<? super MicroblockTextureEntry> getRegistryType()
	{
		return MicroblockTextureEntry.class;
	}
	
	public static MicroblockTextureEntry readFromNBT(NBTTagCompound compound)
	{
		return CTMultipart_API.microblockTextureRegistry.getObject(new ResourceLocation(compound.getCompoundTag("BlockEntityTag").getString("entry")));
	}
	
	public static NBTTagCompound writeToNBT(MicroblockTextureEntry entry)
	{
		NBTTagCompound compound = new NBTTagCompound();
		NBTTagCompound compound1 = new NBTTagCompound();
		compound1.setString("entry", entry.getRegistryName().toString());
		compound.setTag("BlockEntityTag", compound1);
		return compound;
	}
}
