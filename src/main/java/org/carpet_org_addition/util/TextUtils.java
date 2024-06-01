package org.carpet_org_addition.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.translate.Translate;
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
    @SuppressWarnings("ExtractMethodRecommender")
    public static MutableText blockPos(BlockPos blockPos, @Nullable Formatting color) {
        MutableText pos = Texts.bracketed(Text.translatable("chat.coordinates", blockPos.getX(), blockPos.getY(), blockPos.getZ()));
        //添加单击事件，复制方块坐标
        pos.styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, WorldUtils.toPosString(blockPos))));
        //添加光标悬停事件：单击复制到剪贴板
        pos.styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextUtils.getTranslate("chat.copy.click"))));
        if (color != null) {
            //修改文本颜色
            pos.styled(style -> style.withColor(color));
        }
        if (CarpetOrgAdditionSettings.canHighlightBlockPos) {
            MutableText highlight = createText(" [H]");
            TextUtils.command(highlight, "/highlightWaypoint " + WorldUtils.toPosString(blockPos),
                    TextUtils.getTranslate("ommc.highlight_waypoint.tooltip"), color, false);
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
     * 获取一个可以单击执行命令的可变文本组件
     *
     * @param underline 是否带有下划线
     * @param text      原始的文本，直接显示在聊天栏中的文本
     * @param command   点击后要执行的命令
     * @param hoverText 悬停在原始文本上的内容
     * @param color     文本的颜色
     * @return 可以单击执行命令的可变文本组件
     */
    public static MutableText command(@NotNull MutableText text, @NotNull String command, @Nullable Text hoverText, @Nullable Formatting color, boolean underline) {
        // 添加下划线
        text.styled(style -> style.withUnderline(underline));
        // 点击后执行命令
        text.styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command)));
        if (hoverText != null) {
            text.styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText)));
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


    public static MutableText hoverText(MutableText initialText, Text hover, @Nullable Formatting color) {
        initialText.styled(style
                -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover)));
        if (color != null) {
            initialText.styled(style -> style.withColor(color));
        }
        return initialText;
    }

    /**
     * @param original      原始的字符串
     * @param color         字符串的颜色
     * @param bold          是否带有粗体
     * @param italic        是否带有斜体
     * @param underlined    是否带有下划线
     * @param strikethrough 是否带有删除线
     * @return 只带有一些普通样式的可变文本对象
     */
    public static MutableText regularStyle(String original, Formatting color, boolean bold, boolean italic, boolean underlined, boolean strikethrough) {
        MutableText text = Text.literal(original);
        text.styled(style -> style.withColor(color)
                .withBold(bold)
                .withItalic(italic)
                .withUnderline(underlined)
                .withStrikethrough(strikethrough));
        return text;
    }

    /**
     * @param original 原始的字符串
     * @param color    字符串的颜色
     * @return 只带有一些普通样式的可变文本对象
     */
    @SuppressWarnings("unused")
    public static MutableText regularStyle(String original, Formatting color) {
        MutableText text = Text.literal(original);
        text.styled(style -> style.withColor(color));
        return text;
    }

    /**
     * 根据字符串创建一个新的可变文本对象
     *
     * @param text 可变文本对象的内容
     * @return 一个新的不包含任何样式的可变文本对象
     */
    public static MutableText createText(String text) {
        return Text.literal(text);
    }

    /**
     * 创建一个不包含任何内容的可变文本对象
     */
    public static MutableText createEmpty() {
        return Text.literal("");
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
     * 将一个可变文本对象设置为斜体
     */
    public static MutableText toItalic(MutableText mutableText) {
        return mutableText.styled(style -> style.withItalic(true));
    }

    /**
     * 设置一个可变文本对象的颜色
     */
    public static MutableText setColor(MutableText mutableText, Formatting formatting) {
        return mutableText.styled(style -> style.withColor(formatting));
    }

    /**
     * 获取一个物品名称的可变文本形式
     *
     * @param item 要获取名称的物品
     */
    @SuppressWarnings("unused")
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
        MutableText mutableText = TextUtils.createEmpty();
        for (Object object : objects) {
            if (object instanceof String str) {
                mutableText.append(Text.literal(str));
            } else if (object instanceof Text text) {
                mutableText.append(text);
            } else {
                throw new IllegalArgumentException(object + "即不是可变文本对象，也不是字符串对象");
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
