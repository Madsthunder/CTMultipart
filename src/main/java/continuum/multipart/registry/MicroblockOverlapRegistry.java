package continuum.multipart.registry;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.realmsclient.util.Pair;

import continuum.api.microblock.MicroblockOverlap;
import continuum.api.multipart.MultipartState;
import continuum.multipart.multiparts.MultipartMicroblock;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.IForgeRegistry;

public class MicroblockOverlapRegistry implements IForgeRegistry<MicroblockOverlap>
{
	public static final MicroblockOverlapRegistry INSTANCE = new MicroblockOverlapRegistry();
	private final BiMap<ResourceLocation, MicroblockOverlap> overlaps = HashBiMap.create();
	
	private MicroblockOverlapRegistry()
	{
	}
	
	@Override
	public Iterator<MicroblockOverlap> iterator()
	{
		return overlaps.values().iterator();
	}
	
	@Override
	public Class<MicroblockOverlap> getRegistrySuperType()
	{
		return MicroblockOverlap.class;
	}
	
	@Override
	public void register(MicroblockOverlap value)
	{
		for(Entry<ResourceLocation, MicroblockOverlap> entry : Sets.newHashSet(this.overlaps.entrySet()))
			if(entry.getValue().isOpposite(value))
				this.overlaps.remove(entry.getValue());
		this.overlaps.put(value.getRegistryName(), value);
	}
	
	@Override
	public void registerAll(MicroblockOverlap... values)
	{
		for(MicroblockOverlap value : values)
			this.register(value);
	}
	
	@Override
	public boolean containsKey(ResourceLocation key)
	{
		return this.overlaps.containsKey(key);
	}
	
	@Override
	public boolean containsValue(MicroblockOverlap value)
	{
		return this.overlaps.containsValue(value);
	}
	
	@Override
	public MicroblockOverlap getValue(ResourceLocation key)
	{
		return this.overlaps.get(key);
	}
	
	@Override
	public ResourceLocation getKey(MicroblockOverlap value)
	{
		return this.overlaps.inverse().get(value);
	}
	
	@Override
	public Set<ResourceLocation> getKeys()
	{
		return Sets.newHashSet(this.overlaps.keySet());
	}
	
	@Override
	public List<MicroblockOverlap> getValues()
	{
		return Lists.newArrayList(this.overlaps.values());
	}
	
	@Override
	public Set<Entry<ResourceLocation, MicroblockOverlap>> getEntries()
	{
		return Sets.newHashSet(this.overlaps.entrySet());
	}
	
	@Override
	public <T> T getSlaveMap(ResourceLocation slaveMapName, Class<T> type)
	{
		return null;
	}
	
	public boolean overlaps(MultipartState<MultipartMicroblock> microblock, MultipartState<MultipartMicroblock> overlapped)
	{
		if(overlapped.canRenderInLayer(BlockRenderLayer.TRANSLUCENT) && !microblock.canRenderInLayer(BlockRenderLayer.TRANSLUCENT))
			return true;
		if((overlapped.canRenderInLayer(BlockRenderLayer.CUTOUT) || overlapped.canRenderInLayer(BlockRenderLayer.CUTOUT_MIPPED)) && !microblock.canRenderInLayer(BlockRenderLayer.CUTOUT) && !microblock.canRenderInLayer(BlockRenderLayer.CUTOUT_MIPPED))
			return true;
		String microblockName = microblock.getMultipart().getMicroblock().getRegistryName().toString().replace(':', '|');
		String overlappedName = overlapped.getMultipart().getMicroblock().getRegistryName().toString().replace(':', '|');
		MicroblockOverlap overlap = this.overlaps.get(new ResourceLocation(microblockName, overlappedName));
		if(overlap != null)
			return overlap.overlaps(microblock, overlapped);
		overlap = this.overlaps.get(new ResourceLocation(overlappedName, microblockName));
				if(overlap != null)
					return !overlap.overlaps(overlapped, microblock);
		return microblock.getIndex() > overlapped.getIndex();
	}
}
