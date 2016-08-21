package continuum.multipart.enums;

import java.util.HashMap;

import com.google.common.collect.Maps;

import continuum.essentials.block.ICuboid;
import continuum.essentials.block.StaticCuboid;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;

public enum StripCuboid implements IPillaredCuboid
{
	POST_X_WE(0D, 7D, 7D, 16D, 9D, 9D, Axis.X, EnumFacing.WEST, EnumFacing.EAST),
	POST_X_DN(0D, 0D, 0D, 16D, 2D, 2D, Axis.X, EnumFacing.DOWN, EnumFacing.NORTH),
	POST_X_DS(0D, 0D, 14D, 16D, 2D, 16D, Axis.X, EnumFacing.DOWN, EnumFacing.SOUTH),
	POST_X_UN(0D, 14D, 0D, 16D, 16D, 2D, Axis.X, EnumFacing.UP, EnumFacing.NORTH),
	POST_X_US(0D, 14D, 14D, 16D, 16D, 16D, Axis.X, EnumFacing.UP, EnumFacing.SOUTH),
	POST_Y_DU(7D, 0D, 7D, 9D, 16D, 9D, Axis.Y, EnumFacing.DOWN, EnumFacing.UP),
	POST_Y_NW(0D, 0D, 0D, 2D, 16D, 2D, Axis.Y, EnumFacing.NORTH, EnumFacing.WEST),
	POST_Y_NE(14D, 0D, 0D, 16D, 16D, 2D, Axis.Y, EnumFacing.NORTH, EnumFacing.EAST),
	POST_Y_SW(0D, 0D, 14D, 2D, 16D, 16D, Axis.Y, EnumFacing.SOUTH, EnumFacing.WEST),
	POST_Y_SE(14D, 0D, 14D, 16D, 16D, 16D, Axis.Y, EnumFacing.SOUTH, EnumFacing.EAST),
	POST_Z_NS(7D, 7D, 0D, 9D, 9D, 16D, Axis.Z, EnumFacing.NORTH, EnumFacing.SOUTH),
	POST_Z_DW(0D, 0D, 0D, 2D, 2D, 16D, Axis.Z, EnumFacing.DOWN, EnumFacing.WEST),
	POST_Z_DE(14D, 0D, 0D, 16D, 2D, 16D, Axis.Z, EnumFacing.DOWN, EnumFacing.EAST),
	POST_Z_UW(0D, 14D, 0D, 2D, 16D, 16D, Axis.Z, EnumFacing.UP, EnumFacing.WEST),
	POST_Z_UE(12D, 12D, 0D, 16D, 16D, 16D, Axis.Z, EnumFacing.UP, EnumFacing.EAST);
	private static final HashMap<EnumFacing, HashMap<EnumFacing, StripCuboid>> values = Maps.newHashMap();
	private final AxisAlignedBB cuboid;
	private final Axis axis;
	private final EnumFacing facing1;
	private final EnumFacing facing2;
	
	private StripCuboid(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, Axis axis, EnumFacing facing1, EnumFacing facing2)
	{
		this.cuboid = new AxisAlignedBB(minX / 16, minY / 16, minZ / 16, maxX / 16, maxY / 16, maxZ / 16);
		this.axis = axis;
		this.facing1 = facing1;
		this.facing2 = facing2;
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
	
	public Axis getAxis()
	{
		return this.axis;
	}
	
	public EnumFacing getFacing1()
	{
		return this.facing1;
	}
	
	public EnumFacing getFacing2()
	{
		return this.facing2;
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
	
	public static StripCuboid getCuboidFromFacings(EnumFacing facing1, EnumFacing facing2)
	{
		return values.get(facing1).get(facing2);
	}
	
	static
	{
		for(StripCuboid cuboid : StripCuboid.values())
		{
			EnumFacing facing1 = cuboid.getFacing1();
			EnumFacing facing2 = cuboid.getFacing2();
			if(!values.containsKey(facing1))
			{
				HashMap<EnumFacing, StripCuboid> map = Maps.newHashMap();
				map.put(facing2, cuboid);
				values.put(facing1, map);
			}
			else
				values.get(facing1).put(facing2, cuboid);
		}
	}
}
