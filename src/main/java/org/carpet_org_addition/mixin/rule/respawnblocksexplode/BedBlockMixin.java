package org.carpet_org_addition.mixin.rule.respawnblocksexplode;

import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
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

//禁止重生方块爆炸
@Mixin(BedBlock.class)
public class BedBlockMixin {
    //禁止床爆炸
    @Inject(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BedBlock;isBedWorking(Lnet/minecraft/world/World;)Z"), cancellable = true)
    private void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (CarpetOrgAdditionSettings.disableRespawnBlocksExplode && !BedBlock.isBedWorking(world)) {
            MessageUtils.sendTextMessageToHud(player, TextUtils.translate("carpet.rule.message.disableRespawnBlocksExplode"));
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }
}
