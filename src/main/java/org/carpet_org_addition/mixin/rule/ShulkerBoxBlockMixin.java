package org.carpet_org_addition.mixin.rule;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.util.MessageUtils;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.WorldUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(ShulkerBoxBlock.class)
public class ShulkerBoxBlockMixin {

    // 强制开启潜影盒
    @Inject(method = "canOpen", at = @At(value = "HEAD"), cancellable = true)
    private static void canOpen(BlockState state, World world, BlockPos pos, ShulkerBoxBlockEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (CarpetOrgAdditionSettings.openShulkerBoxForcibly) {
            cir.setReturnValue(true);
        }
    }

    // CCE更新抑制器
    @Inject(method = "getComparatorOutput", at = @At("HEAD"))
    private void getComparatorOutput(BlockState state, World world, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        //!world.isClient： 更新抑制不在客户端进行，防止客户端游戏崩溃
        if (canUpdateSuppression(world, pos) && !world.isClient) {
            throw new ClassCastException("类型转换异常，在:"
                    + world.getRegistryKey().getValue() + " " + WorldUtils.toPosString(pos));
        }
    }

    // 潜影盒是否可以更新抑制
    @Unique
    private static boolean canUpdateSuppression(World world, BlockPos pos) {
        if (CarpetOrgAdditionSettings.CCEUpdateSuppression) {
            if (world.getBlockEntity(pos) instanceof ShulkerBoxBlockEntity shulkerBoxBlockEntity) {
                String blockName = shulkerBoxBlockEntity.getDisplayName().getString();
                return "更新抑制器".equals(blockName) || "updateSuppression".equalsIgnoreCase(blockName);
            }
        }
        return false;
    }

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        // 提示玩家不能打开用于更新抑制的潜影盒
        if (canUpdateSuppression(world, pos)) {
            MessageUtils.sendTextMessageToHud(player, TextUtils.getTranslate("carpet.rule.message.CCEUpdateSuppression"));
            cir.setReturnValue(ActionResult.CONSUME);
        }
    }
}
