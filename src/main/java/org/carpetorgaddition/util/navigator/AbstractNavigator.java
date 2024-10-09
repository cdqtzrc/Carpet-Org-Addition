package org.carpetorgaddition.util.navigator;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.carpetorgaddition.util.TextUtils;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractNavigator {
    protected static final String IN = "carpet.commands.navigate.hud.in";
    protected static final String DISTANCE = "carpet.commands.navigate.hud.distance";
    protected static final String REACH = "carpet.commands.navigate.hud.reach";
    @NotNull
    protected final ServerPlayerEntity player;
    protected final NavigatorInterface navigatorInterface;

    public AbstractNavigator(@NotNull ServerPlayerEntity player) {
        this.player = player;
        this.navigatorInterface = (NavigatorInterface) this.player;
    }

    public abstract void tick();

    /**
     * 此导航器的结束条件
     *
     * @return 导航是否需要结束
     */
    protected abstract boolean terminate();

    /**
     * @return 此导航器的浅拷贝副本
     */
    public abstract AbstractNavigator copy(ServerPlayerEntity player);

    @NotNull
    protected MutableText getHUDText(Vec3d vec3d, Text in, Text distance) {
        MutableText text;
        // 添加上下箭头
        text = switch (verticalAngle(this.player, vec3d)) {
            case 1 -> TextUtils.appendAll(in, " ↑ ", distance);
            case -1 -> TextUtils.appendAll(in, " ↓ ", distance);
            default -> TextUtils.appendAll(in, "   ", distance);
        };
        // 添加左右箭头
        text = switch (forwardAngle(this.player, vec3d)) {
            case -3 -> TextUtils.appendAll("    ", text, " >>>");
            case -2 -> TextUtils.appendAll("    ", text, "  >>");
            case -1 -> TextUtils.appendAll("    ", text, "   >");
            case 1 -> TextUtils.appendAll("<   ", text, "    ");
            case 2 -> TextUtils.appendAll("<<  ", text, "    ");
            case 3 -> TextUtils.appendAll("<<< ", text, "    ");
            default -> TextUtils.appendAll("    ", text, "    ");
        };
        return text;
    }

    /**
     * @param target 玩家看向的位置
     * @see net.minecraft.client.gui.hud.SubtitlesHud#render(DrawContext)
     */
    public static int forwardAngle(PlayerEntity player, Vec3d target) {
        double x = target.getX() - player.getX();
        double y = target.getZ() - player.getZ();
        // 将直角坐标转换为极坐标，然后获取角度
        double result = player.getYaw() + Math.toDegrees(Math.atan2(x, y));
        result = result < 0 ? result + 360 : result;
        result = result > 180 ? result - 360 : result;
        return forwardAngle(result);
    }

    private static int forwardAngle(double value) {
        if (value < 0) {
            return -forwardAngle(-value);
        }
        if (value <= 3) {
            return 0;
        }
        if (value <= 60) {
            return 1;
        }
        if (value <= 100) {
            return 2;
        }
        return 3;
    }

    // 玩家视角是否指向目标位置（仅考虑高度）
    public static int verticalAngle(PlayerEntity player, Vec3d target) {
        double x = Math.sqrt(Math.pow(player.getX() - target.getX(), 2) + Math.pow(player.getZ() - target.getZ(), 2));
        double y = target.getY() - player.getEyeY();
        double result = player.getPitch() + Math.toDegrees(Math.atan2(y, x));
        if (result >= 10) {
            return 1;
        } else if (result <= -10) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * 让玩家清除这个导航器
     */
    public void clear() {
        this.navigatorInterface.clearNavigator();
    }
}
