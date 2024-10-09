package org.carpetorgaddition.util.predicate;

import net.minecraft.command.argument.ItemPredicateArgumentType;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

/**
 * 这里的代码并没有什么用
 */
public abstract class AbstractItemStackPredicate implements ItemPredicateArgumentType.ItemStackPredicateArgument {
    protected AbstractMatchPredicate predicate;
    protected @Nullable NbtCompound nbt;

    public AbstractItemStackPredicate(AbstractMatchPredicate predicate, @Nullable NbtCompound nbt) {
        this.predicate = predicate;
        this.nbt = nbt;
    }

    // 获取物品的名称，或者物品标签
    @Override
    public String toString() {
        return predicate.toString();
    }
}
