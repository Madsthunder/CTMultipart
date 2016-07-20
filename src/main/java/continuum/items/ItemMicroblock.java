package continuum.multipart.items;

import java.util.List;

import continuum.api.multipart.CTMultipart_API;
import continuum.api.multipart.implementations.IMicroblock;
import continuum.essentials.mod.CTMod;
import continuum.multipart.blocks.BlockLayered;
import continuum.multipart.enums.EnumMicroblockType;
import continuum.multipart.mod.Multipart_EH;
import continuum.multipart.mod.Multipart_OH;
import continuum.multipart.registry.MicroblockTextureEntry;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.translation.I18n;

public class ItemMicroblock extends ItemBlock
{
	public CTMod<Multipart_OH, Multipart_EH> mod;
	
	public ItemMicroblock(CTMod<Multipart_OH, Multipart_EH> mod, EnumMicroblockType type)
	{
		super((Block)type.getBlock(mod));
		this.mod = mod;
		this.setRegistryName(this.block.getRegistryName());
		this.setUnlocalizedName(this.block.getUnlocalizedName());
	}
	
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list)
	{
		MicroblockTextureEntry defaultEntry = CTMultipart_API.microblockTextureRegistry.getDefaultValue();
		for(MicroblockTextureEntry entry : CTMultipart_API.microblockTextureRegistry)
			if(entry != defaultEntry)
			{
				ItemStack stack = new ItemStack(item);
				stack.setTagCompound(MicroblockTextureEntry.writeToNBT(entry));
				list.add(stack);
			}
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack stack)
	{
		MicroblockTextureEntry entry = MicroblockTextureEntry.readFromNBT(stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound());
		return entry == CTMultipart_API.microblockTextureRegistry.getDefaultValue() ? "Default" : entry.getDisplayName() + " " + I18n.translateToLocal("microblock." + ((IMicroblock)this.block).getType().getName().toLowerCase());
	}
	
	public IBlockState getRenderState()
	{
		Block block = this.block;
		IBlockState defaultt = block.getDefaultState();
		if(block instanceof BlockLayered) return defaultt.withProperty(BlockLayered.direction, EnumFacing.SOUTH);
		return defaultt;
	}
}
