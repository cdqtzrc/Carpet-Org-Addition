package org.carpet_org_addition.mixin.rule;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.rulevalue.BetterTotemOfUndying;
import org.carpet_org_addition.util.InventoryUtils;
import org.carpet_org_addition.util.matcher.ItemMatcher;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    //创造玩家免疫/kill
    @Inject(method = "kill", at = @At("HEAD"), cancellable = true)
    private void kill(CallbackInfo ci) {
        if (CarpetOrgAdditionSettings.creativeImmuneKill) {
            LivingEntity livingEntity = (LivingEntity) (Object) this;
            if (livingEntity instanceof PlayerEntity player) {
                if (player.isCreative()) {
                    ci.cancel();
                }
            }
        }
    }

    //禁用伤害免疫
    @WrapOperation(method = "damage", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;timeUntilRegen:I", opcode = Opcodes.GETFIELD))
    private int setTimeUntilRegen(LivingEntity instance, Operation<Integer> original) {
        if (CarpetOrgAdditionSettings.disableDamageImmunity) {
            return 0;
        }
        return original.call(instance);
    }

    // 增强不死图腾
    @Inject(method = "tryUseTotem", at = @At("HEAD"), cancellable = true)
    private void tryUseTotem(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        // 在一开始就对规则是否开启进行判断，这样当其他Mod也修改了此段代码时，就可以通过关闭改规则来保障其他Mod的正常运行
        if (CarpetOrgAdditionSettings.betterTotemOfUndying == BetterTotemOfUndying.FALSE) {
            return;
        }
        LivingEntity thisLivingEntity = (LivingEntity) (Object) this;
        if (source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            cir.setReturnValue(false);
            return;
        }
        ItemStack itemStack = null;
        for (Hand hand : Hand.values()) {
            ItemStack itemStack2 = thisLivingEntity.getStackInHand(hand);
            if (!itemStack2.isOf(Items.TOTEM_OF_UNDYING)) continue;
            itemStack = itemStack2.copy();
            itemStack2.decrement(1);
            break;
        }
        // 从玩家物品栏寻找不死图腾
        if (itemStack == null && thisLivingEntity instanceof PlayerEntity playerEntity) {
            DefaultedList<ItemStack> mainInventory = playerEntity.getInventory().main;
            for (ItemStack totemOfUndying : mainInventory) {
                if (totemOfUndying.isOf(Items.TOTEM_OF_UNDYING)) {
                    itemStack = totemOfUndying.copy();
                    totemOfUndying.decrement(1);
                    break;
                } else if (CarpetOrgAdditionSettings.betterTotemOfUndying == BetterTotemOfUndying.SHULKER_BOX
                        && InventoryUtils.isShulkerBoxItem(totemOfUndying)) {
                    // 从潜影盒中获取不死图腾
                    ItemStack itemInTheBox = InventoryUtils.pickItemFromShulkerBox(totemOfUndying, new ItemMatcher(Items.TOTEM_OF_UNDYING));
                    if (itemInTheBox.isEmpty()) {
                        continue;
                    }
                    itemStack = itemInTheBox.copy();
                    // 处理潜影盒中堆叠的不死图腾
                    if (itemInTheBox.getCount() > 1) {
                        itemStack.setCount(1);
                        itemInTheBox.decrement(1);
                        // 插入到玩家物品栏中
                        playerEntity.getInventory().insertStack(itemInTheBox);
                        if (itemInTheBox.isEmpty()) {
                            break;
                        }
                        // 丢出剩余的不死图腾
                        playerEntity.dropItem(itemInTheBox.copy(), false, false);
                        itemInTheBox.setCount(0);
                    }
                    break;
                }
            }
        }
        if (itemStack != null) {
            if (thisLivingEntity instanceof ServerPlayerEntity serverPlayerEntity) {
                serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(Items.TOTEM_OF_UNDYING));
                Criteria.USED_TOTEM.trigger(serverPlayerEntity, itemStack);
            }
            thisLivingEntity.setHealth(1.0f);
            thisLivingEntity.clearStatusEffects();
            thisLivingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1));
            thisLivingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1));
            thisLivingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 800, 0));
            thisLivingEntity.getWorld().sendEntityStatus(thisLivingEntity, EntityStatuses.USE_TOTEM_OF_UNDYING);
        }
        cir.setReturnValue(itemStack != null);
    }
}