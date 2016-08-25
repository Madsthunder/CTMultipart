package continuum.api.multipart;

import java.util.List;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;

public class MultipartEvent extends WorldEvent
{
	public MultipartEvent(TileEntityMultiblock source)
	{
		super(source.getWorld());
		this.source = source;
	}
	
	private TileEntityMultiblock source;
	
	public BlockPos getPos()
	{
		return this.getSource().getPos();
	}
	
	public TileEntityMultiblock getSource()
	{
		return this.source;
	}
	
	public static class AABBExceptionsEvent extends MultipartEvent
	{
		public List<AxisAlignedBB> allowed;
		private Multipart multipart;
		private AxisAlignedBB box;
		
		public AABBExceptionsEvent(TileEntityMultiblock source, List<AxisAlignedBB> allowed, Multipart multipart, AxisAlignedBB box)
		{
			super(source);
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