package continuum.multipart.renderer;

import continuum.api.multipart.MultipartInfo;
import continuum.api.multipart.TileEntityMultiblock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.RayTraceResult;

public class MultipartTESR extends TileEntitySpecialRenderer<TileEntityMultiblock>
{
	public final Minecraft minecraft;
	public final BlockRendererDispatcher dispatcher;
	public final TextureAtlasSprite[] destroyStages = new TextureAtlasSprite[10];
	
	public MultipartTESR()
	{
		this.minecraft = Minecraft.getMinecraft();
		this.dispatcher = this.minecraft.getBlockRendererDispatcher();
	}
	
	@Override
	public void renderTileEntityAt(TileEntityMultiblock multiblock, double x, double y, double z, float partialTicks, int destroyStage)
	{
		GlStateManager.pushMatrix();
		TileEntitySpecialRenderer tesr;
		TileEntity entity;
		for(MultipartInfo info : multiblock)
			if(info.hasTileEntity() && (tesr = TileEntityRendererDispatcher.instance.getSpecialRenderer(entity = info.getTileEntity())) != null)
				tesr.renderTileEntityAt(entity, x, y, z, partialTicks, 0);
		GlStateManager.popMatrix();
	}
	
	@Override
	public void renderTileEntityFast(TileEntityMultiblock multipart, double x, double y, double z, float partialTicks, int destroyStage, VertexBuffer buffer)
	{
		/**World world = multipart.getWorld();
		BlockPos pos = multipart.getPos();
		List<MultipartInfo> infoList = multipart.getAllData();
		MultipartInfo dataToDamage = null;
		if(destroyStage >= 0 && this.minecraft.objectMouseOver.hitInfo instanceof MultipartInfo)
			dataToDamage = (MultipartInfo)this.minecraft.objectMouseOver.hitInfo;
		buffer.setTranslation(x - pos.getX(), y - pos.getY(), z - pos.getZ());
		for(Integer i = 0; i < infoList.size(); i++)
		{
		    MultipartInfo info = infoList.get(infoList.size() - 1 - i);
		    IBlockState state = info.getActualState();
		    IBakedModel model = this.dispatcher.getModelForState(state instanceof MultiblockStateImpl ? ((MultiblockStateImpl)state).getImplementation() : state);
		    state = info.getExtendedState();
		    if(state != null && model != null)
		    {	
		        this.dispatcher.getBlockModelRenderer().renderModel(world, model, state, pos, buffer, true);
		        if(info == dataToDamage)
		        	this.dispatcher.getBlockModelRenderer().renderModel(world, ForgeHooksClient.getDamageModel(model, this.minecraft.getTextureMapBlocks().getAtlasSprite("minecraft:blocks/destroy_stage_" + destroyStage), state, world, pos), state, pos, buffer, true);
		    }
		    
		}
		GlStateManager.pushMatrix();
		TileEntity entity;
		TileEntitySpecialRenderer renderer;
		for(MultipartInfo info : infoList)
			if(info.hasTileEntity())
		    	if((entity = info.getTileEntity()).hasFastRenderer())
		        	if((renderer = TileEntityRendererDispatcher.instance.getSpecialRenderer(entity)) != null)
		        		System.out.println(renderer);
		        		//renderer.renderTileEntityFast(entity, x, y, z, partialTicks, destroyStage, buffer);
		GlStateManager.popMatrix();*/
	}
}
