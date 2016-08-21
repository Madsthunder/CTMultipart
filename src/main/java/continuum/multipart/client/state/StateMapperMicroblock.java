package continuum.multipart.client.state;

import java.util.HashMap;
import java.util.Map;

import continuum.api.multipart.MicroblockTextureEntry;
import continuum.api.multipart.MultipartAPI;
import continuum.multipart.mod.Multipart_OH;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.item.ItemStack;

public class StateMapperMicroblock implements IStateMapper, ItemMeshDefinition
{
	public final Multipart_OH objectHolder;
	public final Map<IBlockState, ModelResourceLocation> locations = new HashMap<IBlockState, ModelResourceLocation>();
	
	public StateMapperMicroblock(Multipart_OH objectHolder)
	{
		this.objectHolder = objectHolder;
	}
	
	public Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block block)
	{
		for(IBlockState state : block.getBlockState().getValidStates())
			for(MicroblockTextureEntry entry : MultipartAPI.microblockTextureRegistry)
				this.locations.put(state, new ModelResourceLocation(this.objectHolder.getModid() + ":microblock", "normal"));
		return this.locations;
	}
	
	@Override
	public ModelResourceLocation getModelLocation(ItemStack stack)
	{
		return new ModelResourceLocation(this.objectHolder.getModid() + ":microblock", "normal");
	}
}
