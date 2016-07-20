package continuum.multipart.client.sounds;

import continuum.api.multipart.CTMultipart_API;
import continuum.essentials.mod.CTMod;
import continuum.essentials.sounds.AdaptableSoundType;
import continuum.essentials.sounds.IAdaptableSound;
import continuum.multipart.multiparts.MultipartMicroblock;
import continuum.multipart.tileentity.TileEntityMicroblock;
import net.minecraft.block.SoundType;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AdaptableSoundMicroblock extends SoundEvent implements IAdaptableSound
{
	public AdaptableSoundMicroblock()
	{
		super(new ResourceLocation("ctmultipart", "microblockSound"));
		this.setRegistryName(new ResourceLocation("ctmultipart", "microblockSound"));
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public ISound getSound(ISound fallback, World world, BlockPos pos)
	{
		TileEntity entity = world.getTileEntity(pos);
		SoundType sound = (entity instanceof TileEntityMicroblock ? ((TileEntityMicroblock)entity).getEntry() : MultipartMicroblock.currentEntry == null ? CTMultipart_API.microblockTextureRegistry.getDefaultValue() : MultipartMicroblock.currentEntry).getSound();
		return new PositionedSoundRecord(sound.getBreakSound(), fallback.getCategory(), (sound.getVolume() + 1) / 2, sound.getPitch() * 0.8F, pos);
	}
	
	@Override
	public AdaptableSoundType getType()
	{
		return AdaptableSoundType.BREAK;
	}
}
