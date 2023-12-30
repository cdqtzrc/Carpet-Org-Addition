package org.carpet_org_addition.command;

import carpet.utils.CommandHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.util.*;

public class SendMessageCommand {
    /*
         TODO:
          功能过于简单，应该对功能进行扩展
              比如允许玩家发送一条彩色的消息
              允许玩家发送一条可以被格式化的消息等（使用其他符号代替分节符§）
              允许玩家发送一条各种组件组装的消息，使用append子命令追加，使用send发送，可以发送各种颜色的文字，方块，物品实体的名称
              允许玩家发送一条可翻译的文本
    */
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("sendMessage")
                .requires(source -> CommandHelper.canUseCommand(source, CarpetOrgAdditionSettings.commandSendMessage))
                .then(CommandManager.literal("copy").then(CommandManager.argument("text", StringArgumentType.string())
                        .executes(SendMessageCommand::sendReplicableText)))
                .then(CommandManager.literal("url").then(CommandManager.argument("url", StringArgumentType.string())
                        .executes(SendMessageCommand::sendClickableLink)))
                .then(CommandManager.literal("location")
                        .executes(SendMessageCommand::sendSelfLocation))
        );
    }

    //发送可复制文本
    private static int sendReplicableText(CommandContext<ServerCommandSource> context) {
        //获取命令来源，并进行非空判断
        ServerCommandSource source = context.getSource();
        //获取玩家对象
        ServerPlayerEntity serverPlayerEntity = source.getPlayer();
        //在输入命令时输入的消息
        String text = StringArgumentType.getString(context, "text");
        //给文本添加颜色，单击事件，鼠标悬停事件
        MutableText copy = TextUtils.copy(text, text, TextUtils.getTranslate("chat.copy.click"), Formatting.GREEN);
        //如果是玩家发送的，在前面追加玩家名
        if (serverPlayerEntity != null) {
            Text name = serverPlayerEntity.getDisplayName();
            copy = name.copy().append(": ").append(copy);
        }
        MessageUtils.broadcastTextMessage(source, copy);
        return 1;
    }

    //发送可点击链接
    private static int sendClickableLink(CommandContext<ServerCommandSource> context) {
        //获取命令来源，并非空判断
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity serverPlayerEntity = source.getPlayer();
        //创建可变文本对象
        String text = StringArgumentType.getString(context, "url");
        MutableText url = TextUtils.url(text, text, TextUtils.getTranslate("carpet.commands.sendMessage.url.click_open_url").getString(), null);
        if (serverPlayerEntity != null) {
            MutableText playerNameText = serverPlayerEntity.getDisplayName().copy();
            url = playerNameText.append(": ").append(url);
        }
        MessageUtils.broadcastTextMessage(source, url);
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
        if (CarpetOrgAdditionSettings.canParseWayPoint) {
            MessageUtils.broadcastStringMessage(player, mutableText.getString(), false);
        } else {
            MessageUtils.broadcastTextMessage(player, mutableText);
        }
        return 1;
    }
}
