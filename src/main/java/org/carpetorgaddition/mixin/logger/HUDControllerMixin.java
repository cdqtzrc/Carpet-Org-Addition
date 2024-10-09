package org.carpetorgaddition.mixin.logger;

import carpet.logging.HUDController;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.carpetorgaddition.logger.WanderingTraderSpawnLogger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(HUDController.class)
public class HUDControllerMixin {
    @Inject(method = "update_hud", at = @At(value = "INVOKE", target = "Ljava/util/Map;keySet()Ljava/util/Set;"), remap = false)
    private static void updateHud(MinecraftServer server, List<ServerPlayerEntity> force, CallbackInfo ci) {
        WanderingTraderSpawnLogger.updateHud(server);
    }
}
