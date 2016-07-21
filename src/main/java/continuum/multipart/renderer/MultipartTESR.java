package continuum.multipart.renderer;

import java.util.List;

import continuum.api.multipart.MultipartInfo;
import continuum.api.multipart.TileEntityMultiblock;
import continuum.api.multipart.state.MultiblockStateImpl;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;

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
	public void renderTileEntityAt(TileEntityMultiblock multipart, double x, double y, double z, float partialTicks, int destroyStage)
	{
		GlStateManager.pushAttrib();
		GlStateManager.pushMatrix();
		World world = multipart.getWorld();
		BlockPos pos = multipart.getPos();
		/**Tessellator tess = Tessellator.getInstance();
		VertexBuffer vb = tess.getBuffer();
		this.bindTexture(TextureMap.locationBlocksTexture);
		RenderHelper.disableStandardItemLighting();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableBlend();
		if (Minecraft.isAmbientOcclusionEnabled())
		{
		    GlStateManager.shadeModel(7425);
		}
		else
		{
		    GlStateManager.shadeModel(7424);
		}
		vb.setTranslation(x - pos.getX(), y - pos.getY(), z - pos.getZ());
		vb.setTranslation(0, 0, 0);
		RenderHelper.enableStandardItemLighting();
		GlStateManager.popMatrix();*/
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
