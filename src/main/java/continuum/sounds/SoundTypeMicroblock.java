package continuum.multipart.client.sounds;

import continuum.essentials.mod.CTMod;
import continuum.multipart.blocks.BlockMicroblockBase;
import continuum.multipart.mod.Multipart_EH;
import continuum.multipart.mod.Multipart_OH;
import continuum.multipart.multiparts.MultipartMicroblock;
import net.minecraft.block.SoundType;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;

public class SoundTypeMicroblock extends SoundType
{
	public SoundTypeMicroblock(Multipart_OH objectHolder)
	{
		super(1.0F, 1.0F, objectHolder.microblockSound = new AdaptableSoundMicroblock(), SoundEvents.BLOCK_STONE_STEP, SoundEvents.BLOCK_STONE_PLACE, SoundEvents.BLOCK_STONE_HIT, SoundEvents.BLOCK_STONE_FALL);
	}
	
	@Override
	public SoundEvent getStepSound()
	{
		return MultipartMicroblock.currentEntry == null ? super.getStepSound() : MultipartMicroblock.currentEntry.getSound().getStepSound();
	}
	
	@Override
	public SoundEvent getPlaceSound()
	{
		return MultipartMicroblock.currentEntry == null ? super.getPlaceSound() : MultipartMicroblock.currentEntry.getSound().getPlaceSound();
	}
	
	@Override
	public SoundEvent getHitSound()
	{
		return MultipartMicroblock.currentEntry == null ? super.getHitSound() : MultipartMicroblock.currentEntry.getSound().getHitSound();
	}
	
	@Override
	public SoundEvent getFallSound()
	{
		return MultipartMicroblock.currentEntry == null ? super.getFallSound() : MultipartMicroblock.currentEntry.getSound().getFallSound();
	}
}
