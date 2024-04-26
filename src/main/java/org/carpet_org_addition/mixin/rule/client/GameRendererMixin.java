package org.carpet_org_addition.mixin.rule.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.attribute.EntityAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @WrapOperation(method = "updateCrosshairTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getBlockInteractionRange()D"))
    private double getPlayerBlockInteractionRange(ClientPlayerEntity clientPlayerEntity, Operation<Double> original) {
        return clientPlayerEntity.getAttributeValue(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE);
    }
}
