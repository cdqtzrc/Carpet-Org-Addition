package org.carpet_org_addition.util.predicate;

import net.minecraft.command.argument.ItemStringReader;
import net.minecraft.item.Item;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;

import java.util.Optional;

public class RegistryTagEntryPredicate extends AbstractRegistryEntryPredicate {
    private final ItemStringReader.TagResult tagResult;

    public RegistryTagEntryPredicate(ItemStringReader.TagResult tagResult) {
        this.tagResult = tagResult;
    }

    @Override
    public boolean test(RegistryEntry<Item> itemRegistryEntry) {
        return this.tagResult.tag().contains(itemRegistryEntry);
    }

    @Override
    public String toString() {
        Optional<TagKey<Item>> tagKey = this.tagResult.tag().getTagKey();
        return tagKey.map(itemTagKey -> "#" + itemTagKey.id().toString()).orElse("#");
    }
}
