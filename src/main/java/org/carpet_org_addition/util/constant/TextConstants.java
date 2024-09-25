package org.carpet_org_addition.util.constant;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.WorldUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class TextConstants {
    /**
     * 主世界
     */
    public static final Text OVERWORLD = TextUtils.translate("carpet.command.dimension.overworld");
    /**
     * 下界
     */
    public static final Text THE_NETHER = TextUtils.translate("carpet.command.dimension.the_nether");
    /**
     * 末地
     */
    public static final Text THE_END = TextUtils.translate("carpet.command.dimension.the_end");
    public static final Text TRUE = TextUtils.translate("carpet.command.boolean.true");
    public static final Text FALSE = TextUtils.translate("carpet.command.boolean.false");
    /**
     * [这里]
     */
    public static final Text CLICK_HERE = TextUtils.translate("carpet.command.text.click.here");
    /**
     * 物品
     */
    public static final Text ITEM = TextUtils.translate("carpet.command.item.item");

    public static Text getBoolean(boolean value) {
        return value ? TRUE : FALSE;
    }

    /**
     * 获取一个方块坐标的可变文本对象，并带有点击复制、悬停文本，颜色效果
     *
     * @param color 文本的颜色，如果为null，不修改颜色
     */
    public static MutableText blockPos(BlockPos blockPos, @Nullable Formatting color) {
        MutableText pos = simpleBlockPos(blockPos);
        //添加单击事件，复制方块坐标
        pos.styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, WorldUtils.toPosString(blockPos))));
        //添加光标悬停事件：单击复制到剪贴板
        pos.styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextUtils.translate("chat.copy.click"))));
        if (color != null) {
            //修改文本颜色
            pos.styled(style -> style.withColor(color));
        }
        if (CarpetOrgAdditionSettings.canHighlightBlockPos) {
            MutableText highlight = TextUtils.createText(" [H]");
            TextUtils.command(highlight, "/highlightWaypoint " + WorldUtils.toPosString(blockPos),
                    TextUtils.translate("ommc.highlight_waypoint.tooltip"), color, false);
            return TextUtils.appendAll(pos, highlight);
        }
        return pos;
    }

    /**
     * 返回一个简单的没有任何样式的方块坐标可变文本对象
     */
    public static MutableText simpleBlockPos(BlockPos blockPos) {
        return Texts.bracketed(Text.translatable("chat.coordinates", blockPos.getX(), blockPos.getY(), blockPos.getZ()));
    }

    /**
     * 单击输入"{@code 命令}"
     */
    public static MutableText clickInput(String command) {
        return TextUtils.translate("carpet.command.text.click.input", command);
    }

    /**
     * 单击执行{@code 命令}
     *
     * @param command 要执行的命令
     */
    public static MutableText clickRun(String command) {
        MutableText run = CLICK_HERE.copy();
        // 文本的悬停提示
        MutableText hoverText = TextUtils.translate("carpet.command.text.click.run", command);
        return TextUtils.command(run, command, hoverText, Formatting.AQUA, false);
    }

    /**
     * 返回物品有几组几个
     *
     * @return {@code 物品组数}组{@code 物品个数}个
     */
    public static MutableText itemCount(int count, int maxCount) {
        // 计算物品有多少组
        int group = count / maxCount;
        // 计算物品余几个
        int remainder = count % maxCount;
        MutableText text = TextUtils.createText(String.valueOf(count));
        // 为文本添加悬停提示
        if (group == 0) {
            return TextUtils.hoverText(text, TextUtils.translate("carpet.command.item.remainder", remainder));
        } else if (remainder == 0) {
            return TextUtils.hoverText(text, TextUtils.translate("carpet.command.item.group", group));
        } else {
            return TextUtils.hoverText(text, TextUtils.translate("carpet.command.item.count", group, remainder));
        }
    }

    /**
     * @param base 原始的文本对象
     * @return 获取物品栏中物品的名称和堆叠数量并用“*”连接，每个物品独占一行
     */
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
