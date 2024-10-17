package org.carpetorgaddition.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.carpetorgaddition.CarpetOrgAddition;

public class ClientCommandUtils {
    /**
     * 让客户端玩家向服务器发送一条命令
     *
     * @param command 命令的内容，不建议以“/”开头
     */
    public static void sendCommand(String command) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            CarpetOrgAddition.LOGGER.error("尝试在游戏外发送命令");
            return;
        }
        // 发送命令，发送前移除命令的斜杠
        player.networkHandler.sendCommand(command.startsWith("/") ? command.substring(1) : command);
    }
}
