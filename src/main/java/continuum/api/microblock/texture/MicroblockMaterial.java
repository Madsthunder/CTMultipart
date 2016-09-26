package continuum.api.microblock.texture;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class MicroblockMaterial implements IForgeRegistryEntry<MicroblockMaterial>
{
	public static final MicroblockMaterial defaultMaterial = new MicroblockMaterial.All("air", Blocks.AIR, "missingno");
	private final ResourceLocation name;
	private final IBlockState baseState;
	private final ItemStack baseStack;
	
	public MicroblockMaterial(String name, Block block)
	{
		this(name, block, 0);
	}
	
	public MicroblockMaterial(String name, Block block, int meta)
	{
		this(name, block, meta, Item.getItemFromBlock(block), meta);
	}
	
	public MicroblockMaterial(String name, Block block, int blockMeta, Item item)
	{
		this(name, block, blockMeta, item, 0);
	}
	
	public MicroblockMaterial(String name, Block block, int blockMeta, Item item, int itemMeta)
	{
		this(name, block.getStateFromMeta(blockMeta), new ItemStack(item, 1, itemMeta));
	}
	
	public MicroblockMaterial(String name, IBlockState state, ItemStack stack)
	{
		this.name = new ResourceLocation(name);
		this.baseState = state;
		this.baseStack = stack;
	}
	
	public abstract ResourceLocation getParticleTexture();
	
	public abstract ResourceLocation getTexture(EnumFacing direction);
	
	public List<ResourceLocation> getAllTextures()
	{
		List<ResourceLocation> textures = Lists.newArrayList();
		for(EnumFacing direction : EnumFacing.values())
			textures.add(this.getTexture(direction));
		return textures;
	}
	
	public SoundType getSound()
	{
		return this.getBlockState().getBlock().getSoundType();
	}
	
	public int getLight()
	{
		return this.getBlockState().getLightValue();
	}
	
	public String getTool()
	{
		return this.getBlockState().getBlock().getHarvestTool(this.getBlockState());
	}
	
	public int getHarvestLevel()
	{
		return this.getBlockState().getBlock().getHarvestLevel(this.getBlockState());
	}
	
	public String getDisplayName()
	{
		return this.getItemStack().getDisplayName();
	}
	
	public IBlockState getBlockState()
	{
		return this.baseState;
	}
	
	public Item getBaseItem()
	{
		return this.getItemStack().getItem();
	}
	
	public ItemStack getItemStack()
	{
		return this.baseStack;
	}
	
	public boolean hasColorMultiplier()
	{
		return false;
	}
	
	public int getColorMultiplier(IBlockAccess access, BlockPos pos, int tintIndex)
	{
		return 0;
	}
	
	@SideOnly(Side.CLIENT)
	public boolean canRenderInLayer(BlockRenderLayer layer)
	{
		return this.getBlockState().getBlock().canRenderInLayer(this.getBlockState(), layer);
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
		IForgeRegistry<MicroblockMaterial> microblockMaterialRegistry = GameRegistry.findRegistry(MicroblockMaterial.class);
		MicroblockMaterial material = compound != null && microblockMaterialRegistry != null ? microblockMaterialRegistry.getValue(new ResourceLocation(compound.getCompoundTag("BlockEntityTag").getString("material"))) : null;
		return  material == null ? MicroblockMaterial.defaultMaterial : material;
	}
	
	public static NBTTagCompound writeToNBT(MicroblockMaterial material)
	{
		NBTTagCompound compound = new NBTTagCompound();
		NBTTagCompound compound1 = new NBTTagCompound();
		compound1.setString("material", material.getRegistryName().toString());
		compound.setTag("BlockEntityTag", compound1);
		return compound;
	}
	
	public static class All extends MicroblockMaterial
	{
		private final ResourceLocation location;
		
		public All(String name, Block block, String location)
		{
			this(name, block, 0, location);
		}
		
		public All(String name, Block block, int meta, String location)
		{
			super(name, block, meta);
			this.location = new ResourceLocation(location);
		}

		@Override
		public ResourceLocation getParticleTexture()
		{
			return this.location;
		}

		@Override
		public ResourceLocation getTexture(EnumFacing direction)
		{
			return this.location;
		}
	}
	
	public static class TopBottom extends MicroblockMaterial
	{
		private final ResourceLocation top;
		private final ResourceLocation bottom;
		private final ResourceLocation sides;
		
		public TopBottom(String name, Block block, String top, String bottom, String sides)
		{
			this(name, block, 0, top, bottom, sides);
		}
		
		public TopBottom(String name, Block block, int meta, String top, String bottom, String sides)
		{
			super(name, block, meta);
			this.top = new ResourceLocation(top);
			this.bottom = new ResourceLocation(bottom);
			this.sides = new ResourceLocation(sides);
		}

		@Override
		public ResourceLocation getParticleTexture()
		{
			return this.sides;
		}

		@Override
		public ResourceLocation getTexture(EnumFacing direction)
		{
			switch(direction)
			{
				case DOWN : return this.bottom;
				case UP : return this.top;
				default : return this.sides;
			}
		}
	}
	
	public static class Pillar extends MicroblockMaterial
	{
		private final ResourceLocation pillar;
		private final ResourceLocation sides;
		
		public Pillar(String name, Block block, String pillar, String sides)
		{
			this(name, block, 0, pillar, sides);
		}
		
		public Pillar(String name, Block block, int meta, String pillar, String sides)
		{
			super(name, block, meta);
			this.pillar = new ResourceLocation(pillar);
			this.sides = new ResourceLocation(sides);
		}

		@Override
		public ResourceLocation getParticleTexture()
		{
			return this.sides;
		}

		@Override
		public ResourceLocation getTexture(EnumFacing direction)
		{
			switch(direction.getAxis())
			{
				case Y : return this.pillar;
				default : return this.sides;
			}
		}
	}
	
	public static class Orientable extends Pillar
	{
		private final ResourceLocation front;
		
		public Orientable(String name, Block block, String pillar, String front, String sides)
		{
			super(name, block, 0, pillar, sides);
			this.front = new ResourceLocation(sides);
		}
		
		public Orientable(String name, Block block, int meta, String pillar, String front, String sides)
		{
			super(name, block, meta, pillar, sides);
			this.front = new ResourceLocation(sides);
		}
		
		@Override
		public ResourceLocation getParticleTexture()
		{
			return this.front;
		}
		
		@Override
		public ResourceLocation getTexture(EnumFacing direction)
		{
			switch(direction)
			{
				case NORTH : return this.front;
				default : return super.getTexture(direction);
			}
		}
	}
}
