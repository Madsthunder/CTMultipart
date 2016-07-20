package continuum.multipart.enums;

import continuum.essentials.block.ICuboid;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;

public interface IPillaredCuboid extends ICuboid
{
	public Axis getAxis();
	
	public EnumFacing getFacing1();
	
	public EnumFacing getFacing2();
}
