package org.carpet_org_addition.mixin.rule;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.BiomeKeys;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.Optional;

@Mixin(MagmaCubeEntity.class)
public class MagmaCubeEntityMixin extends SlimeEntity {

    public MagmaCubeEntityMixin(EntityType<? extends SlimeEntity> entityType, World world) {
        super(entityType, world);
    }

    // 禁止岩浆怪生成于下界荒地
    @Inject(method = "canMagmaCubeSpawn", at = @At("HEAD"), cancellable = true)
    private static void canSpawn(EntityType<MagmaCubeEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random, CallbackInfoReturnable<Boolean> cir) {
        if (CarpetOrgAdditionSettings.disableMagmaCubeSpawnNetherWastes) {
            boolean canSpawn = Objects.equals(world.getBiome(pos).getKey(), Optional.of(BiomeKeys.NETHER_WASTES));
            if (canSpawn) {
                cir.setReturnValue(false);
            }
        }
    }
}
