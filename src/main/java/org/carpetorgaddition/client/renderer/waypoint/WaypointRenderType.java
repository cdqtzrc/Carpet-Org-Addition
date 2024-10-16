package org.carpetorgaddition.client.renderer.waypoint;

import net.minecraft.util.Identifier;

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
}
