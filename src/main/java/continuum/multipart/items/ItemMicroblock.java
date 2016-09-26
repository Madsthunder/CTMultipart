package continuum.multipart.items;


import java.util.List;

import continuum.api.microblock.Microblock;
import continuum.api.microblock.texture.MicroblockMaterial;
import continuum.multipart.blocks.BlockLayered;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry;

public class ItemMicroblock extends ItemBlock
{
	private final Microblock microblock;
	
	public ItemMicroblock(Microblock microblock)
	{
		super(microblock.getBlock());
		this.microblock = microblock;
		this.setUnlocalizedName(this.block.getUnlocalizedName());
	}
	
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list)
	{
		IForgeRegistry<MicroblockMaterial> microblockMaterialRegistry = GameRegistry.findRegistry(MicroblockMaterial.class);
		if(microblockMaterialRegistry != null)
		for(MicroblockMaterial material : microblockMaterialRegistry)
			if(material != MicroblockMaterial.defaultMaterial)
			{
				ItemStack stack = new ItemStack(item);
				stack.setTagCompound(MicroblockMaterial.writeToNBT(material));
				list.add(stack);
			}
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack stack)
	{
		MicroblockMaterial entry = MicroblockMaterial.readFromNBT(stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound());
		return entry == MicroblockMaterial.defaultMaterial ? "Default" : entry.getDisplayName() + " " + I18n.translateToLocal("microblock." + this.microblock.getName().toLowerCase());
	}
	
	public IBlockState getRenderState()
	{
		Block block = this.block;
		IBlockState defaultt = block.getDefaultState();
		if(block instanceof BlockLayered) return defaultt.withProperty(BlockLayered.direction, EnumFacing.SOUTH);
		return defaultt;
	}
	
	public Microblock getMicroblock()
	{
		return this.microblock;
	}
}
