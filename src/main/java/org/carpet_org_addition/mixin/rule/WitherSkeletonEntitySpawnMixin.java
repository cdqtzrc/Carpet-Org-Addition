package org.carpet_org_addition.mixin.rule;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.ServerWorldAccess;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//跳过凋零骷髅生成亮度检查
@Mixin(HostileEntity.class)
public class WitherSkeletonEntitySpawnMixin {
    @Inject(method = "canSpawnInDark", at = @At("HEAD"), cancellable = true)
    private static void spawnLight(EntityType<? extends HostileEntity> type, ServerWorldAccess world,
                                   SpawnReason spawnReason, BlockPos pos, Random random,
                                   CallbackInfoReturnable<Boolean> cir) {
        if (CarpetOrgAdditionSettings.witherSkeletonCanSpawnToPortal && type == EntityType.WITHER_SKELETON) {
            boolean canSpawn = world.getDifficulty() != Difficulty.PEACEFUL
                    && HostileEntity.canMobSpawn(type, world, spawnReason, pos, random);
            cir.setReturnValue(canSpawn);
        }
    }
}
