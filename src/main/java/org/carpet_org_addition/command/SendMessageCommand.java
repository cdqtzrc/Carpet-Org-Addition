package org.carpet_org_addition.command;

import carpet.utils.CommandHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.ColorArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.util.*;

public class SendMessageCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("sendMessage")
                .requires(source -> CommandHelper.canUseCommand(source, CarpetOrgAdditionSettings.commandSendMessage))
                .then(CommandManager.literal("copy")
                        .then(CommandManager.argument("text", StringArgumentType.string())
                                .executes(SendMessageCommand::sendReplicableText)))
                .then(CommandManager.literal("url")
                        .then(CommandManager.argument("url", StringArgumentType.string())
                                .executes(SendMessageCommand::sendClickableLink)))
                .then(CommandManager.literal("location")
                        .executes(SendMessageCommand::sendSelfLocation))
                .then(CommandManager.literal("color")
                        .then(CommandManager.argument("color", ColorArgumentType.color())
                                .then(CommandManager.argument("text", StringArgumentType.string())
                                        .executes(SendMessageCommand::sendColorText))))
                .then(CommandManager.literal("strikethrough")
                        .then(CommandManager.argument("text", StringArgumentType.string())
                                .executes(SendMessageCommand::sendStrikethroughText)))
                .then(CommandManager.literal("formatting")
                        .then(CommandManager.argument("text", StringArgumentType.string())
                                .executes(SendMessageCommand::sendFormattingText)))
        );
    }

    //发送可复制文本
    private static int sendReplicableText(CommandContext<ServerCommandSource> context) {
        //获取命令来源，并进行非空判断
        ServerCommandSource source = context.getSource();
        //在输入命令时输入的消息
        String text = StringArgumentType.getString(context, "text");
        //给文本添加颜色，单击事件，鼠标悬停事件
        MutableText copy = TextUtils.copy(text, text, TextUtils.getTranslate("chat.copy.click"), Formatting.GREEN);
        MessageUtils.broadcastTextMessage(source, appendPlayerName(source, copy));
        return 1;
    }

    //发送可点击链接
    private static int sendClickableLink(CommandContext<ServerCommandSource> context) {
        //获取命令来源，并非空判断
        ServerCommandSource source = context.getSource();
        //创建可变文本对象
        String text = StringArgumentType.getString(context, "url");
        MutableText url = TextUtils.url(text, text, TextUtils.getTranslate("carpet.commands.sendMessage.url.click_open_url").getString(), null);
        MessageUtils.broadcastTextMessage(source, appendPlayerName(source, url));
        return 1;
    }

    //发送自己的位置
    private static int sendSelfLocation(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = CommandUtils.getPlayer(context);
        BlockPos blockPos = player.getBlockPos();
        MutableText mutableText = switch (StringUtils.getDimensionId(player.getWorld())) {
            case "minecraft:overworld" -> TextUtils.getTranslate("carpet.commands.sendMessage.location.overworld",
                    player.getDisplayName(),
                    TextUtils.blockPos(blockPos, Formatting.GREEN),
                    TextUtils.blockPos(MathUtils.getTheNetherPos(player), Formatting.RED));
            case "minecraft:the_nether" -> TextUtils.getTranslate("carpet.commands.sendMessage.location.the_nether",
                    player.getDisplayName(),
                    TextUtils.blockPos(blockPos, Formatting.RED),
                    TextUtils.blockPos(MathUtils.getOverworldPos(player), Formatting.GREEN));
            case "minecraft:the_end" -> TextUtils.getTranslate("carpet.commands.sendMessage.location.the_end",
                    player.getDisplayName(),
                    TextUtils.blockPos(blockPos, Formatting.DARK_PURPLE));
            default -> TextUtils.getTranslate("carpet.commands.sendMessage.location.default",
                    player.getDisplayName(),
                    StringUtils.getDimensionId(player.getWorld()),
                    TextUtils.blockPos(blockPos, null));
        };
        MessageUtils.broadcastTextMessage(player, mutableText);
        return 1;
    }

    // 发送带颜色的文本
    private static int sendColorText(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        // 获取文本的颜色
        Formatting color = ColorArgumentType.getColor(context, "color");
        // 获取要发送的文本内容
        String text = StringArgumentType.getString(context, "text");
        // 发送消息
        MessageUtils.broadcastTextMessage(source, appendPlayerName(source, TextUtils.regularStyle(text, color)));
        return 1;
    }

    // 发送带删除线的消息
    private static int sendStrikethroughText(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        // 获取要发送的文本内容
        String text = StringArgumentType.getString(context, "text");
        // 发送消息
        MessageUtils.broadcastTextMessage(source, appendPlayerName(source,
                TextUtils.regularStyle(text, Formatting.WHITE, false, false, false, true)));
        return 1;
    }

    // 发送可格式化文本
    private static int sendFormattingText(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        // 获取要发送的文本内容，并将“$”符号替换为“§”
        String text = StringArgumentType.getString(context, "text").replaceAll("\\$", "§");
        MessageUtils.broadcastTextMessage(source, appendPlayerName(source, TextUtils.createText(text)));
        return 1;
    }

    // 在文本前添加玩家名（如果玩家不为null）
    private static MutableText appendPlayerName(ServerCommandSource source, MutableText text) {
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) {
            return text;
        }
        // 如果玩家不为null，则发送消息时在文本前添加玩家名
        return TextUtils.appendAll(player.getDisplayName(), ": ", text);
    }
}
