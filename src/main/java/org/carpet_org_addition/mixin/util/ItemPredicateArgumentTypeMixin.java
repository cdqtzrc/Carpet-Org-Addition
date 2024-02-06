package org.carpet_org_addition.mixin.util;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Either;
import net.minecraft.command.argument.ItemPredicateArgumentType;
import net.minecraft.command.argument.ItemStringReader;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.carpet_org_addition.util.predicate.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * {@link net.minecraft.command.argument.ItemPredicateArgumentType.ItemStackPredicateArgument}的重新实现
 */
@Mixin(ItemPredicateArgumentType.class)
public class ItemPredicateArgumentTypeMixin {
    @Shadow
    @Final
    private RegistryWrapper<Item> registryWrapper;

    @Inject(method = "parse(Lcom/mojang/brigadier/StringReader;)Lnet/minecraft/command/argument/ItemPredicateArgumentType$ItemStackPredicateArgument;", at = @At("HEAD"), cancellable = true)
    private void parse(StringReader stringReader,
                       CallbackInfoReturnable<ItemPredicateArgumentType.ItemStackPredicateArgument> cir)
            throws CommandSyntaxException {
        Either<ItemStringReader.ItemResult, ItemStringReader.TagResult> either
                = ItemStringReader.itemOrTag(registryWrapper, stringReader);
        AbstractItemStackPredicate map = either.map(itemResult -> getItemStackPredicate(new RegistryItemEntryPredicate(itemResult), itemResult.nbt()),
                tagResult -> getItemStackPredicate(new RegistryTagEntryPredicate(tagResult), tagResult.nbt()));
        cir.setReturnValue(map);
    }

    private static AbstractItemStackPredicate getItemStackPredicate(AbstractRegistryEntryPredicate predicate, @Nullable NbtCompound nbt) {
        return nbt != null ? new WithNbtItemStackPredicate(predicate, nbt) : new ItemStackPredicate(predicate, null);
    }
}
