package continuum.multipart.client.model;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import com.google.common.collect.Maps;

import continuum.multipart.enums.EnumFacingArray;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3i;

public class Model4x implements ModelX
{
	private final HashMap<Pair<EnumFacingArray, EnumFacingArray>, Triple<Boolean, AxisAlignedBB, Vec3i>> map = Maps.newHashMap();
	
	@Override
	public Iterator<Triple<Boolean, AxisAlignedBB, Vec3i>> iterator()
	{
		return this.map.values().iterator();
	}
	
	public Triple<Boolean, AxisAlignedBB, Vec3i> get(EnumFacingArray... arrays)
	{
		return this.map.get(Pair.of(arrays[0], arrays[1]));
	}
	
	public Triple<Boolean, AxisAlignedBB, Vec3i> setTo(Boolean value, EnumFacingArray... arrays)
	{
		Triple<Boolean, AxisAlignedBB, Vec3i> t1 = this.get(arrays);
		Triple<Boolean, AxisAlignedBB, Vec3i> t2 = Triple.of(value, t1.getMiddle(), t1.getRight());
		map.replace(Pair.of(arrays[0], arrays[1]), t1, t2);
		return t2;
	}
	
	public void setAllTo(Boolean value)
	{
		for(EnumFacingArray array1 : EnumFacingArray.getIterable())
			for(EnumFacingArray array2 : EnumFacingArray.getIterable())
				this.setTo(value, array1, array2);
	}
	
	public Integer getEFASize()
	{
		return 2;
	}
}
