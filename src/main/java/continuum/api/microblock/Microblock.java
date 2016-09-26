package continuum.api.microblock;

import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import com.google.common.collect.Lists;

import continuum.essentials.block.ICuboid;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class Microblock implements IForgeRegistryEntry<Microblock>
{
	public abstract Block getBlock();
	
	public abstract List<ICuboid> getCuboids();
	
	public abstract String getName();
	
	public abstract AxisAlignedBB getSelectionBox(IBlockState state);
	
	public abstract List<AxisAlignedBB> getRenderBoxes(IBlockState state);
	
	@SideOnly(Side.CLIENT)
	public ItemCameraTransforms getCameraTransforms()
	{
		return new ItemCameraTransforms(new ItemTransformVec3f(new Vector3f(75, 45, 0), new Vector3f(0, 2.5F, 0), new Vector3f(0.375F, 0.375F, 0.375F)), new ItemTransformVec3f(new Vector3f(75, 45, 0), new Vector3f(0, 2.5F, 0), new Vector3f(0.375F, 0.375F, 0.375F)), new ItemTransformVec3f(new Vector3f(0, 225, 0), new Vector3f(), new Vector3f(0.4F, 0.4F, 0.4F)), new ItemTransformVec3f(new Vector3f(0, 45, 0), new Vector3f(), new Vector3f(0.4F, 0.4F, 0.4F)), ItemTransformVec3f.DEFAULT, new ItemTransformVec3f(new Vector3f(30, 225, 0), new Vector3f(), new Vector3f(0.625F, 0.625F, 0.625F)), new ItemTransformVec3f(new Vector3f(), new Vector3f(), new Vector3f(0.25F, 0.25F, 0.25F)), new ItemTransformVec3f(new Vector3f(), new Vector3f(), new Vector3f(0.5F, 0.5F, 0.5F)));
	}
	
	public String getUnlocalizedName()
	{
		return this.getBlock().getUnlocalizedName();
	}
	
	public List<AxisAlignedBB> getExceptions(ICuboid cuboid)
	{
		return Lists.newArrayList();
	}
	
	public void addExceptionsToList(ICuboid cuboid, List<AxisAlignedBB> aabbs)
	{
	}
	
	public final Microblock setRegistryName(ResourceLocation location)
	{
		return this;
	}
	
	public final Class<? super Microblock> getRegistryType()
	{
		return Microblock.class;
	}
}
