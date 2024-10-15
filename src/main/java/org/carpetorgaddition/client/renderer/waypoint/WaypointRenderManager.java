package org.carpetorgaddition.client.renderer.waypoint;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;

public class WaypointRenderManager {
    private static final EnumMap<WaypointRenderType, WaypointRender> RENDERS = new EnumMap<>(WaypointRenderType.class);

    public static void register() {
        // 注册路径点渲染器
        WorldRenderEvents.LAST.register(WaypointRenderManager::frame);
    }

    private static void frame(WorldRenderContext context) {
        RENDERS.forEach((key, value) -> value.drawWaypoint(context));
        RENDERS.entrySet().removeIf(entry -> entry.getValue().endRendering());
    }

    public static void setRender(WaypointRender render) {
        RENDERS.put(render.getRenderType(), render);
    }

    @Nullable
    public static WaypointRender getRender(WaypointRenderType type) {
        return RENDERS.get(type);
    }

    public static void clearRender(WaypointRenderType type) {
        RENDERS.remove(type);
    }

    public static void clearAllRender() {
        RENDERS.clear();
    }
}
