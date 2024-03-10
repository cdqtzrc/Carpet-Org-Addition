package org.carpet_org_addition.rulevalidator;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.Validator;
import net.minecraft.server.command.ServerCommandSource;
import org.carpet_org_addition.util.TextUtils;
import org.jetbrains.annotations.Nullable;

public class BeaconRangeExpandValidator extends Validator<Integer> {
    @Override
    public Integer validate(@Nullable ServerCommandSource serverCommandSource, CarpetRule<Integer> carpetRule, Integer integer, String s) {
        return integer <= 1024 ? integer : null;
    }

    @Override
    public String description() {
        return TextUtils.getTranslate("carpet.rule.validate.beaconRangeExpand").getString();
    }
}
