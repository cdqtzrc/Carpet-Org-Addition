package org.carpet_org_addition.mixin.rule;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.BiomeKeys;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.Optional;

//禁止岩浆怪生成于下界荒地
@Mixin(MagmaCubeEntity.class)
public class MagmaCubeEntityMixin extends SlimeEntity {

    public MagmaCubeEntityMixin(EntityType<? extends SlimeEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "canSpawn", at = @At("HEAD"), cancellable = true)
    private void canSpawn(WorldView world, CallbackInfoReturnable<Boolean> cir) {
        if (CarpetOrgAdditionSettings.disableMagmaCubeSpawnNetherWastes) {
            // boolean b = world.getBiome(BlockPos.ofFloored(this.getPos())) == BiomeKeys.NETHER_WASTES;
            boolean b = Objects.equals(world.getBiome(BlockPos.ofFloored(this.getPos())).getKey(), Optional.of(BiomeKeys.NETHER_WASTES));
            if (b) {
                cir.setReturnValue(false);
            }
        }
    }
}
