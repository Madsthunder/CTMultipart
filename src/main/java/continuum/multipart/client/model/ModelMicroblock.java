package continuum.multipart.client.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;
import org.lwjgl.util.vector.Vector3f;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import continuum.api.microblock.Microblock;
import continuum.api.microblock.MicroblockStateImpl;
import continuum.api.microblock.compat.MultipartCompat;
import continuum.api.microblock.texture.MicroblockMaterial;
import continuum.api.multipart.MultiblockStateImpl;
import continuum.api.multipart.MultipartState;
import continuum.essentials.hooks.BlockHooks;
import continuum.essentials.hooks.ObjectHooks;
import continuum.essentials.mod.CTMod;
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
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IRetexturableModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry;

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
		IForgeRegistry<MicroblockMaterial> microblockMaterialRegistry = GameRegistry.findRegistry(MicroblockMaterial.class);
		if(microblockMaterialRegistry != null)
			for(MicroblockMaterial material : microblockMaterialRegistry)
			{
				textures.add(material.getParticleTexture());
				textures.addAll(material.getAllTextures());
			}
		return textures;
	}
	
	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> function)
	{
		return new BakedModelMicroblock(this, format, function);
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
		private final Pair<Microblock, List<BakedQuad>> itemModel;
		
		public BakedModelMicroblock(ModelMicroblock rootClass, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> function)
		{
			this.format = format;
			this.function = function;
			this.itemModel = null;
		}
		
		public BakedModelMicroblock(BakedModelMicroblock baseModel, ItemMicroblock item, NBTTagCompound compound)
		{
			this.format = baseModel.format;
			this.function = baseModel.function;
			IBlockState state = item.getRenderState();
			this.itemModel = Pair.of(item.getMicroblock(), this.createQuads(item.getMicroblock(), state, null, MicroblockMaterial.readFromNBT(compound)));
		}
		
		@Override
		public List<BakedQuad> getQuads(IBlockState state, EnumFacing facing, long random)
		{
			if(this.itemModel != null)
				return this.itemModel.getRight();
			IBlockState state1 = state;
			if(state instanceof MultiblockStateImpl)
				state1 = ((MultiblockStateImpl)state).getImplementation();
			if(state1 instanceof MicroblockStateImpl)
				return this.createQuads(((MicroblockStateImpl)state1).getMicroblock(), state, facing, ((MicroblockStateImpl)state1).getMicroblockMaterial());
			return Lists.newArrayList();
		}
		
		private List<BakedQuad> createQuads(Microblock microblock, IBlockState state, EnumFacing facing, MicroblockMaterial material)
		{
			return this.getQuads(facing, microblock.getRenderBoxes(state), spliceBoxes(microblock, state, material), ObjectHooks.applyAll(this.function, material.getAllTextures()));
		}
		
		public List<BakedQuad> getQuads(EnumFacing facing, Iterable<AxisAlignedBB> originalBoxes, Iterable<AxisAlignedBB> aabbs, List<TextureAtlasSprite> textures)
		{
			List<BakedQuad> quads = Lists.newArrayList();
			for(AxisAlignedBB aabb1 : aabbs)
				for(EnumFacing facing1 : EnumFacing.values())
				{
					int i = facing1.ordinal();
					final AxisAlignedBB aabb = BlockHooks.createAABBFromSide(facing1, aabb1);
					if(Iterables.any(originalBoxes, new Predicate<AxisAlignedBB>()
					{
						@Override
						public boolean apply(AxisAlignedBB aabb2)
						{
							return BlockHooks.isFlushWithSide(facing1, aabb2, aabb);
						}
					}))
					{
						AxisAlignedBB aabb3 = BlockHooks.dialate(aabb, 16);
						quads.add(bakery.makeBakedQuad(minVec(aabb3), maxVec(aabb3), createPartFace(facing1, aabb3, textures.get(i)), textures.get(i), facing1, ModelRotation.X0_Y0, null, true, true));
					}
				}
			return quads;
		}
		
		public static float[] getUVs(EnumFacing f, AxisAlignedBB aabb)
		{
			Axis axis = f.getAxis();
			if(axis.isVertical())
				return new float[] { (float)aabb.minX, invertMin(EnumFacing.DOWN, f, aabb.minZ, aabb.maxZ), (float)aabb.maxX, invertMax(EnumFacing.DOWN, f, aabb.minZ, aabb.maxZ) };
			if(axis.isHorizontal())
				if(axis == Axis.Z)
					return new float[] { invertMin(EnumFacing.NORTH, f, aabb.minX, aabb.maxX), invertMin(f, f, aabb.minY, aabb.maxY), invertMax(EnumFacing.NORTH, f, aabb.minX, aabb.maxX), invertMax(f, f, aabb.minY, aabb.maxY) };
				else if(axis == Axis.X)
					return new float[] { invertMin(EnumFacing.EAST, f, aabb.minZ, aabb.maxZ), invertMin(f, f, aabb.minY, aabb.maxY), invertMax(EnumFacing.EAST, f, aabb.minZ, aabb.maxZ), invertMax(f, f, aabb.minY, aabb.maxY) };
			return new float[4];
		}
		
		public static float invertMin(EnumFacing facingEquals, EnumFacing facingSubject, double minValue, double maxValue)
		{
			if(facingSubject == facingEquals)
				return (float)(16 - maxValue);
			return (float)minValue;
		}
		
		public static float invertMax(EnumFacing facingEquals, EnumFacing facingSubject, double minValue, double maxValue)
		{
			if(facingSubject == facingEquals)
				return (float)(16 - minValue);
			return (float)maxValue;
		}
		
		public static BlockPartFace createPartFace(EnumFacing facing, AxisAlignedBB aabb, TextureAtlasSprite texture)
		{
			return new BlockPartFace(facing, -1, texture.getIconName(), new BlockFaceUV(getUVs(facing, aabb), 0));
		}
		
		public static AxisAlignedBB getAABB(AxisAlignedBB aabb, EnumFacing facing)
		{
			aabb = new AxisAlignedBB(aabb.minX * 16, aabb.minY * 16, aabb.minZ * 16, aabb.maxX * 16, aabb.maxY * 16, aabb.maxZ * 16);
			if(facing == EnumFacing.DOWN)
				return new AxisAlignedBB(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.minY, aabb.maxZ);
			if(facing == EnumFacing.UP)
				return new AxisAlignedBB(aabb.minX, aabb.maxY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
			if(facing == EnumFacing.NORTH)
				return new AxisAlignedBB(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.minZ);
			if(facing == EnumFacing.SOUTH)
				return new AxisAlignedBB(aabb.minX, aabb.minY, aabb.maxZ, aabb.maxX, aabb.maxY, aabb.maxZ);
			if(facing == EnumFacing.WEST)
				return new AxisAlignedBB(aabb.minX, aabb.minY, aabb.minZ, aabb.minX, aabb.maxY, aabb.maxZ);
			if(facing == EnumFacing.EAST)
				return new AxisAlignedBB(aabb.maxX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
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
			return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
		}
		
		@Override
		public ItemCameraTransforms getItemCameraTransforms()
		{
			return this.itemModel.getLeft().getCameraTransforms();
		}
		
		@Override
		public ItemOverrideList getOverrides()
		{
			return MicroblockIOL.I;
		}
	}
	
	public static class MicroblockIOL extends ItemOverrideList
	{
		private static final MicroblockIOL I = new MicroblockIOL();
		
		private MicroblockIOL()
		{
			super(Lists.newArrayList());
		}
		
		@Override
		public IBakedModel handleItemState(IBakedModel model, ItemStack stack, World world, EntityLivingBase entity)
		{
			MicroblockMaterial material;
			if(model instanceof BakedModelMicroblock && stack != null && stack.getItem() instanceof ItemMicroblock && (stack.hasTagCompound() && (material = MicroblockMaterial.readFromNBT(stack.getTagCompound())) != null))
			{
				return new BakedModelMicroblock((BakedModelMicroblock)model, (ItemMicroblock)stack.getItem(), stack.getTagCompound());
			}
			return model;
		}
	}
	
	public static Iterable<AxisAlignedBB> spliceBoxes(Microblock microblock, IBlockState state, MicroblockMaterial material)
	{
		Iterable<AxisAlignedBB> set = Sets.newHashSet(microblock.getRenderBoxes(state));
		if(state instanceof MultiblockStateImpl)
		{
			MultiblockStateImpl msi = (MultiblockStateImpl)state;
			set = BlockHooks.splitAABBs(set, msi.getInfoList().getAllInfoOfMultipartInstance(MultipartMicroblock.class), new Predicate<MultipartState<MultipartMicroblock>>()
			{
				@Override
				public boolean apply(MultipartState<MultipartMicroblock> info)
				{
					return MultipartCompat.microblockOverlaps(info, msi.getInfo());
				}
			}, new Function<MultipartState<MultipartMicroblock>, Iterable<AxisAlignedBB>>()
			{
				@Override
				public Iterable<AxisAlignedBB> apply(MultipartState<MultipartMicroblock> info)
				{
					return ((MultipartMicroblock)info.getMultipart()).getMicroblock().getRenderBoxes(info.getState());
				}
			});
		}
		return set;
	}
}
