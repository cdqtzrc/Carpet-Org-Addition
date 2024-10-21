package org.carpetorgaddition.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import org.carpetorgaddition.client.command.DictionaryCommand;
import org.carpetorgaddition.client.command.HighlightCommand;
import org.carpetorgaddition.client.command.argument.ClientBlockPosArgumentType;
import org.carpetorgaddition.client.renderer.waypoint.WaypointRender;
import org.carpetorgaddition.client.renderer.waypoint.WaypointRenderManager;
import org.carpetorgaddition.client.renderer.waypoint.WaypointRenderType;
import org.carpetorgaddition.network.WaypointClearS2CPack;
import org.carpetorgaddition.network.WaypointUpdateS2CPack;

public class CarpetOrgAdditionClientRegister {
    public static void register() {
        registerCommand();
        registerCommandArgument();
        registerC2SNetworkPack();
        registerNetworkPackReceiver();
        registerRender();
    }

    /**
     * 注册客户端命令
     */
    private static void registerCommand() {
        // 高亮路径点命令
        HighlightCommand.register();
        // 字典命令
        DictionaryCommand.register();
    }

    /**
     * 注册客户端命令参数
     */
    private static void registerCommandArgument() {
        // 客户端方块坐标命令参数
        ClientBlockPosArgumentType.register();
    }

    /**
     * 注册客户端到服务端的数据包
     */
    private static void registerC2SNetworkPack() {
    }

    /**
     * 注册数据包接收器
     */
    private static void registerNetworkPackReceiver() {
        // 注册路径点更新数据包
        ClientPlayNetworking.registerGlobalReceiver(WaypointUpdateS2CPack.ID, (payload, context) -> WaypointRenderManager.setRender(new WaypointRender(WaypointRenderType.NAVIGATOR, payload.target(), payload.worldId())));
        // 注册路径点清除数据包
        ClientPlayNetworking.registerGlobalReceiver(WaypointClearS2CPack.ID, ((payload, context) -> WaypointRenderManager.setFade(WaypointRenderType.NAVIGATOR)));
    }

    /**
     * 注册渲染器
     */
    private static void registerRender() {
        // 注册路径点渲染器
        WaypointRenderManager.register();
    }
}
