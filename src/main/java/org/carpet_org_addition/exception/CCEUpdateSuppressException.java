package org.carpet_org_addition.exception;

import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.carpet_org_addition.CarpetOrgAddition;
import org.carpet_org_addition.util.WorldUtils;

public class CCEUpdateSuppressException extends ClassCastException {
    private final BlockPos triggerPos;

    public CCEUpdateSuppressException(BlockPos blockPos, String message) {
        super(message);
        this.triggerPos = blockPos;
    }

    public void onCatch(ServerPlayerEntity player, Packet<ServerPlayPacketListener> packet) {
        StringBuilder builder = new StringBuilder();
        builder.append(player.getName().getString()).append("在");
        if (packet instanceof PlayerActionC2SPacket actionC2SPacket) {
            // 破坏方块
            switch (actionC2SPacket.getAction()) {
                // 不应该会执行到其他case块
                // 不获取方块名称是因为此时方块可能已经被破坏，不获取物品名称也是同理
                case START_DESTROY_BLOCK, ABORT_DESTROY_BLOCK, STOP_DESTROY_BLOCK -> builder.append("破坏方块");
                case DROP_ALL_ITEMS, DROP_ITEM -> builder.append("丢弃物品");
                case RELEASE_USE_ITEM -> builder.append("使用物品");
                case SWAP_ITEM_WITH_OFFHAND -> builder.append("交换主副手物品");
                default -> throw new IllegalStateException();
            }
        } else if (packet instanceof PlayerInteractBlockC2SPacket) {
            // 放置或交互方块
            builder.append("放置或交互方块");
        } else {
            // 其它异常
            builder.append("发送").append(packet.getClass().getSimpleName()).append("数据包");
        }
        String worldPos = WorldUtils.toWorldPosString(player.getWorld(), this.triggerPos);
        builder.append("时触发了CCE更新抑制，在").append(worldPos);
        CarpetOrgAddition.LOGGER.info(builder.toString());
    }
}
