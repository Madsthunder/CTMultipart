package continuum.api.microblock;

import continuum.api.microblock.texture.MicroblockMaterial;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer.StateImplementation;
import net.minecraft.block.state.IBlockState;

public class MicroblockStateImpl extends StateImplementation
{
	public final StateImplementation implementation;
	public final MicroblockMaterial entry;
	
	public MicroblockStateImpl(StateImplementation state, MicroblockMaterial entry)
	{
		super(state.getBlock(), state.getProperties(), state.getPropertyValueTable());
		this.implementation = state;
		this.entry = entry;
	}
	
	@Override
	public <T extends Comparable<T>, V extends T> IBlockState withProperty(IProperty<T> property, V value)
	{
		IBlockState state = this.implementation.withProperty(property, value);
		return new MicroblockStateImpl((StateImplementation)state, this.entry);
	}
}
