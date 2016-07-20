package continuum.multipart.enums;

import continuum.essentials.block.ICuboid;
import continuum.essentials.block.StaticCuboid;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;

public enum NotchCuboid implements ICorneredCuboid
{
	NOTCH_DNW(0D, 0D, 0D, 8D, 8D, 8D, EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.WEST),
	NOTCH_DNE(8D, 0D, 0D, 16D, 8D, 8D, EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.EAST),
	NOTCH_DSW(0D, 0D, 8D, 8D, 8D, 16D, EnumFacing.DOWN, EnumFacing.SOUTH, EnumFacing.WEST),
	NOTCH_DSE(8D, 0D, 8D, 16D, 8D, 16D, EnumFacing.DOWN, EnumFacing.SOUTH, EnumFacing.EAST),
	NOTCH_UNW(0D, 8D, 0D, 8D, 16D, 8D, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.WEST),
	NOTCH_UNE(8D, 8D, 0D, 16D, 16D, 8D, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.EAST),
	NOTCH_USW(0D, 8D, 8D, 8D, 16D, 16D, EnumFacing.UP, EnumFacing.SOUTH, EnumFacing.WEST),
	NOTCH_USE(8D, 8D, 8D, 16D, 16D, 16D, EnumFacing.UP, EnumFacing.SOUTH, EnumFacing.EAST);
	private AxisAlignedBB cuboid;
	private EnumFacing facingX;
	private EnumFacing facingY;
	private EnumFacing facingZ;
	
	private NotchCuboid(Double minX, Double minY, Double minZ, Double maxX, Double maxY, Double maxZ, EnumFacing facingY, EnumFacing facingZ, EnumFacing facingX)
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
