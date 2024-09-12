package org.carpet_org_addition.util.constant;

import net.minecraft.text.MutableText;
import org.carpet_org_addition.util.TextUtils;

public class TextConstants {
    /**
     * 主世界
     */
    public static final MutableText OVERWORLD = TextUtils.getTranslate("carpet.command.dimension.overworld");
    /**
     * 下界
     */
    public static final MutableText THE_NETHER = TextUtils.getTranslate("carpet.command.dimension.the_nether");
    /**
     * 末地
     */
    public static final MutableText THE_END = TextUtils.getTranslate("carpet.command.dimension.the_end");
    public static final MutableText TRUE = TextUtils.getTranslate("carpet.command.boolean.true");
    public static final MutableText FALSE = TextUtils.getTranslate("carpet.command.boolean.false");
    /**
     * [这里]
     */
    public static final MutableText CLICK_HERE = TextUtils.getTranslate("carpet.command.text.click.here");

    public static MutableText getBoolean(boolean value) {
        return value ? TRUE : FALSE;
    }

    /**
     * 单击输入"%s"
     */
    public static MutableText clickInput(Object... args) {
        return TextUtils.getTranslate("carpet.command.text.click.input", args);
    }
}
