package continuum.multipart.multiparts;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import continuum.api.multipart.MultipartInfo;
import continuum.api.multipart.TileEntityMultiblock;
import continuum.api.multipart.boundingboxes.CollidableAABB;
import continuum.api.multipart.implementations.Multipart;
import continuum.essentials.block.ICuboid;
import continuum.essentials.block.StaticCuboid;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;

public class MutipartTorch extends Multipart
{
	@Override
	public boolean canPlaceIn(IBlockAccess access, BlockPos pos, IBlockState state, TileEntityMultiblock source, RayTraceResult result)
	{
		return !source.boxIntersectsMultipart(null, state.getBoundingBox(access, pos), false, false);
	}
	
	@Override
	public List<ICuboid> getCuboids(MultipartInfo info)
	{
		return Lists.asList(new StaticCuboid(info.getState().getBoundingBox(info.getWorld(), info.getPos())), new ICuboid[0]);
	}
	
	@Override
	public List<AxisAlignedBB> getCollisionBoxes(MultipartInfo info)
	{
		ArrayList<AxisAlignedBB> boxes = Lists.newArrayList();
		boxes.add(new CollidableAABB(info.getState().getBoundingBox(info.getWorld(), info.getPos()), false));
		return boxes;
	}
	
	@Override
	public Block getBlock()
	{
		return Blocks.TORCH;
	}
}
