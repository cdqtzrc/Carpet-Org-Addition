package org.carpet_org_addition.mixin.util;

import net.minecraft.server.PlayerManager;
import net.minecraft.text.Text;
import org.carpet_org_addition.CarpetOrgAddition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(method = "broadcast(Lnet/minecraft/text/Text;Z)V", at = @At("HEAD"), cancellable = true)
    private void broadcast(Text message, boolean overlay, CallbackInfo ci) {
        if (CarpetOrgAddition.hiddenLoginMessages) {
            ci.cancel();
        }
    }
}
