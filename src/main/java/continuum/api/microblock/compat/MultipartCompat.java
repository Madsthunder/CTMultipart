package continuum.api.microblock.compat;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Predicate;

import continuum.api.microblock.Microblock;
import continuum.api.microblock.texture.MicroblockMaterial;
import continuum.api.multipart.MultipartState;
import continuum.essentials.mod.APIMethodMirrorable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;

public class MultipartCompat
{
	@APIMethodMirrorable
	public static boolean compatActive()
	{
		return false;
	}

	@APIMethodMirrorable
	/**
	 * 
	 * @param toOverlap Microblock that will be overlapped.
	 * @param microblock Microblock that will overlap.
	 * @param predicate Predicate used to decide if the Microblock should overlap.
	 * @return 
	 */
	public static boolean addMicroblockOverlap(Microblock toOverlap, Microblock microblock, Predicate<Pair<MultipartState<MultipartMicroblock>, MultipartState<MultipartMicroblock>>> predicate)
	{
		return false;
	}

	@APIMethodMirrorable
	public static boolean microblockOverlaps(MultipartState<MultipartMicroblock> subject, MultipartState<MultipartMicroblock> info)
	{
		return false;
	}
}
