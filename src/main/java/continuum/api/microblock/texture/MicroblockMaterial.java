package continuum.api.microblock.texture;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;

public class MicroblockMaterial implements IForgeRegistryEntry<MicroblockMaterial>
{
	public static final MicroblockMaterial defaultTexture = new MicroblockMaterial("air", Blocks.AIR, "missingno");
	private final IBlockState baseState;
	private final ItemStack baseStack;
	private ResourceLocation particle = new ResourceLocation("missingno");
	private final ResourceLocation[] textures = new ResourceLocation[6];
	private final ResourceLocation name;
	
	public MicroblockMaterial(String name, Block block, String location)
	{
		this(name, block, 0, location);
	}
	
	public MicroblockMaterial(String name, Block block, int meta, String location)
	{
		this(name, block.getStateFromMeta(meta), new ItemStack(block, 1, meta), location);
	}
	
	public MicroblockMaterial(String name, IBlockState state, ItemStack stack, String location)
	{
		this.name = new ResourceLocation(name);
		this.baseState = state;
		this.baseStack = stack;
		ResourceLocation texture = new ResourceLocation(location);
		this.particle = texture;
		for(EnumFacing direction : EnumFacing.values())
			this.textures[direction.ordinal()] = texture;
	}
	
	public MicroblockMaterial(String name, Block block, String... locations)
	{
		this(name, block, 0, locations);
	}
	
	public MicroblockMaterial(String name, Block block, Integer meta, String... locations)
	{
		this(name, block.getStateFromMeta(meta), new ItemStack(block, 1, meta), locations);
	}
	
	public MicroblockMaterial(String name, IBlockState state, ItemStack stack, String... locations)
	{
		this.name = new ResourceLocation(name);
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
	
	public final MicroblockMaterial setRegistryName(ResourceLocation name)
	{
		return this;
	}
	
	public final ResourceLocation getRegistryName()
	{
		return this.name;
	}
	
	@Override
	public final Class<? super MicroblockMaterial> getRegistryType()
	{
		return MicroblockMaterial.class;
	}
	
	public static MicroblockMaterial readFromNBT(NBTTagCompound compound)
	{
		return compound != null && MicroblockMaterialApi.apiActive() ? MicroblockMaterialApi.getMicroblockMaterialRegistry().getObject(new ResourceLocation(compound.getCompoundTag("BlockEntityTag").getString("entry"))) : MicroblockMaterial.defaultTexture;
	}
	
	public static NBTTagCompound writeToNBT(MicroblockMaterial entry)
	{
		NBTTagCompound compound = new NBTTagCompound();
		NBTTagCompound compound1 = new NBTTagCompound();
		compound1.setString("entry", entry.getRegistryName().toString());
		compound.setTag("BlockEntityTag", compound1);
		return compound;
	}
}
