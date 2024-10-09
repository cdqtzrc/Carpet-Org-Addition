package org.carpetorgaddition.util.predicate;

import net.minecraft.command.argument.ItemPredicateArgumentType;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * {@link net.minecraft.command.argument.ItemPredicateArgumentType#getItemStackPredicate(Predicate, NbtCompound)}方法的返回值
 * 是一个ItemStackPredicateArgument接口的实现类对象，它在lambda表达式中被定义，（貌似）无法使用Mixin注入代码，也就无法获取这个类中的成员变量等属性，
 * 因此，为了让/playerAction命令在获取假玩家合成物品动作的状态时正确显示物品的标签，本类重新实现了这个接口，并将原本类中用到这个lambda的重定向到了本类，
 * 然后重写了{@link AbstractItemStackPredicate#toString()}方法用来获取原lambda类中参数的字符串形式，方便其它类的调用
 *
 * @see org.carpetorgaddition.mixin.util.ItemPredicateArgumentTypeMixin
 */
@SuppressWarnings("JavadocReference")
public abstract class AbstractItemStackPredicate implements ItemPredicateArgumentType.ItemStackPredicateArgument {
    protected AbstractRegistryEntryPredicate predicate;
    protected @Nullable NbtCompound nbt;

    public AbstractItemStackPredicate(AbstractRegistryEntryPredicate predicate, @Nullable NbtCompound nbt) {
        this.predicate = predicate;
        this.nbt = nbt;
    }

    // 获取物品的名称，或者物品标签
    @Override
    public String toString() {
        return predicate.toString();
    }
}
