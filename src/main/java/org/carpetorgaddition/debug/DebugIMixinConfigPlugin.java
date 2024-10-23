package org.carpetorgaddition.debug;

import net.fabricmc.loader.api.FabricLoader;
import org.carpetorgaddition.CarpetOrgAddition;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.util.Annotations;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Set;

public class DebugIMixinConfigPlugin implements IMixinConfigPlugin {
    /**
     * 当前jvm是否为调试模式
     */
    public static final boolean IS_DEBUG = ManagementFactory.getRuntimeMXBean().getInputArguments().stream().anyMatch(s -> s.contains("jdwp"));
    /**
     * 如果直接使用{@link CarpetOrgAddition#LOGGER}会导致一些类被提前加载，因此在这里重新创建一个对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger("CarpetOrgAdditionDebug");

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        try {
            ClassNode classNode = MixinService.getService().getBytecodeProvider().getClassNode(mixinClassName);
            AnnotationNode annotationNode = Annotations.getVisible(classNode, OnlyDeveloped.class);
            // Mixin类没有被@OnlyDeveloped注解
            if (annotationNode == null) {
                return true;
            }
            // 类被注解，且开发环境
            if (IS_DEBUG && FabricLoader.getInstance().isDevelopmentEnvironment()) {
                DebugIMixinConfigPlugin.LOGGER.info("Mixin类已被允许开发环境下加载：{}", mixinClassName);
                return true;
            }
            return false;
        } catch (IOException | ClassNotFoundException e) {
            return true;
        }
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
