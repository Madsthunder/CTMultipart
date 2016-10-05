package continuum.api.multipart;

import continuum.essentials.tileentity.TileEntitySyncable;

public class TileEntityMultiblockBase extends TileEntitySyncable
{
	public TileEntityMultiblockBase()
	{
		super(false, true, true);
	}
	
	@Override
	public boolean canRenderBreaking()
	{
		return true;
	}
}