package continuum.multipart.blocks;

import continuum.api.microblock.BlockMicroblockBase;
import continuum.api.microblock.Microblock;
import continuum.api.microblock.TileEntityMicroblockBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockDefaultMicroblock extends BlockMicroblockBase
{
	public BlockDefaultMicroblock(Microblock microblock)
	{
		super(microblock);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileEntityMicroblockBase();
	}
}
