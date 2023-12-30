package org.carpet_org_addition.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.carpet_org_addition.CarpetOrgAddition;

import java.util.ArrayList;
import java.util.Objects;

public class MessageUtils {
    private MessageUtils() {
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
     * 让一个玩家发送带有特殊样式的文本，文本会显示在屏幕中下方的HUD上，文本内容仅对消息发送者可见
     *
     * @param player  要发送文本消息的玩家
     * @param message 发送文本消息的内容
     */
    public static void sendTextMessageToHud(PlayerEntity player, Text message) {
        player.sendMessage(message, true);
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
     * @param player            1.通过这个服务器命令源对象获取玩家管理器对象，然后通过玩家管理器对象发送消息，player不是消息的发送者。<br/>
     *                          2.如果containPlayerName为true，用来在消息前追加玩家名
     * @param message           消息的内容
     * @param containPlayerName 是否在消息前追加玩家名
     */
    public static void broadcastStringMessage(PlayerEntity player, String message, boolean containPlayerName) {
        try {
            PlayerManager playerManager = Objects.requireNonNull(player.getServer()).getPlayerManager();
            playerManager.broadcast(
                    containPlayerName ? TextUtils.appendAll(player.getDisplayName(), message)
                            : Text.literal(message), false
            );
        } catch (NullPointerException e) {
            CarpetOrgAddition.LOGGER.error("无法通过玩家获取服务器对象", e);
        }
    }

    /**
     * 广播一条带有特殊样式的文本消息
     *
     * @param player  通过这个玩家对象获取玩家管理器对象，然后通过玩家管理器对象发送消息，player不是消息的发送者
     * @param message 要广播消息的内容
     */
    public static void broadcastTextMessage(PlayerEntity player, Text message) {
        try {
            PlayerManager playerManager = Objects.requireNonNull(player.getServer()).getPlayerManager();
            playerManager.broadcast(message, false);
        } catch (NullPointerException e) {
            CarpetOrgAddition.LOGGER.error("无法通过玩家获取服务器对象", e);
        }
    }

    /**
     * 广播一条带有特殊样式的文本消息
     *
     * @param source  通过这个服务器命令源对象获取玩家管理器对象，然后通过玩家管理器对象发送消息，source不是消息的发送者
     * @param message 要广播消息的内容
     */
    public static void broadcastTextMessage(ServerCommandSource source, Text message) {
        try {
            PlayerManager playerManager = Objects.requireNonNull(source.getServer()).getPlayerManager();
            playerManager.broadcast(message, false);
        } catch (NullPointerException e) {
            CarpetOrgAddition.LOGGER.error("无法通过服务器命令源获取服务器对象", e);
        }
    }

    /**
     * 发送一条可以被翻译的消息做为命令的执行反馈，消息内容仅消息发送者可见
     */
    public static void sendCommandFeedback(ServerCommandSource source, String key, Object... obj) {
        MessageUtils.sendTextMessage(source, TextUtils.getTranslate(key, obj));
    }

    /**
     * 发送多条带有特殊样式的消息，每一条消息单独占一行，消息内容仅发送者可见
     *
     * @param source 消息的发送者，消息内容仅发送者可见
     * @param list   存储所有要发送的消息的集合
     */
    public static void sendListMessage(ServerCommandSource source, ArrayList<MutableText> list) {
        for (MutableText mutableText : list) {
            sendTextMessage(source, mutableText);
        }
    }
}
