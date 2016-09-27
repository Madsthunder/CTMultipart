package continuum.api.multipart;

import java.lang.reflect.Field;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.SimpleBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TESRMultiblockBase<T extends TileEntity> extends TileEntitySpecialRenderer<T>
{
	private static final Minecraft mc = Minecraft.getMinecraft();
	private final BlockRendererDispatcher dispatcher = mc.getBlockRendererDispatcher();
	private static final TextureAtlasSprite[] destroyStages = new TextureAtlasSprite[10];
	private static MultipartStateList renderingInfoList;
	
	public TESRMultiblockBase()
	{
		initTextures();
	}
	
	@Override
	public void renderTileEntityAt(T entity, double x, double y, double z, float partialTicks, int destroyStage)
	{
		if(entity.hasCapability(MultipartStateList.MULTIPARTINFOLIST, null))
		{
			if(destroyStage >= 0 && mc.objectMouseOver != null && mc.objectMouseOver.hitInfo instanceof MultipartState && mc.objectMouseOver.getBlockPos().equals(entity.getPos()))
			{
				World world = entity.getWorld();
				BlockPos pos = entity.getPos();
				Tessellator.getInstance().getBuffer().setTranslation(x - pos.getX(), y - pos.getY(), z - pos.getZ());
				MultipartState state = (MultipartState)mc.objectMouseOver.hitInfo;
				IBakedModel model = new SimpleBakedModel.Builder(state.getExtendedState(), this.dispatcher.getBlockModelShapes().getModelForState(state.getActualState()), destroyStages[destroyStage], entity.getPos()).makeBakedModel();
				System.out.println(model.getParticleTexture());
				this.dispatcher.getBlockModelRenderer().renderModel(entity.getWorld(), model, state.getActualState(), entity.getPos(), Tessellator.getInstance().getBuffer(), true);
				//this.dispatcher.renderBlockDamage(((MultipartState)mc.objectMouseOver.hitInfo).getActualState(), new BlockPos(x, y, z), destroyStages[destroyStage], entity.getWorld());
				Tessellator.getInstance().getBuffer().setTranslation(0, 0, 0);
			}
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
	
	public static void initTextures()
	{
		if(mc.getTextureMapBlocks() != null)
		{
			try
			{
				Field field = TextureMap.class.getDeclaredField("mapUploadedSprites");
				field.setAccessible(true);
				System.out.println(field.get(mc.getTextureMapBlocks()));
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			for(int i = 0; i < DESTROY_STAGES.length; i++)
				destroyStages[i] = mc.getTextureMapBlocks().getAtlasSprite(new ResourceLocation(DESTROY_STAGES[i].getResourceDomain(), DESTROY_STAGES[i].getResourcePath().replaceAll("textures/", "").replaceAll(".png", "")).toString());
		}
	}
}
