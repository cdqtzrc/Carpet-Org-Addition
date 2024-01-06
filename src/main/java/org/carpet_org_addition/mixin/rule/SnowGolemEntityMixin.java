package org.carpet_org_addition.mixin.rule;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.biome.Biome;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//禁止雪傀儡融化
@Mixin(SnowGolemEntity.class)
public class SnowGolemEntityMixin {
    @WrapOperation(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/registry/entry/RegistryEntry;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"))
    private boolean damage(RegistryEntry<Biome> instance, TagKey<Biome> tTagKey, Operation<Boolean> original) {
        if (CarpetOrgAdditionSettings.disableSnowGolemMelts) {
            return false;
        }
        return original.call(instance, tTagKey);
    }
}