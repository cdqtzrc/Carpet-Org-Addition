package org.carpetorgaddition.mixin.rule;

import carpet.patches.EntityPlayerMPFake;
import carpet.utils.CommandHelper;
import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.carpetorgaddition.CarpetOrgAdditionSettings;
import org.carpetorgaddition.util.CommandUtils;
import org.carpetorgaddition.util.MathUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Shadow
    public abstract HungerManager getHungerManager();

    @Unique
    private final PlayerEntity thisPlayer = (PlayerEntity) (Object) this;

    //血量不满时也可以进食
    @Inject(method = "canConsume", at = @At("HEAD"), cancellable = true)
    private void canEat(boolean ignoreHunger, CallbackInfoReturnable<Boolean> cir) {
        if (CarpetOrgAdditionSettings.healthNotFullCanEat && thisPlayer.getHealth() < thisPlayer.getMaxHealth() - 0.3//-0.3：可能生命值不满但是显示的心满了
                && this.getHungerManager().getSaturationLevel() <= 5) {
            cir.setReturnValue(true);
        }
    }

    // 快速设置假玩家合成
    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void interact(Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (thisPlayer instanceof ServerPlayerEntity serverPlayer && !thisPlayer.isSpectator()
                && CommandHelper.canUseCommand(thisPlayer.getCommandSource(),
                CarpetOrgAdditionSettings.commandPlayerAction)) {
            switch (CarpetOrgAdditionSettings.quickSettingFakePlayerCraft) {
                case FALSE:
                    break;
                case SNEAKING:
                    if (!thisPlayer.isSneaking()) {
                        break;
                    }
                case TRUE:
                    if (serverPlayer.getMainHandStack().isOf(Items.CRAFTING_TABLE)) {
                        if (entity instanceof EntityPlayerMPFake fakePlayer) {
                            CommandUtils.execute(serverPlayer, "/playerAction " + fakePlayer.getName().getString() + " craft gui");
                            cir.setReturnValue(ActionResult.SUCCESS);
                        }
                    }
                default: {
                }
            }
        }
    }

    // 最大方块交互距离
    @Inject(method = "getBlockInteractionRange", at = @At("HEAD"), cancellable = true)
    private void getBlockInteractionRange(CallbackInfoReturnable<Double> cir) {
        if (MathUtils.isDefaultDistance()) {
            return;
        }
        cir.setReturnValue(MathUtils.getPlayerMaxInteractionDistance());
    }

    // 实体交互距离
    @Inject(method = "getEntityInteractionRange", at = @At("HEAD"), cancellable = true)
    private void getEntityInteractionRange(CallbackInfoReturnable<Double> cir) {
        if (CarpetOrgAdditionSettings.maxBlockPlaceDistanceReferToEntity) {
            cir.setReturnValue(MathUtils.getPlayerMaxInteractionDistance());
        }
    }

    // 玩家死亡产生的掉落物不会自然消失
    @WrapOperation(method = "dropInventory", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;dropAll()V"))
    private void drop(PlayerInventory inventory, Operation<Void> original) {
        if (CarpetOrgAdditionSettings.playerDropsNotDespawning) {
            for (List<ItemStack> list : ImmutableList.of(inventory.main, inventory.armor, inventory.offHand)) {
                for (int i = 0; i < list.size(); ++i) {
                    ItemStack itemStack = list.get(i);
                    if (!itemStack.isEmpty()) {
                        ItemEntity itemEntity = inventory.player.dropItem(itemStack, true, false);
                        list.set(i, ItemStack.EMPTY);
                        if (itemEntity == null) {
                            continue;
                        }
                        // 设置掉落物不消失
                        itemEntity.setNeverDespawn();
                    }
                }
            }
        } else {
            // 掉落物正常消失
            original.call(inventory);
        }
    }
}