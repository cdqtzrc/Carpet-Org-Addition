package org.carpet_org_addition.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.carpet_org_addition.util.MessageUtils;
import org.carpet_org_addition.util.wheel.CraftPresets;

import java.io.File;

@SuppressWarnings("unused")
public class PresetsCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("presets")
                .then(CommandManager.literal("craft")
                        .then(CommandManager.literal("add")
                                .then(CommandManager.argument("name", StringArgumentType.string())
                                        .then(CommandManager.argument("presets", StringArgumentType.string())
                                                .executes(PresetsCommand::addCraftPresets))))
                        .then(CommandManager.literal("list").executes(PresetsCommand::listCraftPreset))));
    }

    private static int addCraftPresets(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        // 获取命令中设定的文件名
        String name = StringArgumentType.getString(context, "name");
        // 获取命令中设定的配方
        String presets = StringArgumentType.getString(context, "presets");
        // 创建一个合成预设对象
        CraftPresets craftPresets = new CraftPresets(name, presets);
        // 将合成预设对象写入本地文件
        craftPresets.saveCraftRecipe(context.getSource().getServer());
        return 1;
    }

    // 列出所有合成预设
    private static int listCraftPreset(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        File file = CraftPresets.getFile(context.getSource().getServer());
        File[] files = file.listFiles();
        int i = 0;
        if (files != null) {
            for (; i < files.length; i++) {
                // 只列出json文件
                if (!files[i].getName().endsWith(".json")) {
                    continue;
                }
                String fileName = CraftPresets.extractFileName(files[i].getName());
                // 列出目录下的每一个文件
                MessageUtils.sendTextMessage(context.getSource(), Text.literal(fileName));
            }
        }
        return i;
    }
}