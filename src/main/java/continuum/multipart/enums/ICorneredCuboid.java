package continuum.multipart.enums;

import continuum.essentials.block.ICuboid;
import net.minecraft.util.EnumFacing;

public interface ICorneredCuboid extends ICuboid
{
	public EnumFacing getFacingX();
	
	public EnumFacing getFacingY();
	
	public EnumFacing getFacingZ();
}
