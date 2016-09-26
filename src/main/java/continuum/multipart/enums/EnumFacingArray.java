package continuum.multipart.enums;

import java.util.ArrayList;

import com.google.common.collect.Lists;

import net.minecraft.util.EnumFacing;

public class EnumFacingArray
{
	public final EnumFacing x;
	public final EnumFacing y;
	public final EnumFacing z;
	private static final ArrayList<EnumFacingArray> allArrays;
	
	public EnumFacingArray(EnumFacing x, EnumFacing y, EnumFacing z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public EnumFacing[] toArray()
	{
		return new EnumFacing[] { this.x, this.y, this.z };
	}
	
	public EnumFacing[] toArray(EnumFacingArray efa2)
	{
		return new EnumFacing[] { this.x, efa2.x, this.y, efa2.y, this.z, efa2.z };
	}
	
	public EnumFacing[] toArray(EnumFacingArray efa2, EnumFacingArray efa3)
	{
		return new EnumFacing[] { this.x, efa2.x, efa3.x, this.y, efa2.y, efa3.y, this.z, efa2.z, efa3.z };
	}
	
	public static Iterable<EnumFacingArray> getIterable()
	{
		return allArrays;
	}
	
	static
	{
		ArrayList<EnumFacingArray> allArrays1 = Lists.newArrayList();
		EnumFacing[] ev = EnumFacing.values();
		for(Integer x = 0; x < 2; x++)
			for(Integer y = 0; y < 2; y++)
				for(Integer z = 0; z < 2; z++)
					allArrays1.add(new EnumFacingArray(ev[x + 4], ev[y], ev[z + 2]));
		allArrays = allArrays1;
	}
}
