package org.carpet_org_addition.command;

import carpet.script.utils.ParticleParser;
import carpet.utils.CommandHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.util.CommandUtils;
import org.carpet_org_addition.util.MessageUtils;

public class ParticleLineCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("particleLine")
                .requires(source -> CommandHelper.canUseCommand(source, CarpetOrgAdditionSettings.commandParticleLine))
                .then(CommandManager.argument("from", Vec3ArgumentType.vec3())
                        .then(CommandManager.argument("to", Vec3ArgumentType.vec3())
                                .executes(ParticleLineCommand::draw))
                )
        );
    }

    // 准备绘制粒子线
    public static int draw(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        // 获取玩家对象
        ServerPlayerEntity player = CommandUtils.getPlayer(context);
        // 获取粒子线的起始和结束点
        Vec3d from = Vec3ArgumentType.getVec3(context, "from");
        Vec3d to = Vec3ArgumentType.getVec3(context, "to");
        // 获取粒子的效果类型
        ParticleEffect mainParticle = ParticleParser.getEffect("dust 0 0 0 1", player.getWorld().createCommandRegistryWrapper(RegistryKeys.PARTICLE_TYPE));
        // 计算粒子线的长度（平方）
        double lineLengthSq = from.squaredDistanceTo(to);
        // 计算粒子线长度
        int distance = (int) Math.round(Math.sqrt(lineLengthSq));
        if (distance == 0) {
            return 0;
        }
        // 计算暂停时间，绘制完粒子线的每一个点后暂停一会
        int waitTime = 500 / distance;
        // 在一个新的线程绘制粒子线
        DrawLineTask drawLine = new DrawLineTask(player, mainParticle, lineLengthSq, from, to, waitTime);
        drawLine.setName("DrawParticleLine");
        drawLine.start();
        // 返回值为粒子线的长度
        return distance;
    }

    static class DrawLineTask extends Thread {
        private final ServerPlayerEntity player;
        private final ParticleEffect mainParticle;
        private final double lineLengthSq;
        private final Vec3d from;
        private final Vec3d to;
        private final int waitTime;

        private DrawLineTask(ServerPlayerEntity player, ParticleEffect mainParticle, double lineLengthSq,
                             Vec3d from, Vec3d to, int waitTime) {
            this.player = player;
            this.mainParticle = mainParticle;
            this.lineLengthSq = lineLengthSq;
            this.from = from;
            this.to = to;
            this.waitTime = waitTime;
        }

        @SuppressWarnings("BusyWait")
        @Override
        public void run() {
            // 在HUD发送箭头文本
            sendArrow();
            Vec3d incvec = to.subtract(from).normalize();
            // 绘制粒子线
            for (Vec3d delta = new Vec3d(0.0, 0.0, 0.0); delta.lengthSquared() < lineLengthSq; delta = delta.add(incvec.multiply(0.5))) {
                player.getServerWorld().spawnParticles(player, mainParticle, true, delta.x + from.x, delta.y + from.y, delta.z + from.z, 5, 0.0, 0.0, 0.0, 0.0);
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }

        /**
         * 发送箭头文本用来指示方向
         *
         * @see net.minecraft.client.gui.hud.SubtitlesHud#render(DrawContext)
         */
        private void sendArrow() {
            // 获取玩家眼睛的位置
            Vec3d eyePos = player.getEyePos();
            Vec3d vec3d2 = new Vec3d(0.0, 0.0, -1.0).rotateX(-player.getPitch() * ((float) Math.PI / 180)).rotateY(-player.getYaw() * ((float) Math.PI / 180));
            Vec3d vec3d3 = new Vec3d(0.0, 1.0, 0.0).rotateX(-player.getPitch() * ((float) Math.PI / 180)).rotateY(-player.getYaw() * ((float) Math.PI / 180));
            Vec3d vec3d4 = vec3d2.crossProduct(vec3d3);
            Vec3d vec3d5 = to.subtract(eyePos).normalize();
            // 视线与垂直方向的夹角
            double verticalAngle = -vec3d4.dotProduct(vec3d5);
            double f = -vec3d2.dotProduct(vec3d5);
            if (f <= 0.5) {
                if (verticalAngle > 0.0) {
                    MessageUtils.sendTextMessageToHud(player, Text.literal("-->"));
                } else if (verticalAngle < 0.0) {
                    MessageUtils.sendTextMessageToHud(player, Text.literal("<--"));
                }
            }
        }
    }
}
