package org.carpet_org_addition.mixin.rule;

import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.carpet_org_addition.util.helpers.ContainerDeepCopy;
import org.spongepowered.asm.mixin.Mixin;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(ContainerComponent.class)
public class ContainerComponentMixin implements ContainerDeepCopy {

    @Override
    public ContainerComponent copy(ContainerComponent component) {
        DefaultedList<ItemStack> list = ((ContainerComponentAccessor) (Object) component).getStacks();
        DefaultedList<ItemStack> copy = DefaultedList.ofSize(list.size(), ItemStack.EMPTY);
        for (int index = 0; index < copy.size(); index++) {
            copy.set(index, list.get(index).copy());
        }
        return ContainerComponentAccessor.constructor(copy);
    }
}
