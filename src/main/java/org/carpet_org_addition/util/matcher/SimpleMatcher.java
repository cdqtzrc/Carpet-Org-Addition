package org.carpet_org_addition.util.matcher;

import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;

public interface SimpleMatcher {
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
     * 返回此匹配器的可变文本形式
     */
    MutableText toText();
}
