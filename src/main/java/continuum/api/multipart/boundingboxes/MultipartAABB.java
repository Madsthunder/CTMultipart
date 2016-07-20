package continuum.api.multipart.boundingboxes;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class MultipartAABB extends AxisAlignedBB
{
	public final Boolean permanent;
	
	public MultipartAABB(double x1, double y1, double z1, double x2, double y2, double z2, Boolean permanent)
	{
		super(x1, y1, z1, x2, y2, z2);
		this.permanent = permanent;
	}
	
	public MultipartAABB(BlockPos pos, Boolean permanent)
	{
		super(pos);
		this.permanent = permanent;
	}
	
	public MultipartAABB(BlockPos pos1, BlockPos pos2, Boolean permanent)
	{
		super(pos1, pos2);
		this.permanent = permanent;
	}
	
	public MultipartAABB(AxisAlignedBB aabb, Boolean permanent)
	{
		this(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ, permanent);
	}
}
