package org.carpet_org_addition.mixin.rule;

import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.biome.Biome;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

//禁止雪傀儡融化
@Mixin(SnowGolemEntity.class)
public class SnowGolemEntityMixin {
    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/registry/entry/RegistryEntry;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"))
    private boolean damage(RegistryEntry<Biome> instance, TagKey<Biome> tTagKey) {
        if (CarpetOrgAdditionSettings.disableSnowGolemMelts) {
            return false;
        }
        return instance.isIn(BiomeTags.SNOW_GOLEM_MELTS);
    }
}