package org.carpetorgaddition.client.command;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.util.math.Vec3d;
import org.carpetorgaddition.client.command.argument.ClientBlockPosArgumentType;
import org.carpetorgaddition.client.renderer.waypoint.WaypointRender;
import org.carpetorgaddition.client.renderer.waypoint.WaypointRenderManager;
import org.carpetorgaddition.client.renderer.waypoint.WaypointRenderType;

public class HighlightCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(ClientCommandManager.literal("highlight")
                        .then(ClientCommandManager.argument("blockPos", ClientBlockPosArgumentType.blockPos())
                                .executes(HighlightCommand::highlight))
                        .then(ClientCommandManager.literal("clear")
                                .executes(context -> clear()))));
    }

    // 高亮路径点
    private static int highlight(CommandContext<FabricClientCommandSource> context) {
        Vec3d vec3d = ClientBlockPosArgumentType.getBlockPos(context, "blockPos").toCenterPos();
        ClientWorld world = context.getSource().getWorld();
        // 获取旧路径点
        WaypointRender oldRender = WaypointRenderManager.getRender(WaypointRenderType.HIGHLIGHT);
        // 创建新路径点
        WaypointRender newRender = new WaypointRender(WaypointRenderType.HIGHLIGHT, vec3d, world);
        // 如果两个路径点指向同一个位置，就让玩家看向该路径点
        if (newRender.equals(oldRender)) {
            // if语句结束后仍要设置新路径点，因为要重置持续时间
            context.getSource().getEntity().lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, oldRender.getPos());
        }
        // 设置新的路径点
        WaypointRenderManager.setRender(newRender);
        return 1;
    }

    // 取消高亮路径点
    private static int clear() {
        WaypointRenderManager.clearRender(WaypointRenderType.HIGHLIGHT);
        return 1;
    }
}
