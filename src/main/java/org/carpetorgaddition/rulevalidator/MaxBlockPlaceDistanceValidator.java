package org.carpetorgaddition.rulevalidator;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.Validator;
import net.minecraft.server.command.ServerCommandSource;
import org.carpetorgaddition.util.constant.RuleValidatorConstants;
import org.jetbrains.annotations.Nullable;

// 最大方块交互距离校验
public class MaxBlockPlaceDistanceValidator extends Validator<Double> {
    public static final double MAX_VALUE = 256.0;

    private MaxBlockPlaceDistanceValidator() {
    }

    @Override
    public Double validate(@Nullable ServerCommandSource serverCommandSource, CarpetRule<Double> carpetRule, Double aDouble, String s) {
        return (aDouble >= 0 && aDouble <= MAX_VALUE) || aDouble == -1 ? aDouble : null;
    }

    @Override
    public String description() {
        return RuleValidatorConstants.betweenTwoNumberOrNumber(0, (int) MAX_VALUE, -1).getString();
    }
}
