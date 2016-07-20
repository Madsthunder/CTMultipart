package continuum.multipart.enums;

import java.util.HashMap;

import org.apache.commons.lang3.tuple.Pair;

import continuum.api.multipart.CTMultipart_API;
import continuum.api.multipart.implementations.IMicroblock;
import continuum.api.multipart.implementations.IMicroblockType;
import continuum.api.multipart.implementations.Multipart;
import continuum.essentials.block.ICuboid;
import continuum.essentials.mod.CTMod;
import continuum.multipart.enums.EnumMicroblockType.EnumPlaceType;
import continuum.multipart.mod.Multipart_EH;
import continuum.multipart.mod.Multipart_OH;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public enum EnumMicroblockType implements IMicroblockType<EnumPlaceType>
{
	SLAB(EnumPlaceType.LAYERED, SlabCuboid.values()),
	PANEL(EnumPlaceType.LAYERED, PanelCuboid.values()),
	COVER(EnumPlaceType.LAYERED, CoverCuboid.values()),
	PILLAR(EnumPlaceType.AXISED, PillarCuboid.values()),
	POST(EnumPlaceType.AXISED, PostCuboid.values()),
	STRIP(EnumPlaceType.AXISED, null),
	NOTCH(EnumPlaceType.CORNERED, NotchCuboid.values()),
	CORNER(EnumPlaceType.CORNERED, CornerCuboid.values()),
	NOOK(EnumPlaceType.CORNERED, NookCuboid.values());
	private static final HashMap<EnumFacing, HashMap<EnumFacing, HashMap<EnumFacing, Pair<AxisAlignedBB, HashMap<EnumFacing, HashMap<EnumFacing, HashMap<EnumFacing, Pair<AxisAlignedBB, HashMap<EnumFacing, HashMap<EnumFacing, HashMap<EnumFacing, AxisAlignedBB>>>>>>>>>>> facingsToAABB;
	public static final EnumFacingArray[] facingComboList;
	private final String name;
	private final EnumPlaceType type;
	private final ICuboid[] cuboids;
	
	private EnumMicroblockType(EnumPlaceType type, ICuboid[] cuboids)
	{
		this.name = this.name().substring(0, 1).toUpperCase() + this.name().toLowerCase().substring(1, this.name().length());
		this.type = type;
		this.cuboids = cuboids;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public ICuboid[] getCuboids()
	{
		return this.cuboids;
	}
	
	public IMicroblock<EnumMicroblockType> getBlock(CTMod<Multipart_OH, Multipart_EH> mod)
	{
		Multipart_OH holder = mod.getObjectHolder();
		switch(this)
		{
			case SLAB :
				return holder.slab;
			case PANEL :
				return holder.panel;
			case COVER :
				return holder.cover;
			case PILLAR :
				return holder.pillar;
			case POST :
				return holder.post;
			case STRIP :
				return holder.strip;
			case NOTCH :
				return holder.notch;
			case CORNER :
				return holder.corner;
			case NOOK :
				return holder.nook;
			default :
				return null;
		}
	}
	
	public Multipart getMultipart()
	{
		return CTMultipart_API.getMultipart(new ResourceLocation("ctmultipart", this.getName()));
	}
	
	@Override
	public Boolean getRender(Integer index1, Integer index2, AxisAlignedBB subject, IBlockState state)
	{
		if(state.getBlock() instanceof IMicroblock)
		{
			IMicroblock microblock = (IMicroblock)state.getBlock();
			IMicroblockType type = microblock.getType();
			if(type instanceof EnumMicroblockType) if(this.ordinal() < ((EnumMicroblockType)type).ordinal())
				return true;
			else if(microblock.getType().getCuboids()[state.getBlock().getMetaFromState(state)].getSelectableCuboid().intersectsWith(subject)) if(this.ordinal() > ((EnumMicroblockType)type).ordinal())
			{
				if(subject.maxY <= 0.5D && type == SLAB && this == PANEL) return false;
			}
			else if(this.ordinal() == ((EnumMicroblockType)type).ordinal()) return index1 > index2;
		}
		return false;
	}
	
	@Override
	public AxisAlignedBB getAABBFromFacings(EnumFacing... facings)
	{
		if(facings.length == 3) return facingsToAABB.get(facings[0]).get(facings[1]).get(facings[2]).getLeft();
		if(facings.length == 6) return facingsToAABB.get(facings[0]).get(facings[2]).get(facings[4]).getRight().get(facings[1]).get(facings[3]).get(facings[5]).getLeft();
		if(facings.length == 9) return facingsToAABB.get(facings[0]).get(facings[3]).get(facings[6]).getRight().get(facings[1]).get(facings[4]).get(facings[7]).getRight().get(facings[2]).get(facings[5]).get(facings[8]);
		return null;
	}
	
	@Override
	public EnumPlaceType getPlaceType()
	{
		return this.type;
	}
	
	public Boolean overrides(IMicroblockType type)
	{
		if(type instanceof EnumMicroblockType) return this.getPlaceType().ordinal() > ((EnumMicroblockType)type).getPlaceType().ordinal() || this.ordinal() < ((EnumMicroblockType)type).ordinal();
		return false;
	}
	
	public BlockPos getRelativePos(EnumFacing... facings)
	{
		AxisAlignedBB aabb = this.getAABBFromFacings(facings);
		if(facings.length == 3) return new BlockPos((aabb.maxX * 2) - 1, (aabb.maxY * 2) - 1, (aabb.maxZ * 2) - 1);
		if(facings.length == 6) return new BlockPos((aabb.maxX * 4) - 1, (aabb.maxY * 4) - 1, (aabb.maxZ * 4) - 1);
		if(facings.length == 9) return new BlockPos((aabb.maxX * 8) - 1, (aabb.maxY * 8) - 1, (aabb.maxZ * 8) - 1);
		return BlockPos.ORIGIN;
	}
	
	private static AxisAlignedBB getAABB(EnumFacing facingX, EnumFacing facingY, EnumFacing facingZ, Double o)
	{
		return getAABB(facingX, facingY, facingZ, o, o, o, o);
	}
	
	private static AxisAlignedBB getAABB(EnumFacing facingX, EnumFacing facingY, EnumFacing facingZ, Double o, AxisAlignedBB base)
	{
		return getAABB(facingX, facingY, facingZ, o, base.minX + o, base.minY + o, base.minZ + o);
	}
	
	private static AxisAlignedBB getAABB(EnumFacing facingX, EnumFacing facingY, EnumFacing facingZ, Double o, Double ox, Double oy, Double oz)
	{
		return new AxisAlignedBB(ox, oy, oz, ox + (o * facingX.getAxisDirection().getOffset()), oy + (o * facingY.getAxisDirection().getOffset()), oz + (o * facingZ.getAxisDirection().getOffset()));
	}
	
	static
	{
		facingsToAABB = new HashMap<EnumFacing, HashMap<EnumFacing, HashMap<EnumFacing, Pair<AxisAlignedBB, HashMap<EnumFacing, HashMap<EnumFacing, HashMap<EnumFacing, Pair<AxisAlignedBB, HashMap<EnumFacing, HashMap<EnumFacing, HashMap<EnumFacing, AxisAlignedBB>>>>>>>>>>>();
		Double o8 = 0.125D;
		EnumFacing[] xs = new EnumFacing[]
		{
				EnumFacing.WEST, EnumFacing.EAST
		};
		EnumFacing[] ys = new EnumFacing[]
		{
				EnumFacing.DOWN, EnumFacing.UP
		};
		EnumFacing[] zs = new EnumFacing[]
		{
				EnumFacing.NORTH, EnumFacing.SOUTH
		};
		for(EnumFacing x1 : xs)
			for(EnumFacing y1 : ys)
				for(EnumFacing z1 : zs)
				{
					AxisAlignedBB aabb1 = getAABB(x1, y1, z1, 0.5D);
					if(!facingsToAABB.containsKey(x1)) facingsToAABB.put(x1, new HashMap<EnumFacing, HashMap<EnumFacing, Pair<AxisAlignedBB, HashMap<EnumFacing, HashMap<EnumFacing, HashMap<EnumFacing, Pair<AxisAlignedBB, HashMap<EnumFacing, HashMap<EnumFacing, HashMap<EnumFacing, AxisAlignedBB>>>>>>>>>>());
					if(!facingsToAABB.get(x1).containsKey(y1)) facingsToAABB.get(x1).put(y1, new HashMap<EnumFacing, Pair<AxisAlignedBB, HashMap<EnumFacing, HashMap<EnumFacing, HashMap<EnumFacing, Pair<AxisAlignedBB, HashMap<EnumFacing, HashMap<EnumFacing, HashMap<EnumFacing, AxisAlignedBB>>>>>>>>>());
					if(!facingsToAABB.get(x1).get(y1).containsKey(z1)) facingsToAABB.get(x1).get(y1).put(z1, Pair.of(aabb1, new HashMap<EnumFacing, HashMap<EnumFacing, HashMap<EnumFacing, Pair<AxisAlignedBB, HashMap<EnumFacing, HashMap<EnumFacing, HashMap<EnumFacing, AxisAlignedBB>>>>>>>()));
					for(EnumFacing x2 : xs)
						for(EnumFacing y2 : ys)
							for(EnumFacing z2 : zs)
							{
								AxisAlignedBB aabb2 = getAABB(x2, y2, z2, 0.25D, aabb1);
								if(!facingsToAABB.get(x1).get(y1).get(z1).getRight().containsKey(x2)) facingsToAABB.get(x1).get(y1).get(z1).getRight().put(x2, new HashMap<EnumFacing, HashMap<EnumFacing, Pair<AxisAlignedBB, HashMap<EnumFacing, HashMap<EnumFacing, HashMap<EnumFacing, AxisAlignedBB>>>>>>());
								if(!facingsToAABB.get(x1).get(y1).get(z1).getRight().get(x2).containsKey(y2)) facingsToAABB.get(x1).get(y1).get(z1).getRight().get(x2).put(y2, new HashMap<EnumFacing, Pair<AxisAlignedBB, HashMap<EnumFacing, HashMap<EnumFacing, HashMap<EnumFacing, AxisAlignedBB>>>>>());
								if(!facingsToAABB.get(x1).get(y1).get(z1).getRight().get(x2).get(y2).containsKey(z2)) facingsToAABB.get(x1).get(y1).get(z1).getRight().get(x2).get(y2).put(z2, Pair.of(aabb2, new HashMap<EnumFacing, HashMap<EnumFacing, HashMap<EnumFacing, AxisAlignedBB>>>()));
								for(EnumFacing x3 : xs)
									for(EnumFacing y3 : ys)
										for(EnumFacing z3 : zs)
										{
											if(!facingsToAABB.get(x1).get(y1).get(z1).getRight().get(x2).get(y2).get(z2).getRight().containsKey(x3)) facingsToAABB.get(x1).get(y1).get(z1).getRight().get(x2).get(y2).get(z2).getRight().put(x3, new HashMap<EnumFacing, HashMap<EnumFacing, AxisAlignedBB>>());
											if(!facingsToAABB.get(x1).get(y1).get(z1).getRight().get(x2).get(y2).get(z2).getRight().get(x3).containsKey(y3)) facingsToAABB.get(x1).get(y1).get(z1).getRight().get(x2).get(y2).get(z2).getRight().get(x3).put(y3, new HashMap<EnumFacing, AxisAlignedBB>());
											if(!facingsToAABB.get(x1).get(y1).get(z1).getRight().get(x2).get(y2).get(z2).getRight().get(x3).get(y3).containsKey(z3)) facingsToAABB.get(x1).get(y1).get(z1).getRight().get(x2).get(y2).get(z2).getRight().get(x3).get(y3).put(z3, getAABB(x3, y3, z3, 0.125D, aabb2));
										}
							}
				}
		facingComboList = new EnumFacingArray[8];
		Integer i = 0;
		for(Integer y = 0; y < 2; y++)
			for(Integer z = 0; z < 2; z++)
				for(Integer x = 0; x < 2; x++)
					facingComboList[i++] = new EnumFacingArray(EnumFacing.values()[x + 4], EnumFacing.values()[y], EnumFacing.values()[z + 2]);
	}
	
	public static enum EnumPlaceType
	{
		LAYERED,
		AXISED,
		CORNERED;
	}
}
