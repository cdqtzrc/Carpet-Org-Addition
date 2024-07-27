package org.carpet_org_addition.mixin.rule;

import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.carpet_org_addition.util.wheel.ContainerDeepCopy;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(ContainerComponent.class)
public class ContainerComponentMixin implements ContainerDeepCopy {

    @Shadow
    @Final
    private DefaultedList<ItemStack> stacks;

    @Override
    public ContainerComponent copy() {
        DefaultedList<ItemStack> list = this.stacks;
        DefaultedList<ItemStack> copy = DefaultedList.ofSize(list.size(), ItemStack.EMPTY);
        for (int index = 0; index < copy.size(); index++) {
            copy.set(index, list.get(index).copy());
        }
        return ContainerComponentAccessor.constructor(copy);
    }
}
