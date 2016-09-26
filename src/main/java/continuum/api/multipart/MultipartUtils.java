package continuum.api.multipart;

import javax.annotation.Nullable;

import com.google.common.collect.BiMap;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry;

public class MultipartUtils
{
	public static final ResourceLocation BLOCK_TO_MULTIPART = new ResourceLocation("ctmultipart", "blocktomultipart");
	public static final ResourceLocation ITEM_TO_MULTIPART = new ResourceLocation("ctmultipart", "itemtomultipart");
	
	public static MultipartState getSelectedInfo(Block testFor, EntityPlayer player, IBlockAccess access)
	{
		RayTraceResult result = ForgeHooks.rayTraceEyes(player, player.capabilities.isCreativeMode ? 4.5D : 5D);
		if(result != null && (testFor == null ? true : access.getBlockState(result.getBlockPos()).getBlock() == testFor))
			return (MultipartState)result.hitInfo;
		else
			return null;
	}
	
	public static BiMap<Block, Multipart> getBlockToMultipartMap()
	{
		IForgeRegistry<Multipart> multipartRegistry = getMultipartRegistry();
		if(multipartRegistry != null)
		{
			return multipartRegistry.getSlaveMap(BLOCK_TO_MULTIPART, BiMap.class);
		}
		return null;
	}
	
	public static BiMap<Block, Multipart> getItemToMultipartMap()
	{
		IForgeRegistry<Multipart> multipartRegistry = getMultipartRegistry();
		if(multipartRegistry != null)
		{
			return multipartRegistry.getSlaveMap(ITEM_TO_MULTIPART, BiMap.class);
		}
		return null;
	}
	
	@Nullable
	public static IForgeRegistry<Multipart> getMultipartRegistry()
	{
		return GameRegistry.findRegistry(Multipart.class);
	}
}
