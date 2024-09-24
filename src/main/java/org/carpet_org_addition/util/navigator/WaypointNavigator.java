package org.carpet_org_addition.util.navigator;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.carpet_org_addition.util.MathUtils;
import org.carpet_org_addition.util.MessageUtils;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.WorldUtils;
import org.carpet_org_addition.util.wheel.Waypoint;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class WaypointNavigator extends AbstractNavigator {
    private final Waypoint waypoint;

    // 路径点所在维度的ID
    private final String waypointDimension;

    public WaypointNavigator(@NotNull ServerPlayerEntity player, Waypoint waypoint) {
        super(player);
        this.waypoint = waypoint;
        if (this.waypoint.getBlockPos() == null) {
            throw new NullPointerException();
        }
        this.waypointDimension = waypoint.getDimension();
    }

    @Override
    public void tick() {
        if (terminate()) {
            return;
        }
        // 路径点的目标位置
        BlockPos blockPos = this.waypoint.getBlockPos();
        // 玩家所在的方块位置
        BlockPos playerBlockPos = this.player.getBlockPos();
        // 玩家所在维度
        String playerDimension = WorldUtils.getDimensionId(this.player.getWorld());
        if (playerDimension.equals(waypointDimension)) {
            // 玩家和路径点在相同的维度
            Text text = this.getHUDText(blockPos.toCenterPos(), getIn(blockPos), getDistance(playerBlockPos, blockPos));
            MessageUtils.sendTextMessageToHud(this.player, text);
        } else if (((playerDimension.equals(WorldUtils.OVERWORLD) && waypointDimension.equals(WorldUtils.THE_NETHER))
                || (playerDimension.equals(WorldUtils.THE_NETHER) && waypointDimension.equals(WorldUtils.OVERWORLD)))
                && this.waypoint.getAnotherBlockPos() != null) {
            // 玩家和路径点在不同的维度，但是维度可以互相转换
            // 将坐标设置为斜体
            Text in = TextUtils.translate(IN, waypoint.getName(),
                    TextUtils.toItalic(TextUtils.simpleBlockPos(blockPos)));
            Text text = this.getHUDText(this.waypoint.getAnotherBlockPos().toCenterPos(), in,
                    getDistance(playerBlockPos, this.waypoint.getAnotherBlockPos()));
            MessageUtils.sendTextMessageToHud(this.player, text);
        } else {
            // 玩家和路径点在不同维度
            Text dimensionName = WorldUtils.getDimensionName(WorldUtils.getWorld(this.player.getServer(),
                    this.waypoint.getDimension()));
            MutableText in = TextUtils.translate(IN, waypoint.getName(),
                    TextUtils.appendAll(dimensionName, TextUtils.simpleBlockPos(blockPos)));
            MessageUtils.sendTextMessageToHud(this.player, in);
        }
    }

    @Override
    public boolean terminate() {
        if (Objects.equals(WorldUtils.getDimensionId(this.player.getWorld()), this.waypointDimension)
                && MathUtils.getBlockIntegerDistance(this.player.getBlockPos(), this.waypoint.getBlockPos()) <= 8) {
            // 到达目的地，停止追踪
            MessageUtils.sendTextMessageToHud(this.player, TextUtils.translate(REACH));
            this.clear();
            return true;
        }
        return false;
    }

    @Override
    public WaypointNavigator copy(ServerPlayerEntity player) {
        if (this.waypoint == null || this.waypoint.getBlockPos() == null) {
            return null;
        }
        return new WaypointNavigator(player, this.waypoint);
    }

    @NotNull
    private MutableText getIn(BlockPos blockPos) {
        return TextUtils.translate(IN, waypoint.getName(), TextUtils.simpleBlockPos(blockPos));
    }

    @NotNull
    private static MutableText getDistance(BlockPos playerBlockPos, BlockPos blockPos) {
        return TextUtils.translate(DISTANCE, MathUtils.getBlockIntegerDistance(playerBlockPos, blockPos));
    }
}
