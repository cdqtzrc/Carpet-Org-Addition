package org.carpet_org_addition.mixin.rule.client.carpet;

import carpet.api.settings.SettingsManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.integrated.IntegratedServer;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//该Mixin类仅在客户端加载
@Mixin(SettingsManager.class)
public class SettingsManagerMixin {
    //开放/carpet命令权限，仅单人游戏
    @Inject(method = "lambda$registerCommand$11", at = @At("HEAD"), cancellable = true)
    private void carpet(ServerCommandSource player, CallbackInfoReturnable<Boolean> cir) {
        if (CarpetOrgAdditionSettings.openCarpetPermissions) {
            IntegratedServer clientServer = MinecraftClient.getInstance().getServer();
            if (clientServer != null) {
                cir.setReturnValue(true);
            }
        }
    }
}
