package org.carpet_org_addition.mixin.rule;

import net.minecraft.server.command.SeedCommand;
import net.minecraft.server.command.ServerCommandSource;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SeedCommand.class)
public class SeedCommandMixin {
    // 开放/seed权限
    @Inject(method = "method_13618", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/command/ServerCommandSource;hasPermissionLevel(I)Z"), cancellable = true)
    private static void privilege(boolean bl, ServerCommandSource source, CallbackInfoReturnable<Boolean> cir) {
        if (CarpetOrgAdditionSettings.openSeedPermissions) {
            cir.setReturnValue(true);
        }
    }
}
