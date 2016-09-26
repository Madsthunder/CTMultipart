package continuum.multipart.enums;

import continuum.essentials.block.ICuboid;
import net.minecraft.util.EnumFacing;

public interface ILayeredCuboid extends ICuboid
{
	public EnumFacing getSide();
	public int ordinal();
}
