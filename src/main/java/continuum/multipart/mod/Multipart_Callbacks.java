package continuum.multipart.mod;

import java.util.Map;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import continuum.api.microblock.Microblock;
import continuum.api.multipart.Multipart;
import continuum.api.multipart.MultipartUtils;
import continuum.multipart.items.ItemMicroblock;
import continuum.multipart.multiparts.MultipartMicroblock;
import continuum.multipart.registry.MicroblockOverlapRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry.AddCallback;
import net.minecraftforge.fml.common.registry.IForgeRegistry.CreateCallback;

public class Multipart_Callbacks
{
	public static final Object MULTIPARTS = new Multiparts();
	public static final Object MICROBLOCKS = new Microblocks();
	public static final ResourceLocation MICROBLOCK_OVERLAPS = new ResourceLocation("ctmultipart", "microblockoverlaps");
	
	private static class Multiparts implements AddCallback<Multipart>, CreateCallback<Multipart>
	{
		@Override
		public void onCreate(Map<ResourceLocation, ?> slaveset, BiMap<ResourceLocation, ? extends IForgeRegistry<?>> registries)
		{
			Map<ResourceLocation, Object> slaves = (Map<ResourceLocation, Object>)slaveset;
			slaves.put(MultipartUtils.BLOCK_TO_MULTIPART, HashBiMap.<Block, Multipart>create());
			slaves.put(MultipartUtils.ITEM_TO_MULTIPART, HashBiMap.<Item, Multipart>create());
		}
		
		@Override
		public void onAdd(Multipart multipart, int id, Map<ResourceLocation, ?> slaveset)
		{
			((BiMap<Block, Multipart>)slaveset.get(MultipartUtils.BLOCK_TO_MULTIPART)).put(multipart.getBlock(), multipart);
			((BiMap<Item, Multipart>)slaveset.get(MultipartUtils.ITEM_TO_MULTIPART)).put(multipart.getItem(), multipart);
		}
	}
	
	private static class Microblocks implements CreateCallback<Microblock>, AddCallback<Microblock>
	{
		@Override
		public void onCreate(Map<ResourceLocation, ?> slaveset, BiMap<ResourceLocation, ? extends IForgeRegistry<?>> registries)
		{
			Map<ResourceLocation, Object> slaves = (Map<ResourceLocation, Object>)slaveset;
			slaves.put(MICROBLOCK_OVERLAPS, MicroblockOverlapRegistry.INSTANCE);
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
