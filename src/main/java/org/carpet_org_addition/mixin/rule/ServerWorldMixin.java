package org.carpet_org_addition.mixin.rule;

import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;

@SuppressWarnings("CommentedOutCode")
@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {
    // 将游戏时间与系统时间同步
/*    @Shadow
    @Final
    private ServerWorldProperties worldProperties;

    @Shadow
    @NotNull
    public abstract MinecraftServer getServer();

    @Inject(method = "setTimeOfDay", at = @At("HEAD"), cancellable = true)
    private void syncSystemTime(long timeOfDay, CallbackInfo ci) {
        if (CarpetOrgAdditionSettings.syncSystemTime) {
            LocalTime localTime = LocalTime.now();
            LocalDate now = LocalDate.now();
            double time = localTime.toEpochSecond(now, ZoneOffset.MIN)
                    - now.toEpochSecond(LocalTime.of(0, 0), ZoneOffset.UTC);
            long ofDay = (long) ((24000L * (time / 86400)) % 24000L);
            long l = this.worldProperties.getTimeOfDay() / 24000L % Integer.MAX_VALUE;
            this.worldProperties.setTimeOfDay(ofDay + 24000L * l);
            this.getServer().sendTimeUpdatePackets();
            ci.cancel();
        }
    }*/
}
