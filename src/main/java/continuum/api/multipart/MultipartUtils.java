package continuum.api.multipart;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeHooks;

public class MultipartUtils
{

	public static MultipartState getSelectedInfo(Block testFor, EntityPlayer player, IBlockAccess access)
	{
		RayTraceResult result = ForgeHooks.rayTraceEyes(player, player.capabilities.isCreativeMode ? 4.5D : 5D);
		if(result != null && (testFor == null ? true : access.getBlockState(result.getBlockPos()).getBlock() == testFor))
			return (MultipartState)result.hitInfo;
		else
			return null;
	}
	
}
