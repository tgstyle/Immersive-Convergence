package com.immersiveconvergence.api.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import java.util.HashMap;

@SuppressWarnings("unused")
public class ICSoundHandler extends PositionedSound implements ITickableSound {
    private static final HashMap<BlockPos, ICSoundHandler> playingSounds = new HashMap<>();
    private static float volumeAdjustment = 1;
    private final BlockPos pos;
    private float unmodifiedVolume;

    public static void playOnce(BlockPos posIn, SoundEvent soundIn, SoundCategory categoryIn, float volumeIn, float pitchIn) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) { return; }
        player.world.playSound(player, posIn, soundIn, categoryIn, volumeIn, pitchIn);
    }

    public static void playRepeating(BlockPos posIn, SoundEvent soundIn, SoundCategory categoryIn, float volumeIn, float pitchIn) {
        ICSoundHandler sound = playingSounds.get(posIn);
        if (sound == null) {
            sound = new ICSoundHandler(posIn, soundIn, categoryIn, true, volumeIn, pitchIn);
            playingSounds.put(posIn, sound);
        }
        else if (!Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(sound)) {
            playingSounds.remove(posIn);
            playingSounds.put(posIn, new ICSoundHandler(posIn, soundIn, categoryIn, true, volumeIn, pitchIn));
        }
        else {
            sound.unmodifiedVolume = volumeIn;
            sound.volume = volumeIn * volumeAdjustment;
            sound.pitch = pitchIn;
            sound.repeat = true;
        }
    }

    public static void stopSound(BlockPos posIn) {
        ICSoundHandler sound = playingSounds.get(posIn);
        if (sound == null) { return; }
        sound.stop(false);
    }

    public static boolean isPlaying(BlockPos posIn) { return playingSounds.get(posIn) != null; }

    public static void deleteAllSounds() {
        playingSounds.forEach((blockPos, sound) -> sound.stop(true));
        playingSounds.clear();
    }

    public static void setVolumeAdjustment(float value) {
        if (volumeAdjustment == value) { return; }
        volumeAdjustment = value;
        playingSounds.forEach((blockPos, sound) -> sound.volume = sound.unmodifiedVolume * volumeAdjustment);
    }

    public ICSoundHandler(BlockPos posIn, SoundEvent soundIn, SoundCategory categoryIn, boolean repeatIn, float volumeIn, float pitchIn) {
        super(soundIn, categoryIn);
        this.pos = posIn;
        this.unmodifiedVolume = volumeIn;
        this.volume = volumeIn * volumeAdjustment;
        this.pitch = pitchIn;
        this.xPosF = pos.getX() + 0.5f;
        this.yPosF = pos.getY() + 0.5f;
        this.zPosF = pos.getZ() + 0.5f;
        this.repeat = repeatIn;
        this.attenuationType = AttenuationType.NONE;
        Minecraft.getMinecraft().getSoundHandler().playSound(this);
    }

    @Override public boolean isDonePlaying() { return playingSounds.get(pos) != this; }

    @Override public void update() {}

    private void stop(boolean keepOnList) {
        if (!keepOnList) { playingSounds.remove(pos); }
        Minecraft.getMinecraft().getSoundHandler().stopSound(this);
    }
}
