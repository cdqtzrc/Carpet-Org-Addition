package org.carpet_org_addition.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class SendMessageUtils {
    private SendMessageUtils() {
    }

    /**
     * 让一个玩家发送带有特殊样式的文本，文本内容仅对消息发送者可见
     *
     * @param player  要发送文本消息的玩家
     * @param message 发送文本消息的内容
     */
    public static void sendTextMessage(PlayerEntity player, Text message) {
        player.sendMessage(message);
    }

    /**
     * 让一个玩家发送带有特殊样式的文本，文本内容仅对消息发送者可见
     *
     * @param source  要发送文本消息的命令源
     * @param message 发送文本消息的内容
     */
    public static void sendTextMessage(ServerCommandSource source, Text message) {
        source.sendMessage(message);
    }

    /**
     * 让服务器命令源发送指定内容的消息，消息内容仅对消息发送者可见
     *
     * @param source  发送消息的消息源
     * @param message 要发送消息的内容
     */
    public static void sendStringMessage(ServerCommandSource source, String message) {
        source.sendMessage(Text.literal(message));
    }
    /**
     * 广播指定内容的消息，消息对所有玩家可见，不带冒号
     *
     * @param player            消息的发送者
     * @param message           消息的内容
     * @param containPlayerName 是否在消息前追加玩家名
     */
    public static void broadcastStringMessage(PlayerEntity player, String message, boolean containPlayerName) {
        try {
            PlayerManager playerManager = Objects.requireNonNull(player.getServer()).getPlayerManager();
            playerManager.broadcast(
                    containPlayerName ? TextUtils.appendAll(player.getDisplayName(), message) : Text.literal(message), false
            );
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 广播一条带有特殊样式的文本消息
     *
     * @param player  要广播消息的玩家
     * @param message 要广播消息的内容
     */
    public static void broadcastTextMessage(PlayerEntity player, Text message) {
        try {
            PlayerManager playerManager = Objects.requireNonNull(player.getServer()).getPlayerManager();
            playerManager.broadcast(message, false);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 广播一条带有特殊样式的文本消息
     *
     * @param source  要广播消息的命令源
     * @param message 要广播消息的内容
     */
    public static void broadcastTextMessage(ServerCommandSource source, Text message) {
        try {
            PlayerManager playerManager = Objects.requireNonNull(source.getServer()).getPlayerManager();
            playerManager.broadcast(message, false);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送一条可以被翻译的消息做为命令的执行反馈，消息内容仅消息发送者可见
     */
    public static void sendCommandFeedback(ServerCommandSource source, String key, Object... obj) {
        SendMessageUtils.sendTextMessage(source, TextUtils.getTranslate(key, obj));
    }
}
