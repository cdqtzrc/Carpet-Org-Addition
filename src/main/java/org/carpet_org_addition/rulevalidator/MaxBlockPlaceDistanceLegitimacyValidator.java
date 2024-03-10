package org.carpet_org_addition.rulevalidator;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.Validator;
import net.minecraft.server.command.ServerCommandSource;
import org.carpet_org_addition.util.TextUtils;
import org.jetbrains.annotations.Nullable;

//检查最大方块交互距离的合法性
public class MaxBlockPlaceDistanceLegitimacyValidator extends Validator<Double> {
    private MaxBlockPlaceDistanceLegitimacyValidator() {
    }

    /**
     * 对服务器最大允许交互距离的值进行校验，值必须大于等于0，因为值为0时，玩家已经无法与任何方块进行交互，更低的值没有意义；为了阻止某些玩家向非常远的地方放置方块，值也必须小于等于128，并且128格已经足够远，正常情况下玩家根本不需要这么远的交互距离。值可以等于-1，表示使用默认的交互距离。
     */

    @Override
    public Double validate(@Nullable ServerCommandSource serverCommandSource, CarpetRule<Double> carpetRule, Double aDouble, String s) {
        return (aDouble >= 0 && aDouble <= 128) || aDouble == -1 ? aDouble : null;
    }

    /**
     * 输入的最大交互距离的值为非法参数时显示的信息
     */

    @Override
    public String description() {
        return TextUtils.getTranslate("carpet.rule.validate.maxBlockPlaceDistance").getString();
    }
}
