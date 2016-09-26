package continuum.multipart.enums;

import continuum.essentials.block.ICuboid;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;

public interface IAxisedCuboid extends ICuboid
{
	public Axis getAxis();
	
	public EnumFacing getFacing1();
	
	public EnumFacing getFacing2();
	
	default public boolean isCentered()
	{
		return this.getFacing1().getOpposite().equals(this.getFacing2());
	}
	
	public int ordinal();
}
