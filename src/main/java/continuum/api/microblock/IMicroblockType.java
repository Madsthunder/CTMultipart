package continuum.api.microblock;

import continuum.essentials.block.ICuboid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public interface IMicroblockType<V>
{
	public String getName();
	
	public ICuboid[] getCuboids();
	
	public boolean getRender(int index1, int index2, AxisAlignedBB subject, IBlockState state);
	
	public AxisAlignedBB getAABBFromFacings(EnumFacing... facings);
	
	public BlockPos getRelativePos(EnumFacing... facings);
	
	public V getPlaceType();
}