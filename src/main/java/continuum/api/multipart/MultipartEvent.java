package continuum.api.multipart;

import java.util.List;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;

public class MultipartEvent extends WorldEvent
{
	public MultipartEvent(MultipartStateList infoList)
	{
		super(infoList.getWorld());
		this.infoList = infoList;
	}
	
	private MultipartStateList infoList;
	
	public BlockPos getPos()
	{
		return this.getInfoList().getPos();
	}
	
	public MultipartStateList getInfoList()
	{
		return this.infoList;
	}
	
	public static class AABBExceptionsEvent extends MultipartEvent
	{
		public List<AxisAlignedBB> allowed;
		private Multipart multipart;
		private AxisAlignedBB box;
		
		public AABBExceptionsEvent(MultipartStateList infoList, List<AxisAlignedBB> allowed, Multipart multipart, AxisAlignedBB box)
		{
			super(infoList);
			this.allowed = allowed;
			this.multipart = multipart;
			this.box = box;
		}
		
		public Multipart getMultipart()
		{
			return this.multipart;
		}
		
		public AxisAlignedBB getBox()
		{
			return this.box;
		}
	}
}
