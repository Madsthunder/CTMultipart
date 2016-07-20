package continuum.multipart.enums;

import continuum.essentials.block.ICuboid;
import continuum.essentials.block.StaticCuboid;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;

public enum PanelCuboid implements ILayeredCuboid
{
	PANEL_D(0D, 0D, 0D, 16D, 4D, 16D, EnumFacing.DOWN),
	PANEL_U(0D, 12D, 0D, 16D, 16D, 16D, EnumFacing.UP),
	PANEL_M(0D, 0D, 0D, 16D, 16D, 4D, EnumFacing.NORTH),
	PANEL_S(0D, 0D, 12D, 16D, 16D, 16D, EnumFacing.SOUTH),
	PANEL_W(0D, 0D, 0D, 4D, 16D, 16D, EnumFacing.WEST),
	PANEL_E(12D, 0D, 0D, 16D, 16D, 16D, EnumFacing.EAST);
	private AxisAlignedBB cuboid;
	private EnumFacing facing;
	
	private PanelCuboid(Double minX, Double minY, Double minZ, Double maxX, Double maxY, Double maxZ, EnumFacing facing)
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
