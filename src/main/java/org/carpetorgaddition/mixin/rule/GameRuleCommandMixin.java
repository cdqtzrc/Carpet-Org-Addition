package org.carpetorgaddition.mixin.rule;

import net.minecraft.server.command.GameRuleCommand;
import net.minecraft.server.command.ServerCommandSource;
import org.carpetorgaddition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRuleCommand.class)
public class GameRuleCommandMixin {
    //开放/gamerule命令权限
    @Inject(method = "method_13393", at = @At("HEAD"), cancellable = true)
    private static void privilege(ServerCommandSource source, CallbackInfoReturnable<Boolean> cir) {
        if (CarpetOrgAdditionSettings.openGameRulePermissions) {
            cir.setReturnValue(true);
        }
    }
}
