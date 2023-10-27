package org.carpet_org_addition.command;

import carpet.utils.CommandHelper;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

import java.util.Random;

public class CreeperCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("creeper").requires(source ->
                        CommandHelper.canUseCommand(source, /*CarpetOrgAdditionSettings.commandCreeper*/false)).then(CommandManager.argument("player", EntityArgumentType.player()).executes(context -> {
                            playCreeperSound(EntityArgumentType.getPlayer(context, "player"));
                            return 1;
                        }).then(CommandManager.literal("damage").executes(context -> {
                            creeperExplosion(EntityArgumentType.getPlayer(context, "player"), false);
                            return 1;
                        }).then(CommandManager.literal("attack").executes(context -> {
                            creeperExplosion(EntityArgumentType.getPlayer(context, "player"), true);
                            return 1;
                        })))
                )
        );
    }

    //播放苦力怕爆炸前引信音效
    private static void playCreeperSound(PlayerEntity player) {
        player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ENTITY_CREEPER_PRIMED, SoundCategory.HOSTILE, 1.0F, 0.5F);
    }

    /*
    让苦力怕对玩家造成伤害
    normalAttack：true 造成普通伤害（有击退）
    false 造成爆炸伤害（无击退）
    */
    private static void creeperExplosion(PlayerEntity player, boolean normalAttack) {
        World world = player.getWorld();
        //创建苦力怕对象
        CreeperEntity creeper = new CreeperEntity(EntityType.CREEPER, world);
        //将苦力怕移动至玩家位置,然后向各个水平方向偏移（-3）~3格，向垂直方向偏移（-1）~1格
        Random r = new Random();
        creeper.refreshPositionAndAngles(player.getBlockPos().up(r.nextInt(2) - 1).east(r.nextInt(6) - 3).north(r.nextInt(6) - 3), player.getYaw(), player.getPitch());
        //苦力怕爆炸音效
        world.playSound(null, creeper.getX(), creeper.getY(), creeper.getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1.0F, 1.0F);
        //产生爆炸粒子效果
        ((ServerWorld) world).spawnParticles(ParticleTypes.EXPLOSION_EMITTER, creeper.getX(), creeper.getY(), creeper.getZ(), 1, 1.0, 0.0, 0.0, 1);
        //让苦力怕对玩家造成2-5点普通攻击或爆炸伤害（取决于boolean normalAttack）
        player.damage(normalAttack ? player.getDamageSources().mobAttack(creeper) : player.getDamageSources().explosion(creeper, creeper), (float) new Random().nextInt(3) + 2);
        //删除这只苦力怕
        creeper.remove(Entity.RemovalReason.DISCARDED);
    }
}
