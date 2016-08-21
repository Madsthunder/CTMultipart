package continuum.api.microblock;

import java.util.List;

import org.apache.commons.lang3.tuple.Triple;

import continuum.api.microblocktexture.MicroblockTextureEntry;
import continuum.api.multipart.Multipart;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IMicroblock<MT extends IMicroblockType>
{
	public IMicroblockType getType();
	
	@SideOnly(Side.CLIENT)
	public List<Triple<Boolean, AxisAlignedBB, BlockPos>> getRenderList(IBlockState state, MicroblockTextureEntry entry);
	
	public Multipart getMultipart();
}
