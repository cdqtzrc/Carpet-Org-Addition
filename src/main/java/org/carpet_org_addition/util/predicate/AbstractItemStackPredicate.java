package org.carpet_org_addition.util.predicate;

import net.minecraft.command.argument.ItemPredicateArgumentType;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractItemStackPredicate implements ItemPredicateArgumentType.ItemStackPredicateArgument {
    protected AbstractRegistryEntryPredicate predicate;
    protected @Nullable NbtCompound nbt;

    public AbstractItemStackPredicate(AbstractRegistryEntryPredicate predicate, @Nullable NbtCompound nbt) {
        this.predicate = predicate;
        this.nbt = nbt;
    }

    @Override
    public String toString() {
        return predicate.toString();
    }
}
