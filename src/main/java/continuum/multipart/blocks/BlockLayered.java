package continuum.multipart.blocks;

import continuum.api.microblock.Microblock;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.BlockStateContainer.Builder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLayered extends BlockMicroblockBase
{
	public static final PropertyDirection direction = PropertyDirection.create("direction");
	
	public BlockLayered(Microblock microblock)
	{
		super(microblock);
		this.setDefaultState(this.getDefaultState().withProperty(direction, EnumFacing.DOWN));
	}
	
	@Override
	public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase entity)
	{
		switch(facing.getAxis())
		{
			case X :
				return this.getStateFromPlaceCoords(hitY, hitZ, facing, EnumFacing.NORTH, EnumFacing.DOWN);
			case Y :
				return this.getStateFromPlaceCoords(hitX, hitZ, facing, EnumFacing.NORTH, EnumFacing.WEST);
			case Z :
				return this.getStateFromPlaceCoords(hitX, hitY, facing, EnumFacing.DOWN, EnumFacing.WEST);
			default:
				return this.getDefaultState();
		}
	}
	
	private IBlockState getStateFromPlaceCoords(Float first, Float second, EnumFacing... facings)
	{
		IBlockState state = this.getDefaultState();
		Boolean FGT5 = first > 0.5;
		Boolean SGT5 = second > 0.5;
		if(first >= 0.3125 && first <= 0.6875 && second >= 0.3125 && second <= 0.6875)
			return state.withProperty(direction, facings[0].getOpposite());
		else if((!FGT5 && !SGT5 && first == second) || (!SGT5 && first > second && first < 1 - second))
			return state.withProperty(direction, facings[1]);
		else if((FGT5 && SGT5 && first == second) || (SGT5 && first < second && first > 1 - second))
			return state.withProperty(direction, facings[1].getOpposite());
		else if((!FGT5 && SGT5 && first == 1 - second) || (!FGT5 && first < second && 1 - first > second))
			return state.withProperty(direction, facings[2]);
		else if((FGT5 && !SGT5 && 1 - first == second) || (FGT5 && first > second && 1 - first < second))
			return state.withProperty(direction, facings[2].getOpposite());
		else
			return state;
	}
	
	@Override
	public boolean isSideSolid(IBlockState state, IBlockAccess access, BlockPos pos, EnumFacing facing)
	{
		return facing == state.getValue(direction);
	}
	
	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(direction).ordinal();
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(direction, EnumFacing.values()[meta]);
	}
	
	@Override
	public BlockStateContainer createBlockState()
	{
		return new Builder(this).add(direction).build();
	}
	
	@Override
	public IBlockState withRotation(IBlockState state, Rotation rotation)
	{
		return state.withProperty(direction, rotation.rotate(state.getValue(direction)));
	}
	
	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirror)
	{
		return state.withProperty(direction, state.getValue(direction).getOpposite());
	}
}
