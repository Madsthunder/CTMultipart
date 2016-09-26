package continuum.api.microblock;

import continuum.api.microblock.material.MicroblockMaterial;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer.StateImplementation;
import net.minecraft.block.state.IBlockState;

public class MicroblockStateImpl extends StateImplementation
{
	private final IBlockState state;
	private final Microblock microblock;
	private final MicroblockMaterial material;
	
	public MicroblockStateImpl(IBlockState state, Microblock microblock, MicroblockMaterial material)
	{
		super(state.getBlock(), state.getProperties());
		this.state = state;
		this.microblock = microblock;
		this.material = material;
	}
	
	@Override
	public <T extends Comparable<T>, V extends T> IBlockState withProperty(IProperty<T> property, V value)
	{
		IBlockState state = this.state.withProperty(property, value);
		return new MicroblockStateImpl(state, this.microblock, this.material);
	}
	
	public IBlockState getState()
	{
		return state;
	}
	
	public MicroblockMaterial getMicroblockMaterial()
	{
		return material;
	}
	
	public Microblock getMicroblock()
	{
		return this.microblock;
	}
}
