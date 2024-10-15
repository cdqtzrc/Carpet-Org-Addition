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
     * 路径点图标，来自原版地图
     */
    private final Identifier icon;
    private final long durationTime;
    private final long vanishingTime;

    WaypointRenderType(Identifier identifier, long durationTime, long vanishingTime) {
        this.icon = identifier;
        this.durationTime = durationTime;
        this.vanishingTime = vanishingTime;
    }

    public Identifier getIcon() {
        return this.icon;
    }

    public long getVanishingTime() {
        return this.vanishingTime;
    }

    public long getDurationTime() {
        return this.durationTime;
    }

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
