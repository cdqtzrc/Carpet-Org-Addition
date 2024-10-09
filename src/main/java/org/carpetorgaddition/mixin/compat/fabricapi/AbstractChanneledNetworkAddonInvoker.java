package org.carpetorgaddition.mixin.compat.fabricapi;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.fabricmc.fabric.impl.networking.AbstractChanneledNetworkAddon;
import net.minecraft.network.ClientConnection;
import org.carpetorgaddition.util.constant.ModIds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@SuppressWarnings("UnstableApiUsage")
@Restriction(require = @Condition(ModIds.FABRIC_NETWORKING_API))
@Mixin(value = AbstractChanneledNetworkAddon.class, remap = false)
public interface AbstractChanneledNetworkAddonInvoker {
    @Accessor("connection")
    ClientConnection getConnection();
}
