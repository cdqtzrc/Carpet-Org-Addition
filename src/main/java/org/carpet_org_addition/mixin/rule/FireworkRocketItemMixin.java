package org.carpet_org_addition.mixin.rule;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireworkRocketItem.class)
public abstract class FireworkRocketItemMixin {
    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    private void useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        PlayerEntity player = context.getPlayer();
        if (player == null) {
            return;
        }
        //不能在飞行时对方块使用烟花
        if (CarpetOrgAdditionSettings.flyingUseOnBlockFirework) {
            if (player.isFallFlying()) {
                cir.setReturnValue(ActionResult.PASS);
                return;
            }
        }
        //烟花火箭使用冷却(对方块使用)
        if (CarpetOrgAdditionSettings.fireworkRocketUseCooldown && !player.isFallFlying()) {
            PlayerEntity user = context.getPlayer();
            if (user != null) {
                user.getItemCooldownManager().set((FireworkRocketItem) (Object) this, 5);
            }
        }
    }

    //烟花火箭使用冷却(使用鞘翅飞行时)
    @Inject(method = "use", at = @At("HEAD"))
    private void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        if (CarpetOrgAdditionSettings.fireworkRocketUseCooldown && user != null && user.isFallFlying()) {
            user.getItemCooldownManager().set((FireworkRocketItem) (Object) this, 5);
        }
    }
}