package org.carpet_org_addition.mixin.rule;

import net.minecraft.screen.AnvilScreenHandler;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 没看懂源代码是什么意思之前先不要乱改，防止产生副作用<br/>
 * Wiki上说的操作数是什么，为什么会同时有操作数和累积惩罚，操作数和累积惩罚是什么关系，任凭操作数增加会不会出现别的问题
 */
//防止铁砧过于昂贵
@Mixin(AnvilScreenHandler.class)
public class AnvilScreenHandlerMixin {

/*    @Inject(method = "canTakeOutput", at = @At("HEAD"), cancellable = true)
    private void canTakeOutput(PlayerEntity player, boolean present, CallbackInfoReturnable<Boolean> cir) {
        //能不能取下输出槽的物品
        cir.setReturnValue(false);
    }

    @Redirect(method = "onTakeOutput", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/Property;get()I"))
    private int onTakeOutput(Property property) {
        //取下输出槽物品后消耗多少级经验
        return 39;
    }

    @Redirect(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/Property;get()I"))
    private int updateResult(Property instance) {
        //如果成本超过这个值，那么输出槽会被设置为空
        return 39;
    }

    @Redirect(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getRepairCost()I"))
    private int updateResult(ItemStack itemStack) {
        return Math.min(itemStack.getRepairCost(), 39);
    }*/
}
