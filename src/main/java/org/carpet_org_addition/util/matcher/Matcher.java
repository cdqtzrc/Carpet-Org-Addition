package org.carpet_org_addition.util.matcher;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.carpet_org_addition.CarpetOrgAddition;

public interface Matcher {
    ItemMatcher AIR_ITEM_MATCHER = new ItemMatcher(Items.AIR);

    /**
     * 检查当前物品堆栈是否与匹配器匹配
     *
     * @param itemStack 要匹配的物品堆栈
     * @return 当前物品堆栈是否与匹配器匹配
     */
    boolean test(ItemStack itemStack);

    /**
     * 当前物品是否与空气物品匹配
     *
     * @return 匹配器的内容是否为空
     */
    boolean isEmpty();

    /**
     * 判断当前匹配器的内容物是否为物品
     *
     * @return 是物品返回true，是物品标签返回false
     */
    boolean isItem();

    /**
     * 获取匹配器内的物品
     *
     * @return 如果是物品直接返回，如果是物品标签返回空气物品
     */
    Item getItem();

    /**
     * 返回匹配器的名称
     *
     * @return 如果是物品，返回物品的名称，如果是物品标签，返回物品标签的字符串
     */
    Text getName();

    /**
     * 获取匹配器默认的物品堆栈
     *
     * @return 如果是物品，返回该物品的默认物品堆栈，如果是物品堆栈，直接返回，如果是物品标签，返回所有物品中第一个匹配的物品，如果没有匹配，返回空物品堆栈
     */
    ItemStack getDefaultStack();

    /**
     * 返回此匹配器的可变文本形式
     *
     * @return 如果是物品，返回默认堆栈的{@link ItemStack#toHoverableText()}，如果是物品标签，返回物品标签字符串的可变文本形式
     */
    MutableText toText();

    /**
     * 根据物品id获取对应物品
     *
     * @param id 物品的命名空间和id
     * @return 指定的物品
     */
    static Item asItem(String id) {
        String[] split = id.split(":");
        if (split.length != 2) {
            CarpetOrgAddition.LOGGER.error("无法根据物品id:“" + id + "”获取物品");
            throw new IllegalArgumentException();
        }
        return Registries.ITEM.get(new Identifier(split[0], split[1]));
    }
}
