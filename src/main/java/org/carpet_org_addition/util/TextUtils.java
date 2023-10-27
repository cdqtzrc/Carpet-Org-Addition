package org.carpet_org_addition.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.carpet.tools.text.Translate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class TextUtils {
    private TextUtils() {
    }

    /**
     * 获取一个方块坐标的可变文本对象，并带有点击复制、悬停文本，颜色效果
     *
     * @param color 文本的颜色，如果为null，不修改颜色
     */
    public static MutableText blockPos(BlockPos blockPos, @Nullable Formatting color) {
        MutableText pos = Texts.bracketed(Text.translatable("chat.coordinates", blockPos.getX(), blockPos.getY(), blockPos.getZ()));
        if (CarpetOrgAdditionSettings.canParseWayPoint) {
            //如果启用了“可解析路径点”，直接返回不带特殊样式的路径点
            return pos;
        }
        //添加单击事件，复制方块坐标
        pos.styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, StringUtils.getBlockPosString(blockPos))));
        //添加光标悬停事件：单击复制到剪贴板
        pos.styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextUtils.getTranslate("chat.copy.click"))));
        if (color != null) {
            //修改文本颜色
            pos.styled(style -> style.withColor(color));
        }
        return pos;
    }

    /**
     * 获取一个可以单击并在聊天框输入文本的可变文本组件
     *
     * @param original  原始文本，直接显示在聊天页面上
     * @param input     点击后输入在聊天框里的文本
     * @param hoverText 光标放在原始文本上显示的内容，如果为null，不显示悬停文本
     * @param color     文本的颜色，如果为null，默认为白色
     */
    public static MutableText suggest(@NotNull String original, @Nullable String input, @Nullable Text hoverText, @Nullable Formatting color) {
        MutableText text = Text.literal(original);
        if (input != null) {
            //添加单击事件
            text.styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, input)));
        }
        if (hoverText != null) {
            //添加鼠标悬停事件
            text.styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText)));
        }
        if (color != null) {
            text.styled(style -> style.withColor(color));
        }
        return text;
    }

    /**
     * 获取一个可以单击复制指定字符串内容到剪贴板的可变文本组件
     *
     * @param original  原始的文本，直接显示在聊天栏中的文本
     * @param copy      单击后要复制的内容
     * @param hoverText 悬停在原始文本上的内容
     * @param color     文本的颜色
     * @return 可以单击复制内容的可变文本组件
     */
    public static MutableText copy(@NotNull String original, @Nullable String copy, @Nullable Text hoverText, @Nullable Formatting color) {
        MutableText text = Text.literal(original);
        if (copy != null) {
            text.styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, copy)));
        }
        if (hoverText != null) {
            text.styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText)));
        }
        if (color != null) {
            text.styled(style -> style.withColor(color));
        }
        return text;
    }

    /**
     * 获取一个可以单击打开网页链接的可变文本组件，带有下划线
     *
     * @param original  原始的文本，直接显示在聊天栏中的文本
     * @param url       单击后要打开的网页链接
     * @param hoverText 悬停在原始文本上的内容
     * @param color     文本的颜色
     * @return 可以单击打开网页链接的可变文本组件
     */
    public static MutableText url(@NotNull String original, @Nullable String url, @Nullable String hoverText, @Nullable Formatting color) {
        MutableText text = Text.literal(original);
        //添加下划线
        text.styled(style -> style.withUnderline(true));
        if (url != null) {
            text.styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url)));
        }
        if (hoverText != null) {
            text.styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(hoverText))));
        }
        if (color != null) {
            text.styled(style -> style.withColor(color));
        }
        return text;
    }

    /**
     * 获取一个带有悬浮文本的可变文本对象
     *
     * @param text  要显示的文本
     * @param hover 显示在文本上的悬浮文字
     */
    public static MutableText hoverText(String text, String hover) {
        return Text.literal(text).styled(style
                -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(hover))));
    }

    /**
     * 获取一个方块名称的可变文本形式
     *
     * @param block 要获取名称的方块
     */
    public static MutableText getBlockName(Block block) {
        return Text.translatable(block.getTranslationKey());
    }

    /**
     * 获取一个物品名称的可变文本形式
     *
     * @param item 要获取名称的物品
     */
    public static MutableText getItemName(Item item) {
        return Text.translatable(item.getTranslationKey());
    }

    /**
     * 将一堆零散的字符串和可变文本拼接成一个大的可变文本
     *
     * @param objects 要拼接的文本，可以是字符串，也可以是文本，但不能是其他类型，否则抛出非法参数异常
     * @return 拼接后的可变文本对象
     */
    public static MutableText appendAll(Object... objects) {
        MutableText mutableText = Text.literal("");
        for (Object object : objects) {
            if (object instanceof String str) {
                mutableText.append(Text.literal(str));
            } else if (object instanceof Text text) {
                mutableText.append(text);
            } else {
                throw new IllegalArgumentException();
            }
        }
        return mutableText;
    }

    /**
     * 获取一个可翻译文本对象
     *
     * @param key 翻译键
     * @return 可翻译文本
     */
    public static MutableText getTranslate(String key, Object... obj) {
        String value;
        try {
            value = Objects.requireNonNull(Translate.getTranslate()).get(key);
        } catch (NullPointerException e) {
            value = null;
        }
        return Text.translatableWithFallback(key, value, obj);
    }
}
