package org.carpet_org_addition.mixin.rule;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.carpet_org_addition.command.RegisterCarpetCommands;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//用于Carpet命令的注册
@Mixin(CommandManager.class)
public abstract class RegisterCarpetCommandsMixin {
    @Shadow
    @Final
    private CommandDispatcher<ServerCommandSource> dispatcher;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onRegister(CommandManager.RegistrationEnvironment commandSelection,
                            CommandRegistryAccess commandBuildContext, CallbackInfo ci) {
        RegisterCarpetCommands.registerCarpetCommands(this.dispatcher, commandSelection, commandBuildContext);
    }
}
