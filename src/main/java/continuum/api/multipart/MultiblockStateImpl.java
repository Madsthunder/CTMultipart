package continuum.api.multipart;

import net.minecraft.block.state.BlockStateContainer.StateImplementation;

public class MultiblockStateImpl extends StateImplementation
{
	private final StateImplementation implementation;
	private final MultipartStateList infoList;
	private final MultipartState info;
	
	public MultiblockStateImpl(StateImplementation state, MultipartStateList infoList, MultipartState info)
	{
		super(state.getBlock(), state.getProperties(), state.getPropertyValueTable());
		this.implementation = state;
		this.infoList = infoList;
		this.info = info;
	}
	
	public MultipartStateList getInfoList()
	{
		return this.infoList;
	}
	
	public MultipartState getInfo()
	{
		return this.info;
	}
	
	public StateImplementation getImplementation()
	{
		return this.implementation;
	}
}
