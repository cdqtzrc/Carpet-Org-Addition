package org.carpetorgaddition.rulevalidator;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.Validator;
import net.minecraft.server.command.ServerCommandSource;
import org.carpetorgaddition.util.constant.RuleValidatorConstants;
import org.jetbrains.annotations.Nullable;

// 设置基岩硬度校验
public class BedrockHardnessValidator extends Validator<Float> {
    private BedrockHardnessValidator() {
    }

    /**
     * 对基岩硬度的值进行校验，修改的硬度值必须大于等于0，或者等于-1。因为硬度为负值的方块本身就无法挖掘，设置为其他的负硬度值是没有意义的
     */
    @Override
    public Float validate(@Nullable ServerCommandSource serverCommandSource, CarpetRule<Float> carpetRule, Float aFloat, String s) {
        return aFloat >= 0 || aFloat == -1 ? aFloat : null;
    }

    /**
     * 输入基岩硬度为非法参数时显示的信息
     */
    @Override
    public String description() {
        return RuleValidatorConstants.greaterThanOrEqualOrNumber(0, -1).getString();
    }
}
