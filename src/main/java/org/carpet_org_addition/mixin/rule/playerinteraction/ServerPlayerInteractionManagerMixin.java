package org.carpet_org_addition.mixin.rule.playerinteraction;

import net.minecraft.server.network.ServerPlayerInteractionManager;
import org.carpet_org_addition.util.MathUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

//服务器最大玩家交互距离
@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {
    @Redirect(method = "processBlockBreakingAction", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;MAX_BREAK_SQUARED_DISTANCE:D"))
    private double processBlockBreakingAction() {
        return MathUtils.getMaxBreakSquaredDistance();
    }
}
