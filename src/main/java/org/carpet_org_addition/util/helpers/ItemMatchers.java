package org.carpet_org_addition.util.helpers;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.carpet_org_addition.CarpetOrgAddition;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.predicate.AbstractItemStackPredicate;

import java.util.function.Predicate;

/**
 * @see org.carpet_org_addition.util.matcher.Matcher
 * @deprecated 被新的类取代
 */
@SuppressWarnings("unused")
@Deprecated(forRemoval = true)
public class ItemMatchers {
    public static final ItemMatchers AIR_ITEM_MATCHER = new ItemMatchers(Items.AIR);
    private final Predicate<ItemStack> predicate;
    private final Item item;

    /**
     * 使用谓词匹配物品
     */
    public ItemMatchers(Predicate<ItemStack> predicate) {
        this.predicate = predicate;
        this.item = null;
    }

    /**
     * 使用物品匹配物品
     */
    public ItemMatchers(Item item) {
        this.predicate = null;
        this.item = item;
    }

    public ItemMatchers(ItemStack itemStack) {
        this(itemStack.getItem());
    }

    /**
     * 使用空气物品匹配物品
     */
    public ItemMatchers() {
        this.predicate = null;
        this.item = Items.AIR;
    }

    /**
     * 判断物品是否与物品匹配器匹配，在合成物品时，匹配的物品可以作为物品的合成材料
     *
     * @param itemStack 要测试的物品堆栈对象
     * @return 物品是否与物品匹配器匹配
     */
    public boolean test(ItemStack itemStack) {
        if (this.predicate == null) {
            return itemStack.isOf(this.item);
        }
        return this.predicate.test(itemStack);
    }

    /**
     * 判断物品是否与空气匹配，合成物品时，与空气匹配的合成材料会直接跳过
     *
     * @return 物品是否与空气匹配
     */
    public boolean isEmpty() {
        return this.test(Items.AIR.getDefaultStack());
    }

    /**
     * @return 当前物品匹配器是否存储的是物品
     */
    public boolean isItem() {
        if (this.predicate instanceof AbstractItemStackPredicate itemStackPredicate) {
            return !itemStackPredicate.toString().startsWith("#");
        }
        return this.item != null;
    }

    /**
     * 获取当前物品匹配器存储的物品
     */
    public Item getItem() {
        if (this.predicate instanceof AbstractItemStackPredicate itemStackPredicate) {
            String itemOrTag = itemStackPredicate.toString();
            if (itemOrTag.startsWith("#")) {
                return ItemStack.EMPTY.getItem();
            }
            return asItem(itemOrTag);
        }
        return this.item == null ? ItemStack.EMPTY.getItem() : this.item;
    }

    /**
     * 获取物品匹配器的字符串形式，如果是物品，返回物品的ID，如果是物品谓词，返回物品标签的字符串形式，否则返回“#”
     *
     * @return 物品名称或“#”
     */
    @Override
    public String toString() {
        if (this.item != null) {
            return this.item.toString();
        }
        if (this.predicate instanceof AbstractItemStackPredicate itemStackPredicate) {
            String string = itemStackPredicate.toString();
            if (string.startsWith("#")) {
                return string;
            }
            String[] split = string.split(":");
            return split.length == 2 ? split[1] : split[0];
        }
        return Items.AIR.toString();
    }

    public Text getName() {
        if (this.isItem()) {
            return this.getItem().getName();
        }
        if (this.predicate instanceof AbstractItemStackPredicate itemStackPredicate) {
            String nameOrTag = itemStackPredicate.toString();
            if (nameOrTag.startsWith("#")) {
                return TextUtils.createText(nameOrTag);
            }
            return ItemMatchers.asItem(nameOrTag).getName();
        }
        return TextUtils.getTranslate("carpet.commands.playerAction.info.craft.item_tag");
    }

    /**
     * 获取物品的默认物品堆栈对象
     *
     * @return 如果是物品，然后物品默认的物品堆栈对象，否则返回null
     */
    public ItemStack getDefaultStack() {
        if (this.isItem()) {
            return this.getItem().getDefaultStack();
        }
        for (Item item : Registries.ITEM) {
            ItemStack defaultStack = item.getDefaultStack();
            if (this.test(defaultStack)) {
                return defaultStack;
            }
        }
        return ItemStack.EMPTY;
    }

    public static Item asItem(String id) {
        String[] split = id.split(":");
        if (split.length != 2) {
            CarpetOrgAddition.LOGGER.error("无法根据物品id:“{}”获取物品", id);
            throw new IllegalArgumentException();
        }
        return Registries.ITEM.get(Identifier.of(split[0], split[1]));
    }

    public MutableText toText() {
        if (this.item == null) {
            if (this.predicate instanceof AbstractItemStackPredicate itemStackPredicate) {
                String itemOrTag = itemStackPredicate.toString();
                if (itemOrTag.startsWith("#")) {
                    return TextUtils.createText(itemOrTag);
                } else {
                    return ItemMatchers.asItem(itemOrTag).getDefaultStack().toHoverableText().copy();
                }
            }
            return TextUtils.getTranslate("carpet.commands.playerAction.info.craft.item_tag");
        }
        return this.item.getDefaultStack().toHoverableText().copy();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ItemMatchers itemMatchers) {
            if (this.isItem() && this.isItem() && this.item == itemMatchers.getItem()) {
                return true;
            }
            return this.predicate != null && this.predicate == itemMatchers.predicate;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
}
