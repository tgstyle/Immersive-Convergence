package com.immersiveconvergence.api.integration;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused", "RedundantSuppression"}) public class DisplayLines {
    public static final String KEY_FUEL_EMPTY = "gui.immersiveconvergence.fuel_empty";
    public static final int DEFAULT_COLOR = 0;
    private final List<Line> lines = new ArrayList<>();

    public sealed interface Line permits Text, Progress {}

    public record Text(Component text) implements Line {}

    public record Progress(int value, int max, String suffix, int fillColor, int borderColor, boolean compact) implements Line {}

    public List<Line> lines() { return lines; }

    public DisplayLines text(Component text) { lines.add(new Text(text)); return this; }

    public DisplayLines text(String text) { return text(Component.literal(text)); }

    public DisplayLines fuelEmpty() { return text(Component.translatable(KEY_FUEL_EMPTY).withStyle(ChatFormatting.GRAY)); }

    public DisplayLines progress(int value, int max, String suffix) { return progress(value, max, suffix, DEFAULT_COLOR, DEFAULT_COLOR, false); }

    public DisplayLines progress(int value, int max, String suffix, int fillColor, int borderColor, boolean compact) { lines.add(new Progress(value, max, suffix, fillColor, borderColor, compact)); return this; }

    public DisplayLines percent(int percent) { return progress(percent, 100, "%"); }

    public DisplayLines rpm(int speed, int maxRpm) { return progress(speed, maxRpm, " RPM"); }

    public DisplayLines temperature(double heatLevel, double workingLevel) { return progress((int) heatLevel, (int) workingLevel, " °C", 0xffcc0000, 0xffff6666, false); }

    public static Component describe(Line line) {
        if (line instanceof Text text) { return text.text(); }
        Progress progress = (Progress) line;
        return Component.literal(progress.value() + "/" + progress.max() + progress.suffix());
    }

    public ListTag write() {
        ListTag list = new ListTag();
        for (Line line : lines) {
            CompoundTag tag = new CompoundTag();
            if (line instanceof Text text) { tag.putString("text", Component.Serializer.toJson(text.text())); }
            else if (line instanceof Progress progress) {
                tag.putInt("value", progress.value());
                tag.putInt("max", progress.max());
                tag.putString("suffix", progress.suffix());
                tag.putInt("fill", progress.fillColor());
                tag.putInt("border", progress.borderColor());
                tag.putBoolean("compact", progress.compact());
            }
            list.add(tag);
        }
        return list;
    }

    public static DisplayLines read(ListTag list) {
        DisplayLines lines = new DisplayLines();
        for (Tag t : list) {
            CompoundTag tag = (CompoundTag) t;
            if (tag.contains("text")) {
                Component text = Component.Serializer.fromJson(tag.getString("text"));
                lines.text(text != null ? text : Component.empty());
            }
            else { lines.progress(tag.getInt("value"), tag.getInt("max"), tag.getString("suffix"), tag.getInt("fill"), tag.getInt("border"), tag.getBoolean("compact")); }
        }
        return lines;
    }
}
