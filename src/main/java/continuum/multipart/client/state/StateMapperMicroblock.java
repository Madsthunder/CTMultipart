package continuum.multipart.client.state;

import java.util.Map;

import com.google.common.collect.Maps;

import continuum.multipart.mod.Multipart_OH;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.item.ItemStack;

public class StateMapperMicroblock implements IStateMapper, ItemMeshDefinition
{
	public static final Multipart_OH holder = Multipart_OH.INSTANCE;
	public final Map<IBlockState, ModelResourceLocation> locations = Maps.newHashMap();
	
	public Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block block)
	{
		for(IBlockState state : block.getBlockState().getValidStates())
			this.locations.put(state, new ModelResourceLocation(holder.getModid() + ":microblock", "normal"));
		return this.locations;
	}
	
	@Override
	public ModelResourceLocation getModelLocation(ItemStack stack)
	{
		return new ModelResourceLocation(holder.getModid() + ":microblock", "normal");
	}
}
