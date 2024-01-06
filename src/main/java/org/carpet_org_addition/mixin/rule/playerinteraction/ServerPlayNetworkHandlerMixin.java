package org.carpet_org_addition.mixin.rule.playerinteraction;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.util.MathUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    //修改方块最大可交互距离
    @WrapOperation(method = "onPlayerInteractBlock", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;MAX_BREAK_SQUARED_DISTANCE:D"))
    private double onPlayerInteractBlock(Operation<Double> original) {
        if (MathUtils.isDefaultDistance()) {
            return original.call();
        }
        return MathUtils.getMaxBreakSquaredDistance();
    }

    //修改方块交互距离第二次检测
    @WrapOperation(method = "onPlayerInteractBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;squaredDistanceTo(DDD)D"))
    private double squaredDistance(ServerPlayerEntity instance, double x, double y, double z, Operation<Double> original) {
        double distance = original.call(instance, x, y, z);
        if (MathUtils.isDefaultDistance()) {
            return distance;
        }
        return distance - MathUtils.getMaxBreakSquaredDistance();
    }

    //修改实体最大交互距离
    @WrapOperation(method = "onPlayerInteractEntity", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;MAX_BREAK_SQUARED_DISTANCE:D"))
    private double onPlayerInteractEntity(Operation<Double> original) {
        if (CarpetOrgAdditionSettings.maxBlockPlaceDistanceReferToEntity) {
            return MathUtils.getMaxBreakSquaredDistance();
        }
        return original.call();
    }
}
