package org.carpet_org_addition.command;

import carpet.patches.EntityPlayerMPFake;
import carpet.utils.CommandHelper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.util.CommandUtils;
import org.carpet_org_addition.util.MessageUtils;
import org.carpet_org_addition.util.constant.CommandSyntaxExceptionConstants;
import org.carpet_org_addition.util.fakeplayer.FakePlayerSerial;
import org.carpet_org_addition.util.helpers.WorldFormat;

import java.io.File;
import java.io.IOException;

public class PlayerManagerCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("playerManager")
                .requires(source -> CommandHelper.canUseCommand(source, CarpetOrgAdditionSettings.commandPlayerManager))
                .then(CommandManager.literal("save")
                        .then(CommandManager.argument(CommandUtils.PLAYER, EntityArgumentType.player())
                                .executes(context -> savePlayer(context, false))
                                .then(CommandManager.argument("annotation", StringArgumentType.string())
                                        .executes(context -> withAnnotationSavePlayer(context, false)))))
                .then(CommandManager.literal("spawn")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                                .suggests(suggests())
                                .executes(PlayerManagerCommand::spawnPlayer)))
                .then(CommandManager.literal("resave")
                        .then(CommandManager.argument(CommandUtils.PLAYER, EntityArgumentType.player())
                                .executes(context -> savePlayer(context, true))
                                .then(CommandManager.argument("annotation", StringArgumentType.string())
                                        .executes(context -> withAnnotationSavePlayer(context, true)))))
                .then(CommandManager.literal("list")
                        .executes(PlayerManagerCommand::list))
                .then(CommandManager.literal("delete")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                                .suggests(suggests())
                                .executes(PlayerManagerCommand::delete))));
    }

    // 自动补全玩家名
    private static SuggestionProvider<ServerCommandSource> suggests() {
        return (context, builder) -> CommandSource.suggestMatching(new WorldFormat(context.getSource().getServer(),
                FakePlayerSerial.PLAYER_DATA).listFiles().stream()
                .filter(file -> file.getName().endsWith(WorldFormat.JSON_EXTENSION))
                .map(file -> WorldFormat.removeExtension(file.getName()))
                .map(StringArgumentType::escapeIfRequired), builder);
    }

    // 列出每一个玩家
    private static int list(CommandContext<ServerCommandSource> context) {
        WorldFormat worldFormat = new WorldFormat(context.getSource().getServer(), FakePlayerSerial.PLAYER_DATA);
        return FakePlayerSerial.list(context, worldFormat);
    }

    // 保存假玩家数据
    private static int savePlayer(CommandContext<ServerCommandSource> context, boolean resave) throws CommandSyntaxException {
        EntityPlayerMPFake fakePlayer = CommandUtils.getArgumentFakePlayer(context);
        FakePlayerSerial fakePlayerSerial = new FakePlayerSerial(fakePlayer);
        savePlayer(context, fakePlayerSerial, fakePlayer, resave);
        return 1;
    }

    // 保存玩家带注释
    private static int withAnnotationSavePlayer(CommandContext<ServerCommandSource> context, boolean resave) throws CommandSyntaxException {
        EntityPlayerMPFake fakePlayer = CommandUtils.getArgumentFakePlayer(context);
        String annotation = StringArgumentType.getString(context, "annotation");
        FakePlayerSerial fakePlayerSerial = new FakePlayerSerial(fakePlayer, annotation);
        savePlayer(context, fakePlayerSerial, fakePlayer, resave);
        return 1;
    }

    // 保存玩家
    private static void savePlayer(CommandContext<ServerCommandSource> context, FakePlayerSerial fakePlayerSerial, EntityPlayerMPFake fakePlayer, boolean resave) throws CommandSyntaxException {
        try {
            if (fakePlayerSerial.save(context.getSource().getServer(), resave)) {
                // 重新保存
                MessageUtils.sendCommandFeedback(context.getSource(),
                        "carpet.commands.playerManager.save.resave",
                        fakePlayer.getDisplayName());
            } else {
                // 首次保存
                MessageUtils.sendCommandFeedback(context.getSource(),
                        "carpet.commands.playerManager.save.success",
                        fakePlayer.getDisplayName());
            }
        } catch (IOException e) {
            throw CommandUtils.createException("carpet.commands.playerManager.save.fail", fakePlayer.getDisplayName());
        }
    }

    // 生成假玩家
    private static int spawnPlayer(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String name = StringArgumentType.getString(context, "name");
        WorldFormat worldFormat = new WorldFormat(context.getSource().getServer(), FakePlayerSerial.PLAYER_DATA);
        try {
            JsonObject json = WorldFormat.loadJson(worldFormat.getFile(name));
            // 生成假玩家
            FakePlayerSerial.spawn(name, context.getSource().getServer(), json);
        } catch (JsonParseException e) {
            // 无法解析json文件
            throw CommandSyntaxExceptionConstants.JSON_PARSE_EXCEPTION;
        } catch (RuntimeException e) {
            // 尝试生成假玩家时出现意外问题
            throw CommandUtils.createException("carpet.commands.playerManager.spawn.fail");
        } catch (IOException e) {
            // 从文件读取“%s”玩家数据失败
            throw CommandUtils.createException("carpet.commands.playerManager.spawn.io", name);
        }
        return 1;
    }

    // 删除玩家信息
    private static int delete(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        WorldFormat worldFormat = new WorldFormat(context.getSource().getServer(), FakePlayerSerial.PLAYER_DATA);
        String name = StringArgumentType.getString(context, "name");
        File file = worldFormat.getFile(name);
        // 文件存在且文件删除成功
        if (file.isFile() && file.delete()) {
            MessageUtils.sendCommandFeedback(context.getSource(), "carpet.commands.playerManager.delete.success");
        } else {
            throw CommandUtils.createException("carpet.commands.playerManager.delete.fail");
        }
        return 1;
    }
}
