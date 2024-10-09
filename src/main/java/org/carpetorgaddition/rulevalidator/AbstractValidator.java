package org.carpetorgaddition.rulevalidator;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.RuleHelper;
import carpet.api.settings.Validator;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import org.carpetorgaddition.util.MessageUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractValidator<T> extends Validator<T> {
    /**
     * @deprecated 不支持翻译
     */
    @Override
    @Deprecated(forRemoval = true)
    public final String description() {
        throw new UnsupportedOperationException();
    }

    @Override
    public T validate(@Nullable ServerCommandSource serverCommandSource, CarpetRule<T> carpetRule, T newValue, String userInput) {
        return validate(newValue) ? newValue : null;
    }

    /**
     * 规则的新值是否有效
     *
     * @param newValue 规则的新值
     * @return 如果值有效，返回true，否则返回false
     * @apiNote 与此方法重载方法一样不得抛出异常
     */
    public abstract boolean validate(T newValue);

    /**
     * @return 规则校验失败时的错误反馈
     */
    @NotNull
    public abstract MutableText errorMessage();

    @Override
    public void notifyFailure(ServerCommandSource source, CarpetRule<T> currentRule, String providedValue) {
        // 获取此规则的翻译名称
        String translatedName = RuleHelper.translatedName(currentRule);
        MessageUtils.sendCommandErrorFeedback(source, "carpet.rule.validate.invalid_value", translatedName, providedValue);
        MessageUtils.sendCommandErrorFeedback(source, errorMessage());
    }
}
