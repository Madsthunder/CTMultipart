package continuum.multipart.client.models;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.lang3.tuple.Triple;

import com.google.common.collect.Maps;

import continuum.multipart.enums.EnumFacingArray;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3i;

public interface ModelX extends Iterable<Triple<Boolean, AxisAlignedBB, Vec3i>>
{
	public Triple<Boolean, AxisAlignedBB, Vec3i> get(EnumFacingArray... arrays);
	
	public Triple<Boolean, AxisAlignedBB, Vec3i> setTo(Boolean value, EnumFacingArray... arrays);
	
	public void setAllTo(Boolean value);
	
	public Integer getEFASize();
}
