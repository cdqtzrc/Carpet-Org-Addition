package org.carpetorgaddition.client.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import org.carpetorgaddition.client.renderer.waypoint.WaypointRender;
import org.carpetorgaddition.client.renderer.waypoint.WaypointRenderManager;
import org.carpetorgaddition.client.renderer.waypoint.WaypointRenderType;
import org.carpetorgaddition.network.WaypointClearS2CPack;
import org.carpetorgaddition.network.WaypointUpdateS2CPack;

public class NetworkPackReceiverRegister {
    public static void register() {
        // 注册路径点更新数据包
        ClientPlayNetworking.registerGlobalReceiver(WaypointUpdateS2CPack.ID, (payload, context) -> WaypointRenderManager.setRender(new WaypointRender(WaypointRenderType.NAVIGATOR, payload.target(), payload.worldId())));
        // 注册路径点清除数据包
        ClientPlayNetworking.registerGlobalReceiver(WaypointClearS2CPack.ID, ((payload, context) -> WaypointRenderManager.clearRender(WaypointRenderType.NAVIGATOR)));
    }
}
