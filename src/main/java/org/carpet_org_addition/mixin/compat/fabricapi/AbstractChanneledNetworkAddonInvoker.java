package org.carpet_org_addition.mixin.compat.fabricapi;

import net.fabricmc.fabric.impl.networking.AbstractChanneledNetworkAddon;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = AbstractChanneledNetworkAddon.class, remap = false)
public interface AbstractChanneledNetworkAddonInvoker {
    @Accessor("connection")
    ClientConnection getConnection();
}
