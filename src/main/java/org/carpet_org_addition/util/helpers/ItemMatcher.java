package org.carpet_org_addition.util.helpers;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.carpet_org_addition.CarpetOrgAddition;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.predicate.AbstractItemStackPredicate;

import java.util.function.Predicate;

/**
 * 活塞等物品的合成材料中，木板不是指某一种物品，而是指一类包含指定物品标签物品，所有木板都可以用来合成活塞，因此使用指定标签的合成配方会比指定物品的配方更加灵活，但是使用/playerTools 玩家名 action craft gui命令指定的配方不能使用标签，只能指定物品，本类用来在遍历玩家物品栏时自动选择让物品使用物品谓词的测试，还是物品直接比较，这样就不用把合成物品的方法写两遍
 */
public class ItemMatcher {
    public static final ItemMatcher AIR_ITEM_MATCHER = new ItemMatcher(Items.AIR);
    private final Predicate<ItemStack> predicate;
    private final Item item;

    /**
     * 使用谓词匹配物品
     */
    public ItemMatcher(Predicate<ItemStack> predicate) {
        this.predicate = predicate;
        this.item = null;
    }

    /**
     * 使用物品匹配物品
     */
    public ItemMatcher(Item item) {
        this.predicate = null;
        this.item = item;
    }

    /**
     * 使用空气物品匹配物品
     */
    public ItemMatcher() {
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
        if (this.predicate == null) {
            return this.item == Items.AIR;
        }
        return this.predicate.test(ItemStack.EMPTY) || this.predicate.test(Items.AIR.getDefaultStack());
    }

    /**
     * @return 当前物品匹配器是否存储的是物品
     */
    public boolean isItem() {
        return this.item != null;
    }

    /**
     * 获取当前物品匹配器存储的物品
     */
    public Item getItem() {
        return this.item;
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
        return "#";
    }

    public Text getName() {
        if (this.item != null) {
            return this.item.getName();
        }
        if (this.predicate instanceof AbstractItemStackPredicate itemStackPredicate) {
            return ItemMatcher.asItem(itemStackPredicate.toString()).getName();
        }
        return TextUtils.getTranslate("carpet.commands.playerAction.info.craft.item_tag");
    }

    /**
     * 获取物品的默认物品堆栈对象
     *
     * @return 如果是物品，然后物品默认的物品堆栈对象，否则返回null
     */
    public Object getDefaultStack() {
        if (this.item != null) {
            return this.item;
        }
        for (Item item : Registries.ITEM) {
            ItemStack defaultStack = item.getDefaultStack();
            if (this.test(defaultStack)) {
                return defaultStack;
            }
        }
        return null;
    }

    public static Item asItem(String id) {
        String[] split = id.split(":");
        if (split.length != 2) {
            CarpetOrgAddition.LOGGER.error("无法根据物品id:“" + id + "”获取物品");
            throw new IllegalArgumentException();
        }
        return Registries.ITEM.get(new Identifier(split[0], split[1]));
    }
}
