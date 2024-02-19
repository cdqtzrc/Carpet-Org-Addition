package org.carpet_org_addition.rulevalidator;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.Validator;
import net.minecraft.server.command.ServerCommandSource;
import org.carpet_org_addition.util.TextUtils;
import org.jetbrains.annotations.Nullable;

public class PiglinBarteringTimeValidator extends Validator<Long> {
    @Override
    public Long validate(@Nullable ServerCommandSource serverCommandSource, CarpetRule<Long> carpetRule, Long aLong, String s) {
        return aLong >= 0 || aLong == -1 ? aLong : null;
    }

    @Override
    public String description() {
        return TextUtils.getTranslate("carpet.rule.validate.customPiglinBarteringTime").getString();
    }
}
