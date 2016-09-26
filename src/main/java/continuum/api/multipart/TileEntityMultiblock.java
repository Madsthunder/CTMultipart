package continuum.api.multipart;

import java.util.HashSet;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import continuum.api.multipart.MultipartEvent.AABBExceptionsEvent;
import continuum.essentials.tileentity.TileEntitySyncable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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