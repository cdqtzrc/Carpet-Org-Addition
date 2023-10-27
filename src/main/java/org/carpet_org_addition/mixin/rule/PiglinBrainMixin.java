package org.carpet_org_addition.mixin.rule;

import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//猪灵快速交易
@Mixin(PiglinBrain.class)
public abstract class PiglinBrainMixin {
    @Shadow
    protected static void consumeOffHandItem(PiglinEntity piglin, boolean barter) {
    }

    @Inject(method = "tickActivities", at = @At(value = "HEAD"))
    private static void item(PiglinEntity piglin, CallbackInfo ci) {
        if (CarpetOrgAdditionSettings.piglinFastBarter && piglin != null && (piglin.getHandItems() == Items.GOLD_INGOT || piglin.getOffHandStack().getItem() == Items.GOLD_INGOT)) {
            World world = piglin.getWorld();
            if (world.getTime() % 8 == 0) {
                consumeOffHandItem(piglin, true);
            }
        }
    }
}
