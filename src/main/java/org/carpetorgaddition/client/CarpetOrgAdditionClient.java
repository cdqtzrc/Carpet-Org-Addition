package org.carpetorgaddition.client;

import net.fabricmc.api.ClientModInitializer;
import org.carpetorgaddition.client.network.NetworkPackReceiverRegister;
import org.carpetorgaddition.client.renderer.WaypointRender;

public class CarpetOrgAdditionClient implements ClientModInitializer {
    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient() {
        // 注册网络数据包接收器
        NetworkPackReceiverRegister.register();
        // 注册路径点渲染器
        WaypointRender.register();
    }
}
