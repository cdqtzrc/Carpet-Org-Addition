package org.carpet_org_addition.mixin.compat.fabricapi;

import carpet.patches.FakeClientConnection;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.fabricmc.fabric.impl.networking.AbstractChanneledNetworkAddon;
import net.fabricmc.fabric.impl.networking.AbstractNetworkAddon;
import net.fabricmc.fabric.impl.networking.GlobalReceiverRegistry;
import org.carpet_org_addition.util.ModIds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Restriction(
        require = @Condition(ModIds.FABRIC_NETWORKING_API)
)
@Mixin(value = AbstractNetworkAddon.class, priority = 998, remap = false)
public abstract class AbstractNetworkAddonMixin {
    @WrapWithCondition(
            method = "lateInit",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/fabricmc/fabric/impl/networking/GlobalReceiverRegistry;startSession(Lnet/fabricmc/fabric/impl/networking/AbstractNetworkAddon;)V"
            )
    )
    boolean notStartSession_ifFakeClientConnection(GlobalReceiverRegistry instance, AbstractNetworkAddon<?> addon) {
        if (addon instanceof AbstractChanneledNetworkAddon<?>) {
            return !(((AbstractChanneledNetworkAddonInvoker) addon).getConnection() instanceof FakeClientConnection);
        }
        return true;
    }
}
