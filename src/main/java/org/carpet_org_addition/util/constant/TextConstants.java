package org.carpet_org_addition.util.constant;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.carpet_org_addition.util.TextUtils;

import java.util.ArrayList;

public class TextConstants {
    /**
     * 主世界
     */
    public static final Text OVERWORLD = TextUtils.getTranslate("carpet.command.dimension.overworld");
    /**
     * 下界
     */
    public static final Text THE_NETHER = TextUtils.getTranslate("carpet.command.dimension.the_nether");
    /**
     * 末地
     */
    public static final Text THE_END = TextUtils.getTranslate("carpet.command.dimension.the_end");
    public static final Text TRUE = TextUtils.getTranslate("carpet.command.boolean.true");
    public static final Text FALSE = TextUtils.getTranslate("carpet.command.boolean.false");
    /**
     * [这里]
     */
    public static final Text CLICK_HERE = TextUtils.getTranslate("carpet.command.text.click.here");
    /**
     * 物品
     */
    public static final Text ITEM = TextUtils.getTranslate("carpet.command.item.item");

    public static Text getBoolean(boolean value) {
        return value ? TRUE : FALSE;
    }

    /**
     * 单击输入"%s"
     */
    public static MutableText clickInput(Object... args) {
        return TextUtils.getTranslate("carpet.command.text.click.input", args);
    }

    /**
     * 单击执行%s
     *
     * @param command 要执行的命令
     */
    public static MutableText clickRun(String command) {
        MutableText run = CLICK_HERE.copy();
        // 文本的悬停提示
        MutableText hoverText = TextUtils.getTranslate("carpet.command.text.click.run", command);
        return TextUtils.command(run, command, hoverText, Formatting.AQUA, false);
    }

    /**
     * 返回物品有几组几个
     *
     * @return %s组%s个
     */
    public static MutableText itemCount(int count, int maxCount) {
        // 计算物品有多少组
        int group = count / maxCount;
        // 计算物品余几个
        int remainder = count % maxCount;
        MutableText text = TextUtils.createText(String.valueOf(count));
        // 为文本添加悬停提示
        if (group == 0) {
            return TextUtils.hoverText(text, TextUtils.getTranslate("carpet.command.item.remainder", remainder), null);
        } else if (remainder == 0) {
            return TextUtils.hoverText(text, TextUtils.getTranslate("carpet.command.item.group", group), null);
        } else {
            return TextUtils.hoverText(text, TextUtils.getTranslate("carpet.command.item.count", group, remainder), null);
        }
    }

    public static MutableText inventory(Text base, Inventory inventory) {
        ArrayList<Text> list = new ArrayList<>();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack itemStack = inventory.getStack(i);
            if (itemStack.isEmpty()) {
                continue;
            }
            list.add(TextUtils.appendAll(itemStack.getName(), "*", String.valueOf(itemStack.getCount())));
        }
        return TextUtils.hoverText(base, TextUtils.appendList(list));
    }
}
