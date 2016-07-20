package continuum.multipart.enums;

import continuum.essentials.block.ICuboid;
import continuum.essentials.block.StaticCuboid;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;

public enum NookCuboid implements ICorneredCuboid
{
	NOOK_DNW(0D, 0D, 0D, 2D, 2D, 2D, EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.WEST),
	NOOK_DNE(14D, 0D, 0D, 16D, 2D, 2D, EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.EAST),
	NOOK_DSW(0D, 0D, 14D, 2D, 2D, 16D, EnumFacing.DOWN, EnumFacing.SOUTH, EnumFacing.WEST),
	NOOK_DSE(14D, 0D, 14D, 16D, 2D, 16D, EnumFacing.DOWN, EnumFacing.SOUTH, EnumFacing.EAST),
	NOOK_UNW(0D, 14D, 0D, 2D, 16D, 2D, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.WEST),
	NOOK_UNE(14D, 14D, 0D, 16D, 16D, 2D, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.EAST),
	NOOK_USW(0D, 14D, 14D, 2D, 16D, 16D, EnumFacing.UP, EnumFacing.SOUTH, EnumFacing.WEST),
	NOOK_USE(14D, 14D, 14D, 16D, 16D, 16D, EnumFacing.UP, EnumFacing.SOUTH, EnumFacing.EAST);
	private AxisAlignedBB cuboid;
	private EnumFacing facingX;
	private EnumFacing facingY;
	private EnumFacing facingZ;
	
	private NookCuboid(Double minX, Double minY, Double minZ, Double maxX, Double maxY, Double maxZ, EnumFacing facingY, EnumFacing facingZ, EnumFacing facingX)
	{
		this.cuboid = new AxisAlignedBB(minX / 16, minY / 16, minZ / 16, maxX / 16, maxY / 16, maxZ / 16);
		this.facingX = facingX;
		this.facingY = facingY;
		this.facingZ = facingZ;
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
	
	@Override
	public EnumFacing getFacingX()
	{
		return this.facingX;
	}
	
	@Override
	public EnumFacing getFacingY()
	{
		return this.facingY;
	}
	
	@Override
	public EnumFacing getFacingZ()
	{
		return this.facingZ;
	}
}