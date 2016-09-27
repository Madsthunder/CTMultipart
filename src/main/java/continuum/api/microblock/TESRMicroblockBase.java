package continuum.api.microblock;

import continuum.api.microblock.material.MicroblockMaterial;
import continuum.api.microblock.material.MicroblockMaterialCapability;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public class TESRMicroblockBase<T extends TileEntity> extends TileEntitySpecialRenderer<T>
{
	private static MicroblockMaterial material = MicroblockMaterial.defaultMaterial;
	
	public boolean isGlobalRenderer(T entity)
	{
		System.out.println(entity);
		this.material = entity.hasCapability(MicroblockMaterialCapability.MICROBLOCKMATERIAL, null) ? entity.getCapability(MicroblockMaterialCapability.MICROBLOCKMATERIAL, null).getMaterial() : material;
		return super.isGlobalRenderer(entity);
	}
	
	public static MicroblockMaterial getMaterial()
	{
		return material;
	}
}
