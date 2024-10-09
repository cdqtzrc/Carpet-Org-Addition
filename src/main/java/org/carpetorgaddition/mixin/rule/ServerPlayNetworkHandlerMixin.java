package org.carpetorgaddition.mixin.rule;

import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.carpetorgaddition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.time.Instant;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    // 禁用聊天数据包顺序检测
    @Inject(method = "isInProperOrder", at = @At("HEAD"), cancellable = true)
    private void isInProperOrder(Instant timestamp, CallbackInfoReturnable<Boolean> cir) {
        if (CarpetOrgAdditionSettings.disableOutOfOrderChatCheck) {
            cir.setReturnValue(true);
        }
    }
}
