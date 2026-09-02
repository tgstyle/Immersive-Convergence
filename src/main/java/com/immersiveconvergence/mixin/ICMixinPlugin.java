package com.immersiveconvergence.mixin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;

import java.util.List;
import java.util.Set;

public class ICMixinPlugin implements IMixinConfigPlugin {
    private static final Logger LOGGER = LogManager.getLogger("ImmersiveConvergence/Mixin");

    @Override public void onLoad(String mixinPackage) {}

    @Override public String getRefMapperConfig() { return null; }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        String mixin = mixinClassName.substring(mixinClassName.lastIndexOf('.') + 1);
        boolean apply = switch (mixin) {
            case "AbstractSplitModelMixin" -> lacksMethod(targetClassName, "useAmbientOcclusion");
            case "BakedBasicSplitModelMixin" -> lacksMethod(targetClassName, "applyTransform");
            case "BakedDynamicSplitModelMixin" -> dynamicSplitModelUnfixed(targetClassName);
            case "PolygonUtilsMixin" -> polygonUtilsUnfixed(targetClassName);
            default -> true;
        };
        if (!apply) { LOGGER.info("Skipping {}: Immersive Engineering already carries this split model fix (IE PR #6427)", mixin); }
        return apply;
    }

    private static ClassNode load(String targetClassName) {
        try { return MixinService.getService().getBytecodeProvider().getClassNode(targetClassName); }
        catch (Throwable t) {
            LOGGER.warn("Could not read {} to check for the IE split model fix; leaving it alone", targetClassName, t);
            return null;
        }
    }

    private static boolean lacksMethod(String targetClassName, String name) {
        ClassNode node = load(targetClassName);
        if (node == null) { return false; }
        return node.methods.stream().noneMatch(m -> m.name.equals(name));
    }

    private static boolean dynamicSplitModelUnfixed(String targetClassName) {
        ClassNode node = load(targetClassName);
        if (node == null) { return false; }
        MethodNode ctor = node.methods.stream().filter(m -> m.name.equals("<init>")).findFirst().orElse(null);
        if (ctor == null) { return false; }
        boolean sawMaximumSize = false;
        boolean sawExpireAfterAccess = false;
        for (AbstractInsnNode insn : ctor.instructions) {
            if (insn instanceof FieldInsnNode field && field.getOpcode() == Opcodes.GETSTATIC && field.name.equals("WEAK_INSTANCES")) { return false; }
            if (insn instanceof MethodInsnNode call && call.owner.equals("com/google/common/cache/CacheBuilder")) {
                if (call.name.equals("maximumSize")) { sawMaximumSize = true; }
                else if (call.name.equals("expireAfterAccess")) { sawExpireAfterAccess = true; }
            }
        }
        return sawMaximumSize && sawExpireAfterAccess;
    }

    private static boolean polygonUtilsUnfixed(String targetClassName) {
        ClassNode node = load(targetClassName);
        if (node == null) { return false; }
        if (node.methods.stream().anyMatch(m -> m.name.equals("bakeSplitLighting"))) { return false; }
        MethodNode method = node.methods.stream()
                .filter(m -> m.name.equals("toBakedQuad") && m.desc.startsWith("(Lmalte0811/modelsplitter/model/Polygon;"))
                .findFirst().orElse(null);
        if (method == null) { return false; }
        AbstractInsnNode lastReal = null;
        for (AbstractInsnNode insn : method.instructions) {
            if (insn instanceof MethodInsnNode call && call.getOpcode() == Opcodes.INVOKESTATIC && call.name.equals("toBakedQuad") && call.desc.contains("ZZ)")) {
                return lastReal != null && lastReal.getOpcode() == Opcodes.ICONST_1;
            }
            if (insn.getOpcode() >= 0) { lastReal = insn; }
        }
        return false;
    }

    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override public List<String> getMixins() { return null; }

    @Override public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
