package org.carpet_org_addition.mixin.rule;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TurtleEggBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TurtleEggBlock.class)
public abstract class TurtleEggBlockMixin extends Block {
    @Shadow
    @Final
    public static IntProperty EGGS;

    public TurtleEggBlockMixin(Settings settings) {
        super(settings);
    }

    //海龟蛋快速孵化
    @Inject(method = "shouldHatchProgress", at = @At("HEAD"), cancellable = true)
    private void progress(World world, CallbackInfoReturnable<Boolean> cir) {
        if (CarpetOrgAdditionSettings.turtleEggFastHatch) {
            cir.setReturnValue(true);
        }
    }

    // 海龟蛋快速采集
    @Inject(method = "afterBreak", at = @At("HEAD"), cancellable = true)
    private void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack tool, CallbackInfo ci) {
        if (CarpetOrgAdditionSettings.turtleEggFastMine) {
            for (int i = 0; i < state.get(EGGS); i++) {
                super.afterBreak(world, player, pos, state, blockEntity, tool);
            }
            // 播放海龟蛋破坏音效
            world.playSound(null, pos, SoundEvents.ENTITY_TURTLE_EGG_BREAK, SoundCategory.BLOCKS, 0.7F, 0.9F + world.random.nextFloat() * 0.2F);
            ci.cancel();
        }
    }
}
