package org.carpet_org_addition.util.navigator;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.carpet_org_addition.util.MathUtils;
import org.carpet_org_addition.util.TextUtils;
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
        text = switch (MathUtils.verticalAngle(this.player, vec3d)) {
            case 1 -> TextUtils.appendAll(in, " ↑ ", distance);
            case -1 -> TextUtils.appendAll(in, " ↓ ", distance);
            default -> TextUtils.appendAll(in, "   ", distance);
        };
        // 添加左右箭头
        text = switch (MathUtils.forwardAngle(this.player, vec3d)) {
            case 3 -> TextUtils.appendAll("    ", text, " >>>");
            case 2 -> TextUtils.appendAll("    ", text, "  >>");
            case 1 -> TextUtils.appendAll("    ", text, "   >");
            case -1 -> TextUtils.appendAll("<   ", text, "    ");
            case -2 -> TextUtils.appendAll("<<  ", text, "    ");
            case -3 -> TextUtils.appendAll("<<< ", text, "    ");
            default -> TextUtils.appendAll("    ", text, "    ");
        };
        return text;
    }

    /**
     * 让玩家清除这个导航器
     */
    public void clear() {
        this.navigatorInterface.clearNavigator();
    }
}
