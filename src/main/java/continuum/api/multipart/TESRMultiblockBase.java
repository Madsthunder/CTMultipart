package continuum.api.multipart;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public class TESRMultiblockBase<T extends TileEntity> extends TileEntitySpecialRenderer<T>
{
	private static final Minecraft mc = Minecraft.getMinecraft();
	private final BlockRendererDispatcher dispatcher = mc.getBlockRendererDispatcher();
	public final TextureAtlasSprite[] destroyStages = new TextureAtlasSprite[10];
	private static MultipartStateList renderingInfoList;
	
	@Override
	public void renderTileEntityAt(T entity, double x, double y, double z, float partialTicks, int destroyStage)
	{
		if(entity.hasCapability(MultipartStateList.MULTIPARTINFOLIST, null))
		{
			GlStateManager.pushMatrix();
			TileEntitySpecialRenderer tesr;
			TileEntity entity1;
			for(MultipartState info : entity.getCapability(MultipartStateList.MULTIPARTINFOLIST, null))
				if(info.hasTileEntity() && (tesr = TileEntityRendererDispatcher.instance.getSpecialRenderer(entity1 = info.getTileEntity())) != null)
					tesr.renderTileEntityAt(entity1, x, y, z, partialTicks, 0);
			GlStateManager.popMatrix();
		}
	}
	
	@Override
	public final boolean isGlobalRenderer(T entity)
	{
		renderingInfoList = entity.hasCapability(MultipartStateList.MULTIPARTINFOLIST, null) ? entity.getCapability(MultipartStateList.MULTIPARTINFOLIST, null) : null;
		return false;
	}
	
	public static MultipartStateList getRenderingInfoList()
	{
		return renderingInfoList;
	}
}
