package org.carpet_org_addition.util.predicate;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import org.jetbrains.annotations.Nullable;

public class WithNbtItemStackPredicate extends AbstractItemStackPredicate {
    public WithNbtItemStackPredicate(AbstractRegistryEntryPredicate predicate, @Nullable NbtCompound nbt) {
        super(predicate, nbt);
    }

    @Override
    public boolean test(ItemStack itemStack) {
        return itemStack.itemMatches(predicate) && NbtHelper.matches(nbt, itemStack.getNbt(), true);
    }
}
