package continuum.api.multipart;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class PermanentAABB extends AxisAlignedBB
{
	public final boolean permanent;
	
	public PermanentAABB(double x1, double y1, double z1, double x2, double y2, double z2, boolean permanent)
	{
		super(x1, y1, z1, x2, y2, z2);
		this.permanent = permanent;
	}
	
	public PermanentAABB(AxisAlignedBB aabb, boolean permanent)
	{
		this(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ, permanent);
	}
}
