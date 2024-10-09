package org.carpetorgaddition.rulevalidator;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.Validator;
import net.minecraft.server.command.ServerCommandSource;
import org.carpetorgaddition.util.constant.RuleValidatorConstants;
import org.jetbrains.annotations.Nullable;

public class FakePlayerMaxCraftCountValidator extends Validator<Integer> {
    /**
     * 最小合成次数
     */
    public static final int MIN_CRAFT_COUNT = 1;

    @Override
    public Integer validate(@Nullable ServerCommandSource serverCommandSource, CarpetRule<Integer> carpetRule, Integer integer, String s) {
        return integer >= MIN_CRAFT_COUNT || integer == -1 ? integer : null;
    }

    @Override
    public String description() {
        return RuleValidatorConstants.greaterThanOrEqualOrNumber(MIN_CRAFT_COUNT, -1).getString();
    }
}
