package org.carpetorgaddition.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class NetworkS2CPackRegister {
    /**
     * 注册数据包
     */
    public static void register() {
        // TODO 添加开关控制数据包是否发送
        // 更新导航点数据包
        PayloadTypeRegistry.playS2C().register(WaypointUpdateS2CPack.ID, WaypointUpdateS2CPack.CODEC);
        // 清除导航点数据包
        PayloadTypeRegistry.playS2C().register(WaypointClearS2CPack.ID, WaypointClearS2CPack.CODEC);
    }
}
