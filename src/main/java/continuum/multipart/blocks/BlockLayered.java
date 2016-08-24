package continuum.multipart.blocks;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Triple;

import com.google.common.collect.Lists;

import continuum.api.microblock.texture.MicroblockTextureEntry;
import continuum.api.multipart.MultiblockStateImpl;
import continuum.api.multipart.MultipartInfo;
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
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLayered extends BlockMicroblockBase<EnumMicroblockType>
{
	public static final PropertyDirection direction = PropertyDirection.create("direction");
	
	public BlockLayered(Multipart_OH objectHolder, EnumMicroblockType type)
	{
		super(objectHolder, type);
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
			default :
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
	
	@Override
	public List<Triple<Boolean, AxisAlignedBB, BlockPos>> getRenderList(IBlockState state, MicroblockTextureEntry entry)
	{
		ArrayList<Triple<Boolean, AxisAlignedBB, BlockPos>> renderList = Lists.newArrayList();
		EnumMicroblockType type = this.getType();
		EnumFacing[] fs = EnumFacing.values();
		EnumFacing f = state.getValue(direction);
		if(state instanceof MultiblockStateImpl)
		{
			MultiblockStateImpl mstate = (MultiblockStateImpl)state;
			List<MultipartInfo> list = Lists.newArrayList();
			for(EnumMicroblockType etype : EnumMicroblockType.values())
				if(etype == type || (etype.getPlaceType() == this.getType().getPlaceType() && etype.overrides(type)))
				{
					list.addAll(mstate.getSource().getAllDataOfBlockInstance(etype.getMultipart().getBlock().getClass()));
				}
			Integer index = list.indexOf(mstate.getInfo());
			for(Integer i = 0; i < 2; i++)
			{
				EnumFacing fy = fs[i];
				for(Integer j = 2; j < 4; j++)
				{
					EnumFacing fz = fs[j];
					for(Integer k = 4; k < 6; k++)
					{
						EnumFacing fx = fs[k];
						EnumFacing[] facings = new EnumFacing[]
						{
								fx, fy, fz
						};
						Boolean render = f == fx || f == fy || f == fz;
						if(type != EnumMicroblockType.SLAB)
						{
							for(Integer l = 0; l < 2; l++)
							{
								EnumFacing fy2 = fs[l];
								for(Integer m = 2; m < 4; m++)
								{
									EnumFacing fz2 = fs[m];
									for(Integer n = 4; n < 6; n++)
									{
										EnumFacing fx2 = fs[n];
										Boolean render2 = (f == fx2 || f == fy2 || f == fz2);
										facings = new EnumFacing[]
										{
												fx, fx2, fy, fy2, fz, fz2
										};
										if(type != EnumMicroblockType.PANEL)
											for(Integer o = 0; o < 2; o++)
											{
												EnumFacing fy3 = fs[o];
												for(Integer p = 2; p < 4; p++)
												{
													EnumFacing fz3 = fs[p];
													for(Integer q = 4; q < 6; q++)
													{
														EnumFacing fx3 = fs[q];
														this.attemptToAdd(type, index, (f == fx3 || f == fy3 || f == fz3) ? render2 ? render : false : false, facings, list, renderList);
													}
												}
											}
										else
											this.attemptToAdd(type, index, render2 ? render : false, facings, list, renderList);
									}
								}
							}
						}
						else
							this.attemptToAdd(type, index, render, facings, list, renderList);
					}
				}
			}
		}
		else
		{
			for(Integer i = 0; i < 2; i++)
			{
				EnumFacing fy = fs[i];
				for(Integer j = 2; j < 4; j++)
				{
					EnumFacing fz = fs[j];
					for(Integer k = 4; k < 6; k++)
					{
						EnumFacing fx = fs[k];
						EnumFacing[] facings = new EnumFacing[]
						{
								fx, fy, fz
						};
						Boolean render = f == fx || f == fy || f == fz;
						if(type != EnumMicroblockType.SLAB)
							for(Integer l = 0; l < 2; l++)
							{
								EnumFacing fy2 = fs[l];
								for(Integer m = 2; m < 4; m++)
								{
									EnumFacing fz2 = fs[m];
									for(Integer n = 4; n < 6; n++)
									{
										EnumFacing fx2 = fs[n];
										Boolean render2 = (f == fx2 || f == fy2 || f == fz2);
										facings = new EnumFacing[]
										{
												fx, fx2, fy, fy2, fz, fz2
										};
										if(type != EnumMicroblockType.PANEL)
											for(Integer o = 0; o < 2; o++)
											{
												EnumFacing fy3 = fs[o];
												for(Integer p = 2; p < 4; p++)
												{
													EnumFacing fz3 = fs[p];
													for(Integer q = 4; q < 6; q++)
													{
														EnumFacing fx3 = fs[q];
														facings = new EnumFacing[]
														{
																fx, fx2, fx3, fy, fy2, fy3, fz, fz2, fz3
														};
														renderList.add(Triple.of((f == fx3 || f == fy3 || f == fz3) ? render2 ? render : false : false, type.getAABBFromFacings(facings), type.getRelativePos(facings)));
													}
												}
											}
										else
											renderList.add(Triple.of(render2 ? render : false, type.getAABBFromFacings(facings), type.getRelativePos(facings)));
									}
								}
							}
						else
							renderList.add(Triple.of(render, type.getAABBFromFacings(facings), type.getRelativePos(facings)));
					}
				}
			}
		}
		return renderList;
	}
	
	public void attemptToAdd(EnumMicroblockType type, Integer index, Boolean render, EnumFacing[] facings, List<MultipartInfo> list, List<Triple<Boolean, AxisAlignedBB, BlockPos>> renderList)
	{
		AxisAlignedBB aabb = type.getAABBFromFacings(facings);
		for(Integer l = 0; render != true && l < list.size(); l++)
		{
			if(aabb.maxY <= 0.25) if(l != index && !type.getRender(index, l, aabb, list.get(l).getState())) render = false;
		}
		renderList.add(Triple.of(render, aabb, type.getRelativePos(facings)));
	}
}
