package org.carpetorgaddition.rulevalidator;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.Validator;
import net.minecraft.server.command.ServerCommandSource;
import org.carpetorgaddition.util.constant.RuleValidatorConstants;
import org.jetbrains.annotations.Nullable;

// 信标范围验证
public class BeaconRangeExpandValidator extends Validator<Integer> {
    public static final int MAX_VALUE = 1024;

    @Override
    public Integer validate(@Nullable ServerCommandSource serverCommandSource, CarpetRule<Integer> carpetRule, Integer integer, String s) {
        return integer <= MAX_VALUE ? integer : null;
    }

    @Override
    public String description() {
        return RuleValidatorConstants.lessThanOrEqual(MAX_VALUE).getString();
    }
}
