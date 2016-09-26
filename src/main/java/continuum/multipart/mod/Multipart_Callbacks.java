package continuum.multipart.mod;

import java.util.Map;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import continuum.api.microblock.Microblock;
import continuum.api.microblock.compat.MultipartMicroblock;
import continuum.api.multipart.Multipart;
import continuum.multipart.items.ItemMicroblock;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry.AddCallback;
import net.minecraftforge.fml.common.registry.IForgeRegistry.CreateCallback;

public class Multipart_Callbacks 
{
	private static final Multipart_OH objectHolder = Multipart_OH.getObjectHolder();
	public static final Object MULTIPARTS = new Multiparts();
	public static final Object MICROBLOCKS = new Microblocks();
	public static final ResourceLocation BLOCK_TO_MULTIPART = new ResourceLocation("ctmultipart", "blocktomultipart");
	public static final ResourceLocation ITEM_TO_MULTIPART = new ResourceLocation("ctmultipart", "itemtomultipart");
	public static final ResourceLocation MICROBLOCKOVERLAPS = new ResourceLocation("ctmultipart", "microblockoverlaps");
	
	private static class Multiparts implements AddCallback<Multipart>, CreateCallback<Multipart>
	{		
		@Override
		public void onCreate(Map<ResourceLocation, ?> slaveset, BiMap<ResourceLocation, ? extends IForgeRegistry<?>> registries)
		{
			Map<ResourceLocation, Object> slaves = (Map<ResourceLocation, Object>)slaveset;
			slaves.put(BLOCK_TO_MULTIPART, HashBiMap.<Block, Multipart>create());
			slaves.put(ITEM_TO_MULTIPART, HashBiMap.<Item, Multipart>create());
		}

		@Override
		public void onAdd(Multipart multipart, int id, Map<ResourceLocation, ?> slaveset)
		{
			((BiMap<Block, Multipart>)slaveset.get(BLOCK_TO_MULTIPART)).put(multipart.getBlock(), multipart);
			((BiMap<Item, Multipart>)slaveset.get(ITEM_TO_MULTIPART)).put(multipart.getItem(), multipart);
		}
	}
	
	private static class Microblocks implements CreateCallback<Microblock>, AddCallback<Microblock>
	{
		@Override
		public void onCreate(Map<ResourceLocation, ?> slaveset, BiMap<ResourceLocation, ? extends IForgeRegistry<?>> registries)
		{
		}
		
		@Override
		public void onAdd(Microblock microblock, int id, Map<ResourceLocation, ?> slaveset)
		{
			IForgeRegistry<Block> blocks = GameRegistry.findRegistry(Block.class);
			IForgeRegistry<Item> items = GameRegistry.findRegistry(Item.class);
			IForgeRegistry<Multipart> multiparts = GameRegistry.findRegistry(Multipart.class);
			if(blocks != null && items != null && multiparts != null)
			{
				Block block = microblock.getBlock();
				if(!blocks.containsKey(block.getRegistryName()))
					blocks.register(block);
				if(!items.containsKey(block.getRegistryName()))
					items.register(new ItemMicroblock(microblock).setUnlocalizedName(microblock.getUnlocalizedName()).setRegistryName(microblock.getBlock().getRegistryName()));
				if(!multiparts.containsKey(block.getRegistryName()))
					multiparts.register(new MultipartMicroblock(microblock));
			}
		}
	}
}
