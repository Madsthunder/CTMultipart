package continuum.multipart.enums;

import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import continuum.api.microblock.Microblock;
import continuum.essentials.block.ICuboid;
import continuum.multipart.blocks.BlockAxised;
import continuum.multipart.blocks.BlockCornered;
import continuum.multipart.blocks.BlockLayered;
import continuum.multipart.mod.Multipart_OH;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DefaultMicroblock extends Microblock
{
	public static final DefaultMicroblock SLAB = new DefaultMicroblock("Slab", SlabCuboid.values(), BlockLayered.class);
	public static final DefaultMicroblock PANEL = new DefaultMicroblock("Panel", PanelCuboid.values(), BlockLayered.class);
	public static final DefaultMicroblock COVER = new DefaultMicroblock("Cover", CoverCuboid.values(), BlockLayered.class);
	public static final DefaultMicroblock PILLAR = new DefaultMicroblock("Pillar", PillarCuboid.values(), BlockAxised.class);
	public static final DefaultMicroblock POST = new DefaultMicroblock("Post", PostCuboid.values(), BlockAxised.class);
	public static final DefaultMicroblock STRIP = new DefaultMicroblock("Strip", StripCuboid.values(), BlockAxised.class);
	public static final DefaultMicroblock NOTCH = new DefaultMicroblock("Notch", NotchCuboid.values(), BlockCornered.class);
	public static final DefaultMicroblock CORNER = new DefaultMicroblock("Corner", CornerCuboid.values(), BlockCornered.class);
	public static final DefaultMicroblock NOOK = new DefaultMicroblock("Nook", NookCuboid.values(), BlockCornered.class);
	public static final ImmutableSet<ILayeredCuboid> LAYERED_CUBOIDS = new ImmutableSet.Builder<ILayeredCuboid>().add(SlabCuboid.values()).add(PanelCuboid.values()).add(CoverCuboid.values()).build();
	public static final ImmutableSet<IAxisedCuboid> AXISED_CUBOIDS = new ImmutableSet.Builder<IAxisedCuboid>().add(PillarCuboid.values()).add(PostCuboid.values()).add(StripCuboid.values()).build();
	public static final ImmutableSet<ICorneredCuboid> CORNERED_CUBOIDS = new ImmutableSet.Builder<ICorneredCuboid>().add(NotchCuboid.values()).add(CornerCuboid.values()).add(NookCuboid.values()).build();
	public static final ImmutableList<DefaultMicroblock> defaultMicroblocks = new ImmutableList.Builder<DefaultMicroblock>().add(SLAB, PANEL, COVER, PILLAR, POST, STRIP, NOTCH, CORNER, NOOK).build();
	public static final Multipart_OH holder = Multipart_OH.INSTANCE;
	private final String name;
	private final List<ICuboid> cuboids;
	private final Block block;
	
	private DefaultMicroblock(String name, ICuboid[] cuboids, Class<? extends Block> clasz)
	{
		this.name = name;
		this.cuboids = Lists.newArrayList(cuboids);
		Block block = null;
		try
		{
			block = clasz.getConstructor(Microblock.class).newInstance(this);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		this.block = block;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public List<ICuboid> getCuboids()
	{
		return this.cuboids;
	}
	
	public List<AxisAlignedBB> getRenderBoxes(IBlockState state)
	{
		return Lists.newArrayList(this.getCuboids().get(state.getBlock().getMetaFromState(state)).getSelectableCuboid());
	}
	
	@Override
	public Block getBlock()
	{
		return this.block;
	}
	
	@Override
	public ResourceLocation getRegistryName()
	{
		return new ResourceLocation(holder.getModid(), "microblock" + this.getName());
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public ItemCameraTransforms getCameraTransforms()
	{
		ItemCameraTransforms defaultt = super.getCameraTransforms();
		AxisAlignedBB aabb = null;
		for(AxisAlignedBB box : this.getRenderBoxes(this.getBlock().getDefaultState()))
			aabb = aabb == null ? box : aabb.union(box);
		float x = (float)(aabb.maxX - aabb.minX) / 32;
		float y = (float)(aabb.maxY - aabb.minY) / 32;
		float z = (float)(aabb.maxZ - aabb.minZ) / 32;
		return new ItemCameraTransforms(defaultt.thirdperson_left, defaultt.thirdperson_right, defaultt.firstperson_left, defaultt.firstperson_right, defaultt.head, new ItemTransformVec3f(new Vector3f(30, 225, 0), isLayered(this) ? new Vector3f(0.12F, -0.075F, 0) : isAxised(this) ? new Vector3f() : isCornered(this) ? new Vector3f(-0.2F, 0.15F, 0) : new Vector3f(), new Vector3f(0.625F, 0.625F, 0.625F)), defaultt.ground, defaultt.fixed);
	}
	
	@Override
	public void addExceptionsToList(ICuboid cuboid, List<AxisAlignedBB> list)
	{
		if(isLayered(this) && cuboid instanceof ILayeredCuboid)
		{
			ILayeredCuboid layered = (ILayeredCuboid)cuboid;
			Iterables.addAll(list, Iterables.transform(Iterables.filter(LAYERED_CUBOIDS, new Predicate<ILayeredCuboid>()
			{
				@Override
				public boolean apply(ILayeredCuboid cuboid1)
				{
					return cuboid1.getSide() != layered.getSide();
				}
			}), ICuboid.CUBOID_TO_SELECTABLE));
			Iterables.addAll(list, Iterables.transform(AXISED_CUBOIDS, ICuboid.CUBOID_TO_SELECTABLE));
		}
		else if(isAxised(this) && cuboid instanceof IAxisedCuboid)
		{
			IAxisedCuboid pillared = (IAxisedCuboid)cuboid;
			Iterables.addAll(list, Iterables.transform(Iterables.filter(AXISED_CUBOIDS, new Predicate<IAxisedCuboid>()
			{
				@Override
				public boolean apply(IAxisedCuboid cuboid1)
				{
					return pillared.isCentered() ? !pillared.getSelectableCuboid().intersectsWith(cuboid1.getSelectableCuboid()) : pillared.getAxis() != cuboid1.getAxis();
				}
			}), ICuboid.CUBOID_TO_SELECTABLE));
			Iterables.addAll(list, Iterables.transform(LAYERED_CUBOIDS, ICuboid.CUBOID_TO_SELECTABLE));
		}
	}
	
	public static boolean isLayered(Microblock microblock)
	{
		return microblock == SLAB || microblock == PANEL || microblock == COVER;
	}
	
	public static boolean isAxised(Microblock microblock)
	{
		return microblock == PILLAR || microblock == POST || microblock == STRIP;
	}
	
	public static boolean isCornered(Microblock microblock)
	{
		return microblock == NOTCH || microblock == CORNER || microblock == NOOK;
	}
	
	@Override
	public AxisAlignedBB getSelectionBox(IBlockState state)
	{
		return this.cuboids.get(this.getBlock().getMetaFromState(state)).getSelectableCuboid();
	}
	
	public List<AxisAlignedBB> getExceptions(ICuboid cuboid)
	{
		List<AxisAlignedBB> boxes = Lists.newArrayList();
		if(this != SLAB && this != PILLAR && this.getCuboids().contains(cuboid))
			if(isLayered(this) && LAYERED_CUBOIDS.contains(cuboid))
			{
				ILayeredCuboid lCuboid = (ILayeredCuboid)cuboid;
				for(ILayeredCuboid l : LAYERED_CUBOIDS)
					if(!SLAB.getCuboids().contains(l) && l.getSide() != lCuboid.getSide())
						boxes.add(l.getSelectableCuboid());
				for(IAxisedCuboid a : AXISED_CUBOIDS)
					if(a.getFacing1() != lCuboid.getSide() && a.getFacing2() != lCuboid.getSide())
						boxes.add(a.getSelectableCuboid());
			}
			else if(isAxised(this) && AXISED_CUBOIDS.contains(cuboid))
			{
				IAxisedCuboid aCuboid = (IAxisedCuboid)cuboid;
				for(ILayeredCuboid l : LAYERED_CUBOIDS)
					if(!SLAB.getCuboids().contains(l) && l.getSide() != aCuboid.getFacing1() && l.getSide() != aCuboid.getFacing2())
						boxes.add(l.getSelectableCuboid());
				for(IAxisedCuboid a : AXISED_CUBOIDS)
					if(a.getFacing1() != aCuboid.getFacing1() || a.getFacing2() != aCuboid.getFacing2())
						boxes.add(a.getSelectableCuboid());
			}
		return boxes;
	}
}
