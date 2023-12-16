package org.carpet_org_addition.rulevalidator;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.Validator;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public class MaxBlockPlaceDistanceCompatibilityCheck extends Validator<Double> {
    /**
     * 检查最大方块放置距离是否与Carpet AMS Addition中的相似功能同时开启
     */
    @Override
    public Double validate(@Nullable ServerCommandSource serverCommandSource, CarpetRule<Double> carpetRule, Double aDouble, String s) {
        if (FabricLoader.getInstance().isModLoaded("carpet-ams-addition")) {
            try {
                Class<?> ams = Class.forName("club.mcams.carpet.AmsServerSettings");
                Field ruleName = ams.getDeclaredField("maxBlockInteractionDistance");
                return (double) ruleName.get(null) == -1.0d ? aDouble : null;
            } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
                return aDouble;
            }
        }
        return aDouble;
    }

    @Override
    public String description() {
        return "已在Carpet AMS Addition中启用类似的功能";
    }
}
