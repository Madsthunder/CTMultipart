package continuum.multipart.multiparts;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import continuum.api.multipart.Multipart;
import continuum.api.multipart.MultipartInfo;
import continuum.api.multipart.TileEntityMultiblock;
import continuum.essentials.block.ICuboid;
import continuum.essentials.block.StaticCuboid;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;

public class MultipartFlowerPot extends Multipart
{
	private static final AxisAlignedBB aabb = new AxisAlignedBB(0.3125D, 0.0D, 0.3125D, 0.6875D, 0.375D, 0.6875D);
	
	@Override
	public boolean canPlaceIn(IBlockAccess access, BlockPos pos, IBlockState state, TileEntityMultiblock source, RayTraceResult result)
	{
		return !source.boxIntersectsMultipart(this, aabb, false, false);
	}
	
	@Override
	public List<ICuboid> getCuboids(MultipartInfo info)
	{
		ArrayList<ICuboid> cuboids = Lists.newArrayList();
		cuboids.add(new StaticCuboid(aabb));
		return cuboids;
	}
	
	@Override
	public List<AxisAlignedBB> getCollisionBoxes(MultipartInfo info)
	{
		ArrayList<AxisAlignedBB> list = Lists.newArrayList();
		list.add(aabb);
		return list;
	}
	
	@Override
	public IBlockState getMultipartState(MultipartInfo info)
	{
		BlockFlowerPot.EnumFlowerType blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.EMPTY;
		TileEntity entity = info.getTileEntity();
		if(entity instanceof TileEntityFlowerPot)
		{
			TileEntityFlowerPot tileentityflowerpot = (TileEntityFlowerPot)entity;
			Item item = tileentityflowerpot.getFlowerPotItem();
			if(item instanceof ItemBlock)
			{
				int i = tileentityflowerpot.getFlowerPotData();
				Block block = Block.getBlockFromItem(item);
				if(block == Blocks.SAPLING)
				{
					switch(BlockPlanks.EnumType.byMetadata(i))
					{
						case OAK :
							blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.OAK_SAPLING;
							break;
						case SPRUCE :
							blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.SPRUCE_SAPLING;
							break;
						case BIRCH :
							blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.BIRCH_SAPLING;
							break;
						case JUNGLE :
							blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.JUNGLE_SAPLING;
							break;
						case ACACIA :
							blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.ACACIA_SAPLING;
							break;
						case DARK_OAK :
							blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.DARK_OAK_SAPLING;
							break;
						default :
							blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.EMPTY;
					}
				}
				else if(block == Blocks.TALLGRASS)
				{
					switch(i)
					{
						case 0 :
							blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.DEAD_BUSH;
							break;
						case 2 :
							blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.FERN;
							break;
						default :
							blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.EMPTY;
					}
				}
				else if(block == Blocks.YELLOW_FLOWER)
				{
					blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.DANDELION;
				}
				else if(block == Blocks.RED_FLOWER)
				{
					switch(BlockFlower.EnumFlowerType.getType(BlockFlower.EnumFlowerColor.RED, i))
					{
						case POPPY :
							blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.POPPY;
							break;
						case BLUE_ORCHID :
							blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.BLUE_ORCHID;
							break;
						case ALLIUM :
							blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.ALLIUM;
							break;
						case HOUSTONIA :
							blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.HOUSTONIA;
							break;
						case RED_TULIP :
							blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.RED_TULIP;
							break;
						case ORANGE_TULIP :
							blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.ORANGE_TULIP;
							break;
						case WHITE_TULIP :
							blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.WHITE_TULIP;
							break;
						case PINK_TULIP :
							blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.PINK_TULIP;
							break;
						case OXEYE_DAISY :
							blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.OXEYE_DAISY;
							break;
						default :
							blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.EMPTY;
					}
				}
				else if(block == Blocks.RED_MUSHROOM)
				{
					blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.MUSHROOM_RED;
				}
				else if(block == Blocks.BROWN_MUSHROOM)
				{
					blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.MUSHROOM_BROWN;
				}
				else if(block == Blocks.DEADBUSH)
				{
					blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.DEAD_BUSH;
				}
				else if(block == Blocks.CACTUS)
				{
					blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.CACTUS;
				}
			}
		}
		return info.getState().withProperty(BlockFlowerPot.CONTENTS, blockflowerpot$enumflowertype);
	}
	
	@Override
	public Block getBlock()
	{
		return Blocks.FLOWER_POT;
	}
	
	@Override
	public boolean onMultipartActivated(MultipartInfo info, EntityPlayer player, EnumHand hand, ItemStack stack, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if(stack != null && stack.getItem() instanceof ItemBlock)
		{
			TileEntityFlowerPot tileentityflowerpot = (TileEntityFlowerPot)info.getTileEntity();
			if(tileentityflowerpot == null)
			{
				return false;
			}
			else if(tileentityflowerpot.getFlowerPotItem() != null)
			{
				return false;
			}
			else
			{
				Block block = Block.getBlockFromItem(stack.getItem());
				if(!this.canContain(block, stack.getMetadata()))
				{
					return false;
				}
				else
				{
					tileentityflowerpot.setFlowerPotData(stack.getItem(), stack.getMetadata());
					tileentityflowerpot.markDirty();
					info.getWorld().notifyBlockUpdate(info.getPos(), info.getSourceState(), info.getSourceState(), 3);
					player.addStat(StatList.FLOWER_POTTED);
					if(!player.capabilities.isCreativeMode)
					{
						--stack.stackSize;
					}
					return true;
				}
			}
		}
		else
		{
			return false;
		}
	}
	
	private boolean canContain(Block blockIn, int meta)
	{
		return blockIn != Blocks.YELLOW_FLOWER && blockIn != Blocks.RED_FLOWER && blockIn != Blocks.CACTUS && blockIn != Blocks.BROWN_MUSHROOM && blockIn != Blocks.RED_MUSHROOM && blockIn != Blocks.SAPLING && blockIn != Blocks.DEADBUSH ? blockIn == Blocks.TALLGRASS && meta == BlockTallGrass.EnumType.FERN.getMeta() : true;
	}
}
