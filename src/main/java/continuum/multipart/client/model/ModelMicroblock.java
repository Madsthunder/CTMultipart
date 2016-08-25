package continuum.multipart.client.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.Logger;
import org.lwjgl.util.vector.Vector3f;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import continuum.api.microblock.IMicroblock;
import continuum.api.microblock.MicroblockStateImpl;
import continuum.api.microblock.texture.MicroblockMaterial;
import continuum.api.microblock.texture.MicroblockMaterialApi;
import continuum.api.multipart.MultiblockStateImpl;
import continuum.essentials.mod.CTMod;
import continuum.multipart.enums.EnumMicroblockType;
import continuum.multipart.enums.EnumMicroblockType.EnumPlaceType;
import continuum.multipart.items.ItemMicroblock;
import continuum.multipart.mod.Multipart_EH;
import continuum.multipart.mod.Multipart_OH;
import continuum.multipart.multiparts.MultipartMicroblock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IRetexturableModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;

public class ModelMicroblock implements IModel
{
	public final Multipart_OH objectHolder;
	public final Logger logger;
	private static final FaceBakery bakery = new FaceBakery();
	public HashMap<String, IRetexturableModel> models = new HashMap<String, IRetexturableModel>();
	
	public ModelMicroblock(CTMod<Multipart_OH, Multipart_EH> mod)
	{
		this.objectHolder = mod.getObjectHolder();
		this.logger = mod.getLogger();
	}
	
	@Override
	public Collection<ResourceLocation> getDependencies()
	{
		return Lists.newArrayList();
	}
	
