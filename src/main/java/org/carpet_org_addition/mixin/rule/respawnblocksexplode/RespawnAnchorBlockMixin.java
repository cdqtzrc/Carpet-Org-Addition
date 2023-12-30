package org.carpet_org_addition.mixin.rule.respawnblocksexplode;

import net.minecraft.block.BlockState;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.util.MessageUtils;
import org.carpet_org_addition.util.TextUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RespawnAnchorBlock.class)
public class RespawnAnchorBlockMixin {
    //禁止重生锚爆炸
    @Inject(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/RespawnAnchorBlock;isNether(Lnet/minecraft/world/World;)Z"), cancellable = true)
    private void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit,
                       CallbackInfoReturnable<ActionResult> cir) {
        if (CarpetOrgAdditionSettings.disableRespawnBlocksExplode && !RespawnAnchorBlock.isNether(world)) {
            MessageUtils.sendTextMessageToHud(player, TextUtils.getTranslate("carpet.rule.message.disableRespawnBlocksExplode"));
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }
}
