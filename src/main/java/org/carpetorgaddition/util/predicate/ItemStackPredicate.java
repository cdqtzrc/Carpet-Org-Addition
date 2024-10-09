package org.carpetorgaddition.util.predicate;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

public class ItemStackPredicate extends AbstractItemStackPredicate {
    public ItemStackPredicate(AbstractRegistryEntryPredicate predicate, @Nullable NbtCompound nbt) {
        super(predicate, nbt);
    }

    @Override
    public boolean test(ItemStack itemStack) {
        return itemStack.itemMatches(predicate);
    }
}
