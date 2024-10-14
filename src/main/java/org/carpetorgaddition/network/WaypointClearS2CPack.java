package org.carpetorgaddition.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.carpetorgaddition.CarpetOrgAddition;
import org.carpetorgaddition.util.GameUtils;

/**
 * 导航点清除数据包
 */
public record WaypointClearS2CPack() implements CustomPayload {
    private static final Identifier WAYPOINT_CLEAR = Identifier.of(CarpetOrgAddition.MOD_ID, "waypoint_clear");
    public static final CustomPayload.Id<WaypointClearS2CPack> ID = new CustomPayload.Id<>(WAYPOINT_CLEAR);
    public static PacketCodec<RegistryByteBuf, WaypointClearS2CPack> CODEC
            = PacketCodec.of((buf, value) -> GameUtils.pass(), buf -> new WaypointClearS2CPack());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
