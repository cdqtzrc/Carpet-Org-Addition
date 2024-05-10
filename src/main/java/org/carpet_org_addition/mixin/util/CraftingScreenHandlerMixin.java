package org.carpet_org_addition.mixin.util;

import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.screen.CraftingScreenHandler;
import org.carpet_org_addition.util.fakeplayer.FakePlayerCraftRecipeInterface;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(CraftingScreenHandler.class)
public abstract class CraftingScreenHandlerMixin implements FakePlayerCraftRecipeInterface {
    @Shadow
    @Final
    private RecipeInputInventory input;

    @Override
    public RecipeInputInventory getInput() {
        return this.input;
    }
}
