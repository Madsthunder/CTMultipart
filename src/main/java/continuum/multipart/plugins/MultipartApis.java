package continuum.multipart.plugins;

import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

import continuum.api.microblock.Microblock;
import continuum.api.microblock.texture.MicroblockMaterial;
import continuum.api.multipart.Multipart;
import continuum.api.multipart.MultipartState;
import continuum.essentials.mod.APIMethodReflectable;
import continuum.essentials.mod.MirrorType;
import continuum.multipart.multiparts.MultipartMicroblock;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry;

/**
 * NEVER reference to this class.
 */
public class MultipartApis
{
	public static final IForgeRegistry<Multipart> multipartRegistry = null;// Registries.createRegistry(Multipart.class,
																			// 0,
																			// Integer.MAX_VALUE
																			// >>
																			// 5);
	public static final FMLControlledNamespacedRegistry<MicroblockMaterial> microblockTextureRegistry = null;
	public static final IForgeRegistry<Microblock> microblockRegistry = null;
	private static final Map<Microblock, Map<Microblock, Predicate<Pair<MultipartState<MultipartMicroblock>, MultipartState<MultipartMicroblock>>>>> overlaps = Maps.newHashMap();
	
	@APIMethodReflectable(clasz = "continuum.api.multipart.MultipartApi", method = "getMultipartRegistry", type = MirrorType.REPLACE_METHOD)
	public static IForgeRegistry<Multipart> getMultipartRegistry()
	{
		return multipartRegistry;
	}
	
	@APIMethodReflectable(clasz = "continuum.api.multipart.MultipartApi", method = "apiActive", type = MirrorType.REPLACE_METHOD)
	public static boolean multipartApiActive()
	{
		return true;
	}
	
	@APIMethodReflectable(clasz = "continuum.api.microblock.MicroblockApi", method = "getMicroblockRegistry", type = MirrorType.REPLACE_METHOD)
	public static IForgeRegistry<Microblock> getMicroblockRegistry()
	{
		return microblockRegistry;
	}
	
	@APIMethodReflectable(clasz = "continuum.api.microblock.texture.MicroblockMaterialApi", method = "getMicroblockMaterialRegistry", type = MirrorType.REPLACE_METHOD)
	public static FMLControlledNamespacedRegistry<MicroblockMaterial> getMicroblockMaterialRegistry()
	{
		return microblockTextureRegistry;
	}
	
	@APIMethodReflectable(clasz = "continuum.api.microblock.texture.MicroblockMaterialApi", method = "apiActive", type = MirrorType.REPLACE_METHOD)
	public static boolean microblockTextureApiActive()
	{
		return true;
	}
	
	@APIMethodReflectable(clasz = "continuum.api.microblock.compat.MultipartCompat", method = "compatActive", type = MirrorType.REPLACE_METHOD)
	public static boolean compatActive()
	{
		return true;
	}
	
	@APIMethodReflectable(clasz = "continuum.api.microblock.compat.MultipartCompat", method = "addMicroblockOverlap", type = MirrorType.REPLACE_RETURN, extras = { "params={ 0, 1, 2 }" })
	public static boolean addMicroblockOverlap(Microblock toOverlap, Microblock microblock, Predicate<Pair<MultipartState<MultipartMicroblock>, MultipartState<MultipartMicroblock>>> predicate)
	{
		Map<Microblock, Predicate<Pair<MultipartState<MultipartMicroblock>, MultipartState<MultipartMicroblock>>>> conflicting = overlaps.get(microblock);
		if(conflicting != null)
			conflicting.remove(toOverlap);
		Map<Microblock, Predicate<Pair<MultipartState<MultipartMicroblock>, MultipartState<MultipartMicroblock>>>> map = overlaps.get(toOverlap);
		if(map == null)
			map = Maps.newHashMap();
		else
			overlaps.remove(toOverlap);
		map.put(microblock, predicate);
		overlaps.put(toOverlap, map);
		return true;
	}
	
	@APIMethodReflectable(clasz = "continuum.api.microblock.compat.MultipartCompat", method = "microblockOverlaps", type = MirrorType.REPLACE_RETURN, extras = { "params={ 0, 1 }" })
	public static boolean microblockOverlaps(MultipartState<MultipartMicroblock> subject, MultipartState<MultipartMicroblock> info)
	{
		Map<Microblock, Predicate<Pair<MultipartState<MultipartMicroblock>, MultipartState<MultipartMicroblock>>>> map = overlaps.get(subject.getMultipart().getMicroblock());
		if(map != null)
		{
			Predicate<Pair<MultipartState<MultipartMicroblock>, MultipartState<MultipartMicroblock>>> predicate = map.get(info.getMultipart());
			if(predicate != null)
				return predicate.apply(Pair.of(subject, info));
		}
		return false;
	}
}
