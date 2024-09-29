package org.carpet_org_addition.command;

import carpet.CarpetServer;
import carpet.api.settings.CarpetRule;
import carpet.api.settings.RuleHelper;
import carpet.utils.CommandHelper;
import carpet.utils.Messenger;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.mixin.rule.carpet.SettingsManagerAccessor;
import org.carpet_org_addition.util.TextUtils;

import java.util.List;
import java.util.regex.Pattern;

public class RuleSearchCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("ruleSearch")
                .requires(source -> CommandHelper.canUseCommand(source, CarpetOrgAdditionSettings.commandRuleSearch))
                .then(CommandManager.argument("rule", StringArgumentType.string())
                        .executes(context -> listRule(context, false))
                        .then(CommandManager.argument("regex", BoolArgumentType.bool())
                                .executes(context -> listRule(context, BoolArgumentType.getBool(context, "regex"))))));
    }

    // 列出符合条件的规则
    private static int listRule(CommandContext<ServerCommandSource> context, boolean regex) {
        String rule = StringArgumentType.getString(context, "rule");
        if (CarpetServer.settingsManager == null) {
            return 0;
        }
        List<CarpetRule<?>> list = CarpetServer.settingsManager.getCarpetRules().stream().toList();
        MutableText text = TextUtils.translate("carpet.commands.ruleSearch.feedback", rule);
        // 将文本设置为粗体
        text.styled(style -> style.withBold(true));
        context.getSource().sendFeedback(() -> text, false);
        int ruleCount = 0;
        for (CarpetRule<?> carpetRule : list) {
            if (matcher(rule, RuleHelper.translatedName(carpetRule), regex)) {
                Messenger.m(context.getSource(),
                        ((SettingsManagerAccessor) CarpetServer.settingsManager).displayInteractiveSettings(carpetRule));
                ruleCount++;
            }
        }
        return ruleCount;
    }

    // Carpet规则名是否包含指定子字符串
    private static boolean matcher(String rule, String translatedName, boolean regex) {
        if (regex) {
            // 检查规则名称是否包含与正则表达式匹配的子字符串
            return Pattern.compile(rule).matcher(translatedName).find();
        }
        return translatedName.contains(rule);
    }
}
