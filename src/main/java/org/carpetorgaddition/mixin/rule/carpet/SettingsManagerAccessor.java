package org.carpetorgaddition.mixin.rule.carpet;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.SettingsManager;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SettingsManager.class)
public interface SettingsManagerAccessor {
    @Invoker("displayInteractiveSetting")
    Text displayInteractiveSettings(CarpetRule<?> rule);
}
