package continuum.api.multipart;

import continuum.essentials.tileentity.TileEntitySyncable;

public class TileEntityMultiblock extends TileEntitySyncable
{
	public TileEntityMultiblock()
	{
		super(false, true);
	}
	
	@Override
	public boolean canRenderBreaking()
	{
		return true;
	}
	
	@Override
	public boolean hasFastRenderer()
	{
		return true;
	}
}