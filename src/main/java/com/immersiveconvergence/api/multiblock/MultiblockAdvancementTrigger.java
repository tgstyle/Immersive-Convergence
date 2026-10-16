package com.immersiveconvergence.api.multiblock;

import com.immersiveconvergence.api.ICMods;
import com.immersiveconvergence.api.multiblock.MultiblockHandler.IMultiblock;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MultiblockAdvancementTrigger implements CriterionTrigger<MultiblockAdvancementTrigger.Instance> {
    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(ICMods.IMMERSIVE_ENGINEERING, "multiblock_formed");
    private final Map<PlayerAdvancements, Listeners> listeners = Maps.newHashMap();

    @Nonnull @Override public ResourceLocation getId() { return ID; }

    @Override public void addPlayerListener(@Nonnull PlayerAdvancements playerAdvancements, @Nonnull CriterionTrigger.Listener<MultiblockAdvancementTrigger.Instance> listener) {
        MultiblockAdvancementTrigger.Listeners listeners = this.listeners.get(playerAdvancements);
        if (listeners == null) {
            listeners = new MultiblockAdvancementTrigger.Listeners(playerAdvancements);
            this.listeners.put(playerAdvancements, listeners);
        }
        listeners.add(listener);
    }

    @Override public void removePlayerListener(@Nonnull PlayerAdvancements playerAdvancements, @Nonnull CriterionTrigger.Listener<MultiblockAdvancementTrigger.Instance> listener) {
        MultiblockAdvancementTrigger.Listeners listeners = this.listeners.get(playerAdvancements);
        if (listeners != null) {
            listeners.remove(listener);
            if (listeners.isEmpty()) { this.listeners.remove(playerAdvancements); }
        }
    }

    @Override public void removePlayerListeners(@Nonnull PlayerAdvancements playerAdvancements) { this.listeners.remove(playerAdvancements); }

    @Nonnull @Override public Instance createInstance(@Nonnull JsonObject json, @Nonnull DeserializationContext context) {
        ContextAwarePredicate and = EntityPredicate.fromJson(json, "player", context);
        return new MultiblockAdvancementTrigger.Instance(ResourceLocation.parse(GsonHelper.getAsString(json, "multiblock")), ItemPredicate.fromJson(json.get("item")), and);
    }

    public void trigger(ServerPlayer player, IMultiblock multiblock, ItemStack hammer) {
        MultiblockAdvancementTrigger.Listeners listeners = this.listeners.get(player.getAdvancements());
        if (listeners != null) { listeners.trigger(multiblock, hammer); }
    }

    public static class Instance extends AbstractCriterionTriggerInstance {
        private final ResourceLocation multiblock;
        private final ItemPredicate hammer;

        public Instance(ResourceLocation multiblock, ItemPredicate hammer, ContextAwarePredicate and) {
            super(MultiblockAdvancementTrigger.ID, and);
            this.multiblock = multiblock;
            this.hammer = hammer;
        }

        public boolean test(IMultiblock multiblock, ItemStack hammer) { return this.multiblock.equals(multiblock.getUniqueName()) && this.hammer.matches(hammer); }

        @Nonnull @Override public JsonObject serializeToJson(@Nonnull SerializationContext conditions) {
            JsonObject jsonobject = super.serializeToJson(conditions);
            jsonobject.addProperty("multiblock", this.multiblock.toString());
            jsonobject.add("item", this.hammer.serializeToJson());
            return jsonobject;
        }
    }

    static class Listeners {
        private final PlayerAdvancements playerAdvancements;
        private final Set<Listener<Instance>> listeners = Sets.newHashSet();

        public Listeners(PlayerAdvancements playerAdvancementsIn) { this.playerAdvancements = playerAdvancementsIn; }

        public boolean isEmpty() { return this.listeners.isEmpty(); }

        public void add(CriterionTrigger.Listener<MultiblockAdvancementTrigger.Instance> listener) { this.listeners.add(listener); }

        public void remove(CriterionTrigger.Listener<MultiblockAdvancementTrigger.Instance> listener) { this.listeners.remove(listener); }

        public void trigger(IMultiblock multiblock, ItemStack hammer) {
            List<Listener<Instance>> list = null;
            for (CriterionTrigger.Listener<MultiblockAdvancementTrigger.Instance> listener : this.listeners) {
                if (listener.getTriggerInstance().test(multiblock, hammer)) {
                    if (list == null) { list = Lists.newArrayList(); }
                    list.add(listener);
                }
            }
            if (list != null) {
                for (CriterionTrigger.Listener<MultiblockAdvancementTrigger.Instance> listener1 : list) { listener1.run(this.playerAdvancements); }
            }
        }
    }
}
