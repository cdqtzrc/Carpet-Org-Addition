package org.carpetorgaddition.mixin.logger;

import carpet.logging.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(Logger.class)
public interface LoggerAccessor {
    @Accessor(value = "subscribedOnlinePlayers", remap = false)
    Map<String, String> getSubscribedOnlinePlayers();
}
