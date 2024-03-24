package org.carpet_org_addition.util.matcher;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import org.carpet_org_addition.util.TextUtils;

import java.util.List;
import java.util.Objects;

public class ItemTagMatcher implements Matcher {
    private final String tag;

    public ItemTagMatcher(String tag) {
        boolean hasSymbol = tag.startsWith("#");
        if (hasSymbol && tag.contains(":")) {
            this.tag = tag;
            return;
        }
        this.tag = "#minecraft:" + (hasSymbol ? tag.substring(1) : tag);
    }

    @Override
    public boolean test(ItemStack itemStack) {
        List<String> list = itemStack.streamTags().map(tag -> tag.id().toString()).toList();
        for (String tag : list) {
            if (Objects.equals(this.tag, tag)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.test(Items.AIR.getDefaultStack());
    }

    @Override
    public boolean isItem() {
        return false;
    }

    @Override
    public Item getItem() {
        return Items.AIR;
    }

    @Override
    public Text getName() {
        return TextUtils.createText(this.tag);
    }

    @Override
    public ItemStack getDefaultStack() {
        for (Item item : Registries.ITEM) {
            ItemStack defaultStack = item.getDefaultStack();
            if (this.test(defaultStack)) {
                return defaultStack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (this.getClass() == obj.getClass()) {
            return this.tag.equals(((ItemTagMatcher) obj).tag);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.tag.hashCode();
    }

    @Override
    public String toString() {
        return this.tag;
    }
}
