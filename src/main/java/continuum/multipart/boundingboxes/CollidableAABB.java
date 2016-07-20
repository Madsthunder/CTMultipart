package continuum.api.multipart.boundingboxes;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class CollidableAABB extends AxisAlignedBB
{
	public final Boolean collidable;
	
	public CollidableAABB(double x1, double y1, double z1, double x2, double y2, double z2, Boolean collidable)
	{
		super(x1, y1, z1, x2, y2, z2);
		this.collidable = collidable;
	}
	
	public CollidableAABB(BlockPos pos, Boolean collidable)
	{
		super(pos);
		this.collidable = collidable;
	}
	
	public CollidableAABB(BlockPos pos1, BlockPos pos2, Boolean collidable)
	{
		super(pos1, pos2);
		this.collidable = collidable;
	}
	
	public CollidableAABB(AxisAlignedBB aabb, Boolean collidable)
	{
		this(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ, collidable);
	}
}
