package continuum.multipart.items;

import net.minecraft.item.Item;

public class ItemSaw extends Item
{
	public ItemSaw(ToolMaterial material)
	{
		this.setMaxDamage(material.getMaxUses());
		this.setHarvestLevel("microblockSaw", material.getHarvestLevel());
	}
}
