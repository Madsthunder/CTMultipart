package continuum.multipart.enums;

import continuum.essentials.block.ICuboid;
import continuum.essentials.block.StaticCuboid;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;

public enum SlabCuboid implements ILayeredCuboid
{
	SLAB_D(0D, 0D, 0D, 16D, 8D, 16D, EnumFacing.DOWN),
	SLAB_U(0D, 8D, 0D, 16D, 16D, 16D, EnumFacing.UP),
	SLAB_N(0D, 0D, 0D, 16D, 16D, 8D, EnumFacing.NORTH),
	SLAB_S(0D, 0D, 8D, 16D, 16D, 16D, EnumFacing.SOUTH),
	SLAB_W(0D, 0D, 0D, 8D, 16D, 16D, EnumFacing.WEST),
	SLAB_E(8D, 0D, 0D, 16D, 16D, 16D, EnumFacing.EAST);
	private AxisAlignedBB cuboid;
	private EnumFacing facing;
	
	private SlabCuboid(Double minX, Double minY, Double minZ, Double maxX, Double maxY, Double maxZ, EnumFacing facing)
	{
		this.cuboid = new AxisAlignedBB(minX / 16, minY / 16, minZ / 16, maxX / 16, maxY / 16, maxZ / 16);
		this.facing = facing;
	}
	
	@Override
	public AxisAlignedBB getSelectableCuboid()
	{
		return this.cuboid;
	}
	
	@Override
	public AxisAlignedBB getShowableCuboid()
	{
		return this.cuboid;
	}
	
	@Override
	public EnumFacing getSide()
	{
		return this.facing;
	}
	
	@Override
	public ICuboid addExtraData(Object obj)
	{
		return this;
	}
	
	@Override
	public Object getExtraData()
	{
		return null;
	}
	
	@Override
	public ICuboid copy()
	{
		return new StaticCuboid(this);
	}
}
