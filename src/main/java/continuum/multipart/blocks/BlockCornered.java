package continuum.multipart.blocks;

import java.util.List;

import org.apache.commons.lang3.tuple.Triple;

import com.google.common.collect.Lists;

import continuum.api.microblock.IMicroblockType;
import continuum.api.microblocktexture.MicroblockTextureEntry;
import continuum.multipart.enums.EnumFacingArray;
import continuum.multipart.enums.EnumMicroblockType;
import continuum.multipart.mod.Multipart_OH;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.BlockStateContainer.Builder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockCornered extends BlockMicroblockBase<EnumMicroblockType>
{
	public static final PropertyDirection directionX = PropertyDirection.create("x", Lists.newArrayList(EnumFacing.WEST, EnumFacing.EAST));
	public static final PropertyDirection directionY = PropertyDirection.create("y", Lists.newArrayList(EnumFacing.DOWN, EnumFacing.UP));
	public static final PropertyDirection directionZ = PropertyDirection.create("z", Lists.newArrayList(EnumFacing.NORTH, EnumFacing.SOUTH));
	
	public BlockCornered(Multipart_OH objectHolder, EnumMicroblockType type)
	{
		super(objectHolder, type);
		this.setDefaultState(this.getDefaultState().withProperty(directionY, EnumFacing.DOWN).withProperty(directionZ, EnumFacing.NORTH).withProperty(directionX, EnumFacing.WEST));
	}
	
	@Override
	public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase entity)
	{
		switch(facing.getAxis())
		{
			case X :
				return this.getStateFromPlaceCoords(hitY, hitZ, facing, directionX, directionY, directionZ);
			case Y :
				return this.getStateFromPlaceCoords(hitX, hitZ, facing, directionY, directionX, directionZ);
			case Z :
				return this.getStateFromPlaceCoords(hitX, hitY, facing, directionZ, directionX, directionY);
			default :
				return this.getDefaultState();
		}
	}
	
	private IBlockState getStateFromPlaceCoords(Float first, Float second, EnumFacing side, PropertyDirection... properties)
	{
		EnumFacing[] fv = properties[1].getAllowedValues().toArray(new EnumFacing[0]);
		EnumFacing[] sv = properties[2].getAllowedValues().toArray(new EnumFacing[0]);
		return this.getDefaultState().withProperty(properties[0], side.getOpposite()).withProperty(properties[1], fv[first > .5 ? 1 : 0]).withProperty(properties[2], sv[second > .5 ? 1 : 0]);
	}
	
	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(directionY).ordinal() * 4 + ((state.getValue(directionZ).ordinal() - 2) * 2 + (state.getValue(directionX).ordinal() - 4));
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		IBlockState state = this.getDefaultState();
		if(meta >= 4)
		{
			state = state.withProperty(directionY, EnumFacing.UP);
			meta = meta - 4;
		}
		if(meta >= 2)
		{
			state = state.withProperty(directionZ, EnumFacing.SOUTH);
			meta = meta - 2;
		}
		if(meta == 1) state = state.withProperty(directionX, EnumFacing.EAST);
		return state;
	}
	
	@Override
	public BlockStateContainer createBlockState()
	{
		return new Builder(this).add(directionX, directionY, directionZ).build();
	}
	
	@Override
	public IBlockState withRotation(IBlockState state, Rotation rotation)
	{
		EnumFacing facingX = state.getValue(directionX);
		EnumFacing facingZ = state.getValue(directionZ);
		if(facingX == EnumFacing.WEST)
			if(facingZ == EnumFacing.NORTH)
				return state.withProperty(directionX, EnumFacing.EAST);
			else
				return state.withProperty(directionZ, EnumFacing.NORTH);
		else if(facingZ == EnumFacing.NORTH)
			return state.withProperty(directionZ, EnumFacing.SOUTH);
		else
			return state.withProperty(directionX, EnumFacing.WEST);
	}
	
	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirror)
	{
		return state.withProperty(directionY, state.getValue(directionY).getOpposite());
	}
	
	@Override
	public List<Triple<Boolean, AxisAlignedBB, BlockPos>> getRenderList(IBlockState state, MicroblockTextureEntry entry)
	{
		IMicroblockType type = this.getType();
		EnumFacingArray array = new EnumFacingArray(state.getValue(directionX), state.getValue(directionY), state.getValue(directionZ));
		EnumFacing[] facings = array.toArray();
		if(type == EnumMicroblockType.CORNER) facings = array.toArray(array);
		if(type == EnumMicroblockType.NOOK) facings = array.toArray(array, array);
		return Lists.newArrayList(Triple.of(true, type.getAABBFromFacings(facings), type.getRelativePos(facings)));
	}
}
