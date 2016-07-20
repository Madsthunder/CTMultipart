package continuum.multipart.enums;

import java.util.HashMap;

import continuum.essentials.block.ICuboid;
import continuum.essentials.block.StaticCuboid;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;

public enum PillarCuboid implements IPillaredCuboid
{
	PILLAR_X_WE(0D, 4D, 4D, 16D, 12D, 12D, Axis.X, EnumFacing.WEST, EnumFacing.EAST),
	PILLAR_X_DN(0D, 0D, 0D, 16D, 8D, 8D, Axis.X, EnumFacing.DOWN, EnumFacing.NORTH),
	PILLAR_X_DS(0D, 0D, 8D, 16D, 8D, 16D, Axis.X, EnumFacing.DOWN, EnumFacing.SOUTH),
	PILLAR_X_UN(0D, 8D, 0D, 16D, 16D, 8D, Axis.X, EnumFacing.UP, EnumFacing.NORTH),
	PILLAR_X_US(0D, 8D, 8D, 16D, 16D, 16D, Axis.X, EnumFacing.UP, EnumFacing.SOUTH),
	PILLAR_Y_DU(4D, 0D, 4D, 12D, 16D, 12D, Axis.Y, EnumFacing.DOWN, EnumFacing.UP),
	PILLAR_Y_NW(0D, 0D, 0D, 8D, 16D, 8D, Axis.Y, EnumFacing.NORTH, EnumFacing.WEST),
	PILLAR_Y_NE(8D, 0D, 0D, 16D, 16D, 8D, Axis.Y, EnumFacing.NORTH, EnumFacing.EAST),
	PILLAR_Y_SW(0D, 0D, 8D, 8D, 16D, 16D, Axis.Y, EnumFacing.SOUTH, EnumFacing.WEST),
	PILLAR_Y_SE(8D, 0D, 8D, 16D, 16D, 16D, Axis.Y, EnumFacing.SOUTH, EnumFacing.EAST),
	PILLAR_Z_NS(4D, 4D, 0D, 12D, 12D, 16D, Axis.Z, EnumFacing.NORTH, EnumFacing.SOUTH),
	PILLAR_Z_DW(0D, 0D, 0D, 8D, 8D, 16D, Axis.Z, EnumFacing.DOWN, EnumFacing.WEST),
	PILLAR_Z_DE(8D, 0D, 0D, 16D, 8D, 16D, Axis.Z, EnumFacing.DOWN, EnumFacing.EAST),
	PILLAR_Z_UW(0D, 8D, 0D, 8D, 16D, 16D, Axis.Z, EnumFacing.UP, EnumFacing.WEST),
	PILLAR_Z_UE(8D, 8D, 0D, 16D, 16D, 16D, Axis.Z, EnumFacing.UP, EnumFacing.EAST);
	private static final HashMap<EnumFacing, HashMap<EnumFacing, PillarCuboid>> values = new HashMap<EnumFacing, HashMap<EnumFacing, PillarCuboid>>();
	private final AxisAlignedBB cuboid;
	private final Axis axis;
	private final EnumFacing facing1;
	private final EnumFacing facing2;
	
	private PillarCuboid(Double minX, Double minY, Double minZ, Double maxX, Double maxY, Double maxZ, Axis axis, EnumFacing facing1, EnumFacing facing2)
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
	
	public static PillarCuboid getCuboidFromFacings(EnumFacing facing1, EnumFacing facing2)
	{
		return values.get(facing1).get(facing2);
	}
	
	static
	{
		for(PillarCuboid cuboid : PillarCuboid.values())
		{
			EnumFacing facing1 = cuboid.getFacing1();
			EnumFacing facing2 = cuboid.getFacing2();
			if(!values.containsKey(facing1))
			{
				HashMap<EnumFacing, PillarCuboid> map = new HashMap<EnumFacing, PillarCuboid>();
				map.put(facing2, cuboid);
				values.put(facing1, map);
			}
			else
				values.get(facing1).put(facing2, cuboid);
		}
	}
}
