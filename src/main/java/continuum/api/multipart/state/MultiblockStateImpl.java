package continuum.api.multipart.state;

import continuum.api.multipart.MultipartInfo;
import continuum.api.multipart.TileEntityMultiblock;
import net.minecraft.block.state.BlockStateContainer.StateImplementation;

public class MultiblockStateImpl extends StateImplementation
{
	private final StateImplementation implementation;
	private final TileEntityMultiblock source;
	private final MultipartInfo info;
	
	public MultiblockStateImpl(StateImplementation state, TileEntityMultiblock source, MultipartInfo info)
	{
		super(state.getBlock(), state.getProperties(), state.getPropertyValueTable());
		this.implementation = state;
		this.source = source;
		this.info = info;
	}
	
	public TileEntityMultiblock getSource()
	{
		return this.source;
	}
	
	public MultipartInfo getInfo()
	{
		return this.info;
	}
	
	public StateImplementation getImplementation()
	{
		return this.implementation;
	}
}
