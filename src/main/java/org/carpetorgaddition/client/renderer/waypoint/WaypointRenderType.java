package org.carpetorgaddition.client.renderer.waypoint;

import net.minecraft.util.Identifier;
import org.carpetorgaddition.client.util.ClientCommandUtils;

public enum WaypointRenderType {
    /**
     * 高亮
     */
    HIGHLIGHT(Identifier.ofVanilla("textures/map/decorations/red_x.png"), 60000L, 1000L),
    /**
     * 导航
     */
    NAVIGATOR(Identifier.ofVanilla("textures/map/decorations/target_x.png"), -1L, -1L);
    /**
     * 路径点图标
     */
    private final Identifier icon;
    /**
     * 路径点持续时间
     */
    private final long durationTime;
    /**
     * 路径点消失时间
     */
    private final long vanishingTime;

    WaypointRenderType(Identifier identifier, long durationTime, long vanishingTime) {
        this.icon = identifier;
        this.durationTime = durationTime;
        this.vanishingTime = vanishingTime;
    }

    /**
     * @return 获取路径点的图标
     */
    public Identifier getIcon() {
        return this.icon;
    }

    /**
     * @return 获取路径点的持续时间
     */
    public long getDurationTime() {
        return this.durationTime;
    }

    /**
     * @return 获取路径点的消失时间
     */
    public long getVanishingTime() {
        return this.vanishingTime;
    }

    /**
     * 获取路径点大小
     *
     * @param distance  摄像机到路径点的距离，用来抵消远小近大
     * @param startTime 路径点开始渲染的时间
     */
    public float getScale(double distance, long startTime) {
        float scale = (float) distance / 30F;
        if (this.vanishingTime > 0) {
            long currentTimeMillis = System.currentTimeMillis();
            long duration = startTime + this.durationTime;
            if (currentTimeMillis < duration) {
                return scale;
            }
            // 剩余消失时间
            long remainingTime = (duration + this.vanishingTime) - currentTimeMillis;
            if (remainingTime < 0) {
                return 0;
            }
            // 让消失动画先慢后快
            float x = remainingTime / (float) this.vanishingTime;
            float cubic = x * x;
            // 消失动画（缩放）
            return scale * cubic;
        } else {
            return scale;
        }
    }

    /**
     * 清除高亮路径点
     */
    public void clear() {
        switch (this) {
            // 直接删除，EnumMap不会引发并发修改异常
            case HIGHLIGHT -> WaypointRenderManager.clearRender(HIGHLIGHT);
            // 请求服务器停止发送路径点更新数据包
            case NAVIGATOR -> {
                ClientCommandUtils.sendCommand("navigate stop");
                WaypointRenderManager.clearRender(NAVIGATOR);
            }
        }
    }

    /**
     * @return 获取日志名称
     * @apiNote 不要在游戏中使用
     */
    public String getLogName() {
        String name = switch (this) {
            case HIGHLIGHT -> "高亮";
            case NAVIGATOR -> "导航";
        };
        return "路径点（" + name + "）";
    }
}
