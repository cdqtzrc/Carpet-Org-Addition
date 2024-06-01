package org.carpet_org_addition.command;

import carpet.utils.CommandHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.carpet_org_addition.CarpetOrgAddition;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.util.CommandUtils;
import org.carpet_org_addition.util.MathUtils;

public class CreeperCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("creeper")
                .requires(source -> CommandHelper.canUseCommand(source, CarpetOrgAdditionSettings.commandCreeper))
                .then(CommandManager.argument("player", EntityArgumentType.player())
                        .executes(CreeperCommand::creeperExplosion)));
    }

    // 创建苦力怕并爆炸
    private static int creeperExplosion(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity targetPlayer = CommandUtils.getArgumentPlayer(context);
        World world = targetPlayer.getWorld();
        // 创建苦力怕对象
        CreeperEntity creeper = new CreeperEntity(EntityType.CREEPER, world);
        // 产生爆炸
        targetPlayer.getWorld().createExplosion(creeper,
                targetPlayer.getX() + MathUtils.randomInt(-3, 3),
                targetPlayer.getY() + MathUtils.randomInt(-1, 1),
                targetPlayer.getZ() + MathUtils.randomInt(-3, 3),
                3F, false, World.ExplosionSourceType.NONE);
        // 删除这只苦力怕
        creeper.discard();
        ServerPlayerEntity sourcePlayer = context.getSource().getPlayer();
        if (sourcePlayer != null) {
            CarpetOrgAddition.LOGGER.info("{}在{}周围制造了一场苦力怕爆炸",
                    sourcePlayer.getName().getString(), targetPlayer.getName().getString());
        }
        return 1;
    }
}
