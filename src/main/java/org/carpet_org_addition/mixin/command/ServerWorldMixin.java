package org.carpet_org_addition.mixin.command;

import net.minecraft.server.world.ServerWorld;
import org.carpet_org_addition.util.task.ServerTask;
import org.carpet_org_addition.util.task.ServerWorldInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.function.BooleanSupplier;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin implements ServerWorldInterface {
    @Shadow
    public abstract void tick(BooleanSupplier shouldKeepTicking);

    @Unique
    private final ArrayList<ServerTask> tasks = new ArrayList<>();

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        this.tasks.removeIf(ServerTask::isEndOfExecution);
        this.tasks.forEach(ServerTask::tick);
    }

    @Override
    public void addTask(ServerTask task) {
        this.tasks.add(task);
    }
}
