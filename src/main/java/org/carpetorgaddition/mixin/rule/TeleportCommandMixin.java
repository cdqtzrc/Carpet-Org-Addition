package org.carpetorgaddition.mixin.rule;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.TeleportCommand;
import org.carpetorgaddition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TeleportCommand.class)
public class TeleportCommandMixin {
    // 开放/tp命令权限
    @Inject(method = "method_13763", at = @At("HEAD"), cancellable = true)
    private static void tpPermissions(ServerCommandSource source, CallbackInfoReturnable<Boolean> cir) {
        if (CarpetOrgAdditionSettings.openTpPermissions) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "method_13764", at = @At("HEAD"), cancellable = true)
    private static void teleportPermissions(ServerCommandSource source, CallbackInfoReturnable<Boolean> cir) {
        if (CarpetOrgAdditionSettings.openTpPermissions) {
            cir.setReturnValue(true);
        }
    }
}
