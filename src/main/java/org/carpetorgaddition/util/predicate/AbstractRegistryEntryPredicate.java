package org.carpetorgaddition.util.predicate;

import net.minecraft.item.Item;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.function.Predicate;

public abstract class AbstractRegistryEntryPredicate implements Predicate<RegistryEntry<Item>> {
}
