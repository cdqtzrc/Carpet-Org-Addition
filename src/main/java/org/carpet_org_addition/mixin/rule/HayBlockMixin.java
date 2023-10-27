package org.carpet_org_addition.mixin.rule;

import net.minecraft.block.HayBlock;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;


//干草捆完全抵消摔落伤害
@Mixin(HayBlock.class)
public class HayBlockMixin {
    @ModifyConstant(method = "onLandedUpon", constant = @Constant(floatValue = 0.2F))
    private float onLandedUpon(float constant) {
        if (CarpetOrgAdditionSettings.hayBlockCompleteOffsetFall) {
            return 0F;
        }
        return constant;
    }
}
