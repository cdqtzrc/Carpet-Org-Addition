package org.carpet_org_addition.util.task;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class DrawParticleLineTask extends ServerTask {
    private final ServerWorld world;
    private final ParticleEffect particleEffect;
    private final double distance;
    // 粒子线的起点
    private final Vec3d from;
    // 粒子线延伸的方向
    private final Vec3d vector;
    private Vec3d origin = new Vec3d(0.0, 0.0, 0.0);

    public DrawParticleLineTask(ServerWorld world, ParticleEffect particleEffect, Vec3d from, Vec3d to) {
        this.world = world;
        this.particleEffect = particleEffect;
        this.from = from;
        this.distance = from.squaredDistanceTo(to);
        this.vector = to.subtract(this.from).normalize();
    }

    @Override
    public void tick() {
        // TODO 调整粒子速度
        // 每一个游戏刻内需要绘制的距离
        double tickDistance = Math.sqrt(distance) / 20;
        double sum = 0;
        // 每次绘制0.5格，直到总距离达到每一个游戏刻内需要绘制的距离
        while (sum < tickDistance) {
            this.spawnParticles();
            this.origin = this.origin.add(this.vector.multiply(0.5));
            sum += 0.5;
        }
    }

    // 生成粒子效果
    private void spawnParticles() {
        this.world.spawnParticles(this.particleEffect,
                this.from.x + this.origin.x,
                this.from.y + this.origin.y,
                this.from.z + this.origin.z,
                5, 0, 0, 0, 1);
    }

    @Override
    public boolean stopped() {
        // TODO 粒子端点距离玩家过远时结束绘制
        return this.distance <= this.origin.lengthSquared();
    }

    @Override
    public String toString() {
        return "绘制粒子线";
    }
}
