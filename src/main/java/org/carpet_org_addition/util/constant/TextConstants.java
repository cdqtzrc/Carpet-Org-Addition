package org.carpet_org_addition.util.constant;

import net.minecraft.text.Text;
import org.carpet_org_addition.util.TextUtils;

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

    public static Text getBoolean(boolean value) {
        return value ? TRUE : FALSE;
    }
}
