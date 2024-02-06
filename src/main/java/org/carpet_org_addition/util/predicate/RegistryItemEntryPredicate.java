package org.carpet_org_addition.util.predicate;

import net.minecraft.command.argument.ItemStringReader;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.Optional;

public class RegistryItemEntryPredicate extends AbstractRegistryEntryPredicate {
    private final ItemStringReader.ItemResult itemResult;

    public RegistryItemEntryPredicate(ItemStringReader.ItemResult itemResult) {
        this.itemResult = itemResult;
    }

    @Override
    public boolean test(RegistryEntry<Item> itemRegistryEntry) {
        return itemRegistryEntry == itemResult.item();
    }

    @Override
    public String toString() {
        Optional<RegistryKey<Item>> key = this.itemResult.item().getKey();
        if (key.isPresent()) {
            return key.get().getValue().toString();
        }
        return Items.AIR.getName().getString();
    }
}
