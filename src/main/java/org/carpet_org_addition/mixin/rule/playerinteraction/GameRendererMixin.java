package org.carpet_org_addition.mixin.rule.playerinteraction;

import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.util.MathUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

//最大方块交互距离适用于实体
@Mixin(net.minecraft.client.render.GameRenderer.class)
public class GameRendererMixin {
    @ModifyConstant(method = "updateTargetedEntity", constant = @Constant(doubleValue = 3.0))
    private double targetedDistance(double constant) {
        if (CarpetOrgAdditionSettings.maxBlockInteractionDistanceReferToEntity) {
            //服务器最大交互距离
            return MathUtils.getPlayerMaxInteractionDistance();
        }
        return constant;
    }

    @ModifyConstant(method = "updateTargetedEntity", constant = @Constant(doubleValue = 9.0))
    private double targetedSquaredDistance(double constant) {
        if (CarpetOrgAdditionSettings.maxBlockInteractionDistanceReferToEntity) {
            //服务器最大交互平方距离
            return MathUtils.getMaxBreakSquaredDistance();
        }
        return constant;
    }
}
