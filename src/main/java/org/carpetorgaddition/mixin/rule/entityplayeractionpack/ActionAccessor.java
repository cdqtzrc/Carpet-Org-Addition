package org.carpetorgaddition.mixin.rule.entityplayeractionpack;

import carpet.helpers.EntityPlayerActionPack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityPlayerActionPack.Action.class)
public interface ActionAccessor {
    @Accessor(value = "isContinuous", remap = false)
    boolean isContinuous();
}
