package org.carpetorgaddition.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.carpetorgaddition.CarpetOrgAddition;
import org.carpetorgaddition.util.WorldUtils;

/**
 * 导航点更新数据包
 *
 * @param target  导航点的目标
 * @param worldId 导航点所在维度
 */
public record WaypointUpdateS2CPack(Vec3d target, String worldId) implements CustomPayload {
    private static final Identifier WAYPOINT_UPDATE = Identifier.of(CarpetOrgAddition.MOD_ID, "waypoint_update");
    public static final Id<WaypointUpdateS2CPack> ID = new Id<>(WAYPOINT_UPDATE);
    public static PacketCodec<RegistryByteBuf, WaypointUpdateS2CPack> CODEC = new PacketCodec<>() {
        @Override
        public WaypointUpdateS2CPack decode(RegistryByteBuf buf) {
            long[] arr = buf.readLongArray();
            Vec3d vec3d = new Vec3d(arr[0] / 100.0, arr[1] / 100.0, arr[2] / 100.0);
            return new WaypointUpdateS2CPack(vec3d, buf.readString());
        }

        @Override
        public void encode(RegistryByteBuf buf, WaypointUpdateS2CPack value) {
            long[] arr = {
                    (long) (value.target().getX() * 100),
                    (long) (value.target().getY() * 100),
                    (long) (value.target().getZ() * 100)};
            buf.writeLongArray(arr);
            buf.writeString(value.worldId);
        }
    };

    public WaypointUpdateS2CPack(Vec3d target, World world) {
        this(target, WorldUtils.getDimensionId(world));
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}