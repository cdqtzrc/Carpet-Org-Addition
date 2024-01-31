package org.carpet_org_addition.mixin.rule.carpet;

import carpet.commands.PlayerCommand;
import carpet.patches.EntityPlayerMPFake;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.util.CommandUtils;
import org.carpet_org_addition.util.fakeplayer.FakePlayerProtectManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerCommand.class)
class PlayerCommandMixin {
    //假玩家保护
    @Inject(method = "kill", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;kill()V"))
    private static void killPlayer(CommandContext<ServerCommandSource> context, CallbackInfoReturnable<Integer> cir) throws CommandSyntaxException {
        if (CarpetOrgAdditionSettings.fakePlayerProtect) {
            String playerName = StringArgumentType.getString(context, "player");
            MinecraftServer server = context.getSource().getServer();
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerName);
            if (player instanceof EntityPlayerMPFake fakePlayer && FakePlayerProtectManager.isNotKill(fakePlayer)) {
                throw CommandUtils.createException("carpet.commands.protect.player.command");
            }
        }
    }
}
