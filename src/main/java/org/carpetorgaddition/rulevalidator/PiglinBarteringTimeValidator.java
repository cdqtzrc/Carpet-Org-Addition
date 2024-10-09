package org.carpetorgaddition.rulevalidator;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.Validator;
import net.minecraft.server.command.ServerCommandSource;
import org.carpetorgaddition.util.constant.RuleValidatorConstants;
import org.jetbrains.annotations.Nullable;

// 自定义猪灵交易时间
public class PiglinBarteringTimeValidator extends Validator<Long> {
    @Override
    public Long validate(@Nullable ServerCommandSource serverCommandSource, CarpetRule<Long> carpetRule, Long aLong, String s) {
        return aLong >= 0 || aLong == -1 ? aLong : null;
    }

    @Override
    public String description() {
        return RuleValidatorConstants.greaterThanOrEqualOrNumber(0, -1).getString();
    }
}