	@Override
	public Collection<ResourceLocation> getTextures()
	{
		ArrayList<ResourceLocation> textures = Lists.newArrayList();
		if(MicroblockMaterialApi.apiActive())
		for(MicroblockMaterial entry : MicroblockMaterialApi.getMicroblockMaterialRegistry())
		{
			textures.add(entry.getParticleLocation());
			for(EnumFacing direction : EnumFacing.values())
				textures.add(entry.getLocationFromDirection(direction));
		}
		return textures;
	}
	
	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> function)
	{
		return new BakedModelMicroblock(this, format, function);
	}
	
	private void createModels()
	{
		this.models.clear();
		for(EnumMicroblockType type : EnumMicroblockType.values())
		{
			String name = type.name().toLowerCase();
			IRetexturableModel model = null;
			try
			{
				IModel model1 = ModelLoaderRegistry.getModel(this.objectHolder.microblockLocations.get(name));
				if(model1 instanceof IRetexturableModel) model = (IRetexturableModel)model1;
			}
			catch(Exception e)
			{
				this.logger.warn("Could Not Find Model For \'" + type.getName() + "\'.");
			}
			if(model != null) this.models.put(name, model);
		}
	}
	
	@Override
	public IModelState getDefaultState()
	{
		return ModelRotation.X0_Y0;
	}
	
	private static class BakedModelMicroblock implements IBakedModel
	{
		private final VertexFormat format;
		private final Function<ResourceLocation, TextureAtlasSprite> function;
		private final MicroblockIOL iol;
		private final Pair<IMicroblock, List<BakedQuad>> itemData;
		
		public BakedModelMicroblock(ModelMicroblock rootClass, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> function)
		{
			this.format = format;
			this.function = function;
			this.itemData = null;
			this.iol = new MicroblockIOL(this);
		}
		
		public BakedModelMicroblock(BakedModelMicroblock baseModel, ItemStack stack)
		{
			this.format = baseModel.format;
			this.function = baseModel.function;
			this.iol = baseModel.iol;
			IBlockState state = ((ItemMicroblock)stack.getItem()).getRenderState();
			this.itemData = Pair.of((IMicroblock)state.getBlock(), this.createQuads((IMicroblock)state.getBlock(), state, null, MicroblockMaterial.readFromNBT(stack.getTagCompound())));
		}
		
		@Override
		public List<BakedQuad> getQuads(IBlockState state, EnumFacing facing, long random)
		{
			if(state instanceof MultiblockStateImpl) state = ((MultiblockStateImpl)state).getImplementation();
			if(this.itemData != null) return this.itemData.getRight();
			if(state instanceof MicroblockStateImpl && state.getBlock() instanceof IMicroblock) return this.createQuads((IMicroblock)state.getBlock(), state, facing, ((MicroblockStateImpl)state).entry);
			return Lists.newArrayList();
		}
		
		private List<BakedQuad> createQuads(IMicroblock microblock, IBlockState state, EnumFacing facing, MicroblockMaterial entry)
		{
			List<Triple<Boolean, AxisAlignedBB, BlockPos>> list = microblock.getRenderList(state, entry);
			return getQuads(facing, list, this.function.apply(entry.getLocationFromIndex(0)), this.function.apply(entry.getLocationFromIndex(1)), this.function.apply(entry.getLocationFromIndex(2)), this.function.apply(entry.getLocationFromIndex(3)), this.function.apply(entry.getLocationFromIndex(4)), this.function.apply(entry.getLocationFromIndex(5)));
		}
		
		public List<BakedQuad> getQuads(EnumFacing facing, List<Triple<Boolean, AxisAlignedBB, BlockPos>> list, TextureAtlasSprite... textures)
		{
			List<BakedQuad> quads = Lists.newArrayList();
			for(Triple<Boolean, AxisAlignedBB, BlockPos> t : list)
				if(t.getLeft())
				{
					for(EnumFacing facing1 : EnumFacing.values())
					{
						AxisAlignedBB aabb = getAABB(t.getMiddle(), facing1);
						Integer i = facing1.ordinal();
						if(!listContainsPos(t.getRight().offset(facing1), list)) quads.add(bakery.makeBakedQuad(minVec(aabb), maxVec(aabb), createPartFace(facing1, aabb, textures[i]), textures[i], facing1, ModelRotation.X0_Y0, null, true, true));
					}
				}
			return quads;
		}
		
		public static float[] getUVs(EnumFacing f, AxisAlignedBB aabb)
		{
			Axis axis = f.getAxis();
			if(axis.isVertical()) return new float[]
			{
					(float)aabb.minX, invertMin(EnumFacing.DOWN, f, aabb.minZ, aabb.maxZ), (float)aabb.maxX, invertMax(EnumFacing.DOWN, f, aabb.minZ, aabb.maxZ)
			};
			if(axis.isHorizontal()) if(axis == Axis.Z)
				return new float[]
				{
						invertMin(EnumFacing.NORTH, f, aabb.minX, aabb.maxX), invertMin(f, f, aabb.minY, aabb.maxY), invertMax(EnumFacing.NORTH, f, aabb.minX, aabb.maxX), invertMax(f, f, aabb.minY, aabb.maxY)
				};
			else if(axis == Axis.X) return new float[]
			{
					invertMin(EnumFacing.EAST, f, aabb.minZ, aabb.maxZ), invertMin(f, f, aabb.minY, aabb.maxY), invertMax(EnumFacing.EAST, f, aabb.minZ, aabb.maxZ), invertMax(f, f, aabb.minY, aabb.maxY)
			};
			return new float[4];
		}
		
		public static float invertMin(EnumFacing facingEquals, EnumFacing facingSubject, double minValue, double maxValue)
		{
			if(facingSubject == facingEquals) return (float)(16 - maxValue);
			return (float)minValue;
		}
		
		public static float invertMax(EnumFacing facingEquals, EnumFacing facingSubject, double minValue, double maxValue)
		{
			if(facingSubject == facingEquals) return (float)(16 - minValue);
			return (float)maxValue;
		}
		
		public static BlockPartFace createPartFace(EnumFacing facing, AxisAlignedBB aabb, TextureAtlasSprite texture)
		{
			return new BlockPartFace(facing, -1, texture.getIconName(), new BlockFaceUV(getUVs(facing, aabb), 0));
		}
		
		public static AxisAlignedBB getAABB(AxisAlignedBB aabb, EnumFacing facing)
		{
			aabb = new AxisAlignedBB(aabb.minX * 16, aabb.minY * 16, aabb.minZ * 16, aabb.maxX * 16, aabb.maxY * 16, aabb.maxZ * 16);
			if(facing == EnumFacing.DOWN) return new AxisAlignedBB(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.minY, aabb.maxZ);
			if(facing == EnumFacing.UP) return new AxisAlignedBB(aabb.minX, aabb.maxY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
			if(facing == EnumFacing.NORTH) return new AxisAlignedBB(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.minZ);
			if(facing == EnumFacing.SOUTH) return new AxisAlignedBB(aabb.minX, aabb.minY, aabb.maxZ, aabb.maxX, aabb.maxY, aabb.maxZ);
			if(facing == EnumFacing.WEST) return new AxisAlignedBB(aabb.minX, aabb.minY, aabb.minZ, aabb.minX, aabb.maxY, aabb.maxZ);
			if(facing == EnumFacing.EAST) return new AxisAlignedBB(aabb.maxX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
			return aabb;
		}
		
		public static Vector3f minVec(AxisAlignedBB aabb)
		{
			return new Vector3f((float)aabb.minX, (float)aabb.minY, (float)aabb.minZ);
		}
		
		public static Vector3f maxVec(AxisAlignedBB aabb)
		{
			return new Vector3f((float)aabb.maxX, (float)aabb.maxY, (float)aabb.maxZ);
		}
		
		public Boolean listContainsPos(BlockPos pos, List<Triple<Boolean, AxisAlignedBB, BlockPos>> list)
		{
			for(Triple<Boolean, AxisAlignedBB, BlockPos> triple : list)
				if(triple.getLeft() && triple.getRight().equals(pos)) return true;
			return false;
		}
		
		@Override
		public boolean isAmbientOcclusion()
		{
			return true;
		}
		
		@Override
		public boolean isGui3d()
		{
			return true;
		}
		
		@Override
		public boolean isBuiltInRenderer()
		{
			return false;
		}
		
		@Override
		public TextureAtlasSprite getParticleTexture()
		{
			return MultipartMicroblock.currentEntry == null ? Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite() : this.function.apply(MultipartMicroblock.currentEntry.getParticleLocation());
		}
		
		@Override
		public ItemCameraTransforms getItemCameraTransforms()
		{
			Object placeType = this.itemData == null ? null : this.itemData.getLeft().getType().getPlaceType();
			return new ItemCameraTransforms(new ItemTransformVec3f(new Vector3f(75, 45, 0), new Vector3f(0, 2.5F, 0), new Vector3f(0.375F, 0.375F, 0.375F)), new ItemTransformVec3f(new Vector3f(75, 45, 0), new Vector3f(0, 2.5F, 0), new Vector3f(0.375F, 0.375F, 0.375F)), new ItemTransformVec3f(new Vector3f(0, 225, 0), new Vector3f(), new Vector3f(0.4F, 0.4F, 0.4F)), new ItemTransformVec3f(new Vector3f(0, 45, 0), new Vector3f(), new Vector3f(0.4F, 0.4F, 0.4F)), ItemTransformVec3f.DEFAULT, new ItemTransformVec3f(new Vector3f(30, 225, 0), placeType == EnumPlaceType.LAYERED ? new Vector3f(0.12F, -0.075F, 0) : placeType == EnumPlaceType.AXISED ? new Vector3f() : placeType == EnumPlaceType.CORNERED ? new Vector3f(-0.2F, 0.15F, 0) : new Vector3f(), new Vector3f(0.625F, 0.625F, 0.625F)), new ItemTransformVec3f(new Vector3f(), new Vector3f(), new Vector3f(0.25F, 0.25F, 0.25F)), new ItemTransformVec3f(new Vector3f(), new Vector3f(), new Vector3f(0.5F, 0.5F, 0.5F)));
		}
		
		@Override
		public ItemOverrideList getOverrides()
		{
			return new MicroblockIOL(this);
		}
	}
	
	public static class MicroblockIOL extends ItemOverrideList
	{
		private final BakedModelMicroblock model;
		
		public MicroblockIOL(BakedModelMicroblock model)
		{
			super(new ArrayList());
			this.model = model;
		}
		
		@Override
		public IBakedModel handleItemState(IBakedModel model, ItemStack stack, World world, EntityLivingBase entity)
		{
			if(stack != null && stack.getItem() instanceof ItemMicroblock && stack.hasTagCompound())
			{
				return new BakedModelMicroblock(this.model, stack);
			}
			return model;
		}
	}
}
