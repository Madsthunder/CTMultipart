package continuum.multipart.client.model;

import java.util.Collection;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import continuum.api.multipart.MultiblockStateImpl;
import continuum.api.multipart.MultipartState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;

public class ModelMultiblock implements IModel
{
	public static final Minecraft mc = Minecraft.getMinecraft();
	
	@Override
	public Collection<ResourceLocation> getDependencies()
	{
		return Sets.newHashSet();
	}
	
	@Override
	public Collection<ResourceLocation> getTextures()
	{
		return Sets.newHashSet();
	}
	
	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> function)
	{
		return new BakedModelMultipart(format, function);
	}
	
	@Override
	public IModelState getDefaultState()
	{
		return ModelRotation.X0_Y0;
	}
	
	private static class BakedModelMultipart implements IBakedModel
	{
		private final VertexFormat format;
		private final Function<ResourceLocation, TextureAtlasSprite> function;
		
		public BakedModelMultipart(VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> function)
		{
			this.format = format;
			this.function = function;
		}
		
		@Override
		public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand)
		{
			List<BakedQuad> quads = Lists.newArrayList();
			if(state instanceof MultiblockStateImpl)
			{
				MultiblockStateImpl impl = (MultiblockStateImpl)state;
				IBakedModel model;
				for(MultipartState info : impl.getInfoList().getInfoToRenderInLayer(MinecraftForgeClient.getRenderLayer()))
					if((model = mc.getBlockRendererDispatcher().getModelForState(info.getActualState())) != null)
						quads.addAll(model.getQuads(info.getExtendedState(true), side, rand));
			}
			return quads;
		}
		
		@Override
		public boolean isAmbientOcclusion()
		{
			return true;
		}
		
		@Override
		public boolean isGui3d()
		{
			return false;
		}
		
		@Override
		public boolean isBuiltInRenderer()
		{
			return false;
		}
		
		@Override
		public TextureAtlasSprite getParticleTexture()
		{
			return mc.getBlockRendererDispatcher().getModelForState(Blocks.STONE.getDefaultState()).getParticleTexture();
		}
		
		@Override
		public ItemCameraTransforms getItemCameraTransforms()
		{
			return ItemCameraTransforms.DEFAULT;
		}
		
		@Override
		public ItemOverrideList getOverrides()
		{
			return ItemOverrideList.NONE;
		}
	}
}
