package continuum.multipart.blocks;

import continuum.api.microblock.Microblock;
import continuum.multipart.enums.DefaultMicroblock;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.BlockStateContainer.Builder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockAxised extends BlockMicroblockBase
{
	public static final PropertyDirection direction1 = PropertyDirection.create("direction1");
	public static final PropertyDirection direction2 = PropertyDirection.create("direction2");
	
	public BlockAxised(Microblock microblock)
	{
		super(microblock);
		this.setDefaultState(this.getDefaultState().withProperty(direction1, EnumFacing.DOWN).withProperty(direction2, EnumFacing.UP));
	}
	
	public boolean isBlockNormalCube(IBlockState state)
	{
		return state.getMaterial().blocksMovement() && state.isFullCube();
	}
	
	@Override
	public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase entity)
	{
		IBlockState state = this.getDefaultState();
		boolean des = this.getMicroblock() != DefaultMicroblock.STRIP;
		Boolean xbtc = hitX >= 0.25 && hitX <= 0.75;
		Boolean ybtc = hitY >= 0.25 && hitY <= 0.75;
		Boolean zbtc = hitZ >= 0.25 && hitZ <= 0.75;
		EnumFacing[] fs = new EnumFacing[] { hitY > 0.5 ? EnumFacing.UP : EnumFacing.DOWN, hitZ > 0.5 ? EnumFacing.SOUTH : EnumFacing.NORTH, hitX > 0.5 ? EnumFacing.EAST : EnumFacing.WEST, facing.getOpposite() };
		switch(facing.getAxis())
		{
			case X :
				if(des && ybtc && zbtc)
					return state.withProperty(direction1, EnumFacing.WEST).withProperty(direction2, EnumFacing.EAST);
				if(ybtc)
					return state.withProperty(direction1, fs[1]).withProperty(direction2, fs[3]);
				if(zbtc)
					return state.withProperty(direction1, fs[0]).withProperty(direction2, fs[3]);
				return state.withProperty(direction1, fs[0]).withProperty(direction2, fs[1]);
			case Y :
				if(des && xbtc && zbtc)
					return state.withProperty(direction1, EnumFacing.DOWN).withProperty(direction2, EnumFacing.UP);
				if(xbtc)
					return state.withProperty(direction1, fs[3]).withProperty(direction2, fs[1]);
				if(zbtc)
					return state.withProperty(direction1, fs[3]).withProperty(direction2, fs[2]);
				return state.withProperty(direction1, fs[1]).withProperty(direction2, fs[2]);
			case Z :
				if(des && xbtc && ybtc)
					return state.withProperty(direction1, EnumFacing.NORTH).withProperty(direction2, EnumFacing.SOUTH);
				if(xbtc)
					return state.withProperty(direction1, fs[0]).withProperty(direction2, fs[3]);
				if(ybtc)
					return state.withProperty(direction1, fs[3]).withProperty(direction2, fs[2]);
				return state.withProperty(direction1, fs[0]).withProperty(direction2, fs[2]);
			default:
				return state;
		}
	}
	
	@Override
	public int getMetaFromState(IBlockState state)
	{
		state = fixBlockState(state);
		EnumFacing[] ds = new EnumFacing[] { state.getValue(direction1), state.getValue(direction2) };
		Axis[] as = new Axis[] { ds[0].getAxis(), ds[1].getAxis() };
		Axis axis = getAxis(as[0], as[1]);
		return (axis.ordinal() * 5) + (axis == as[0] ? 0 : ((ds[0].ordinal() - (as[0] == Axis.Z ? 2 : 0)) * 2 + (ds[1].ordinal() - (as[1] == Axis.X ? 4 : 2))) + 1);
	}
	
	public static Axis getAxis(Axis axis1, Axis axis2)
	{
		if(axis1 == axis2)
			return axis1;
		else
		{
			for(Axis axis : Axis.values())
				if(axis != axis1 && axis != axis2)
					return axis;
		}
		return null;
	}
	
	public static IBlockState fixBlockState(IBlockState state)
	{
		if(state.getBlock() instanceof BlockAxised)
		{
			EnumFacing d1 = state.getValue(direction1);
			EnumFacing d2 = state.getValue(direction2);
			if(d1 == d2)
				return fixBlockState(state.withProperty(direction2, d2.getOpposite()));
			if(d1.ordinal() > d2.ordinal())
				return state.withProperty(direction1, d2).withProperty(direction2, d1);
		}
		return state;
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		IBlockState state = this.getDefaultState();
		Axis a;
		if(meta >= 10)
		{
			a = Axis.Z;
			meta -= 10;
		}
		else if(meta >= 5)
		{
			a = Axis.Y;
			meta -= 5;
		}
		else
			a = Axis.X;
		if(meta == 0)
		{
			Integer i = 0;
			for(EnumFacing facing : EnumFacing.values())
				if(facing.getAxis() == a)
					state = state.withProperty(i++ == 0 ? direction1 : direction2, facing);
		}
		else
		{
			meta -= 1;
			EnumFacing[] facings = new EnumFacing[4];
			Integer i = 0;
			for(EnumFacing facing : EnumFacing.values())
				if(facing.getAxis() != a)
					facings[i++] = facing;
			if(meta >= 2)
			{
				state = state.withProperty(direction1, facings[1]);
				meta -= 2;
			}
			else
				state = state.withProperty(direction1, facings[0]);
			if(meta >= 1)
				state = state.withProperty(direction2, facings[3]);
			else
				state = state.withProperty(direction2, facings[2]);
		}
		return state;
	}
	
	@Override
	public IBlockState withRotation(IBlockState state, Rotation rotation)
	{
		return state/**
					 * .withProperty(direction,
					 * rotation.rotate(state.getValue(direction)))
					 */
		;
	}
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess access, BlockPos pos)
	{
		return fixBlockState(state);
	}
	
	@Override
	public BlockStateContainer createBlockState()
	{
		return new Builder(this).add(direction1, direction2).build();
	}
}