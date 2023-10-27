package org.carpet_org_addition.rulevalidator;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.Validator;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.Nullable;

//检查末影螨生成概率数组的合法性
public class CheckEnderPearlSpawnEndermiteProbability extends Validator<Float> {
    private CheckEnderPearlSpawnEndermiteProbability() {
    }

    /**
     * 对末影珍珠生成末影螨的概率的值进行校验，因为是百分比，所以值必须介于0-1之间；如果为-1，表示默认的生成概率
     */

    @Override
    public Float validate(@Nullable ServerCommandSource serverCommandSource, CarpetRule<Float> carpetRule, Float aFloat, String s) {
        return (aFloat >= 0 && aFloat <= 1) || aFloat == -1 ? aFloat : null;
    }

    /**
     * 输入的末影珍珠生成末影螨概率的值为非法参数时显示的信息
     */

    @Override
    public String description() {
        return "值必须介于0-1之间，或者-1";
    }
}
