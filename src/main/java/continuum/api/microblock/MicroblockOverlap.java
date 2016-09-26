package continuum.api.microblock;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;

public final class MicroblockOverlap extends Pair<Microblock, Microblock> implements IForgeRegistryEntry<MicroblockOverlap>
{
	public static final ResourceLocation OVERLAPREGISTRY = new ResourceLocation("ctmultipart", "microblockoverlapregistry");
	private final Microblock microblock;
	private final Microblock overlapped;
	private final Predicate<Pair> predicate;
	
	public MicroblockOverlap(Microblock microblock, Microblock overlapped)
	{
		this(microblock, overlapped, Predicates.alwaysTrue());
	}
	
	public MicroblockOverlap(Microblock microblock, Microblock overlapped, Predicate<Pair> predicate)
	{
		this.microblock = microblock;
		this.overlapped = overlapped;
		this.predicate = predicate;
	}
	
	public Microblock getMicroblock()
	{
		return this.microblock;
	}
	
	public Microblock getOverlapped()
	{
		return this.overlapped;
	}
	
	public boolean overlaps(Object par0, Object par1)
	{
		return this.predicate.apply(Pair.of(par0, par1));
	}
	
	public boolean isOpposite(MicroblockOverlap overlap)
	{
		return this.overlapped.equals(overlap.microblock) && this.microblock.equals(overlap.overlapped);
	}
	
	@Override
	public MicroblockOverlap setRegistryName(ResourceLocation location)
	{
		return this;
	}
	
	@Override
	public ResourceLocation getRegistryName()
	{
		return new ResourceLocation(this.microblock.getRegistryName().toString().replace(':', '|'), this.overlapped.getRegistryName().toString().replace(':', '|'));
	}
	
	@Override
	public Class<? super MicroblockOverlap> getRegistryType()
	{
		return MicroblockOverlap.class;
	}

	@Override
	public Microblock setValue(Microblock value)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Microblock getLeft()
	{
		return this.microblock;
	}

	@Override
	public Microblock getRight()
	{
		return this.overlapped;
	}
}
