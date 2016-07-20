package continuum.multipart.enums;

import continuum.essentials.block.ICuboid;
import continuum.essentials.block.StaticCuboid;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;

public enum CoverCuboid implements ILayeredCuboid
{
	COVER_D(0D, 0D, 0D, 16D, 2D, 16D, EnumFacing.DOWN),
	COVER_U(0D, 14D, 0D, 16D, 16D, 16D, EnumFacing.UP),
	COVER_N(0D, 0D, 0D, 16D, 16D, 2D, EnumFacing.NORTH),
	COVER_S(0D, 0D, 14D, 16D, 16D, 16D, EnumFacing.SOUTH),
	COVER_W(0D, 0D, 0D, 2D, 16D, 16D, EnumFacing.WEST),
	COVER_E(14D, 0D, 0D, 16D, 16D, 16D, EnumFacing.EAST);
	private AxisAlignedBB cuboid;
	private EnumFacing facing;
	
	private CoverCuboid(Double minX, Double minY, Double minZ, Double maxX, Double maxY, Double maxZ, EnumFacing facing)
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
