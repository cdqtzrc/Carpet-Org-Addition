package org.carpet_org_addition.command;

import carpet.utils.CommandHelper;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.util.MathUtils;

public class CreeperCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("creeper").requires(source ->
                        CommandHelper.canUseCommand(source, CarpetOrgAdditionSettings.commandCreeper))
                .then(CommandManager.argument("player", EntityArgumentType.player())
                        .executes(context -> creeperExplosion(EntityArgumentType.getPlayer(context, "player"))
                        )));
    }

    // 创建苦力怕并爆炸
    private static int creeperExplosion(ServerPlayerEntity player) {
        World world = player.getWorld();
        // 创建苦力怕对象
        CreeperEntity creeper = new CreeperEntity(EntityType.CREEPER, world);
        try {
            // 产生爆炸
            player.getWorld().createExplosion(creeper,
                    player.getX() + MathUtils.randomInt(-3, 3),
                    player.getY() + MathUtils.randomInt(-1, 1),
                    player.getZ() + MathUtils.randomInt(-3, 3),
                    3F, false, World.ExplosionSourceType.NONE);
        } finally {
            // 删除这只苦力怕
            creeper.discard();
        }
        return 1;
    }
}
