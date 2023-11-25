package org.carpet_org_addition.mixin.rule.playerinteraction;

import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.util.MathUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    //修改方块最大可交互距离
    @Redirect(method = "onPlayerInteractBlock", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;MAX_BREAK_SQUARED_DISTANCE:D"))
    private double onPlayerInteractBlock() {
        return MathUtils.getMaxBreakSquaredDistance();
    }

    //修改方块交互距离第二次检测
    @ModifyConstant(method = "onPlayerInteractBlock", constant = @Constant(doubleValue = 64.0))
    private double onPlayerInteractBlock(double constant) {
        return Math.max(MathUtils.getMaxBreakSquaredDistance(), constant);
    }

    //修改实体最大交互距离
    @Redirect(method = "onPlayerInteractEntity", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;MAX_BREAK_SQUARED_DISTANCE:D"))
    private double onPlayerInteractEntity() {
        if (CarpetOrgAdditionSettings.maxBlockPlaceDistanceReferToEntity) {
            return MathUtils.getMaxBreakSquaredDistance();
        }
        return MathUtils.getDefaultInteractionDistance();
    }
}
