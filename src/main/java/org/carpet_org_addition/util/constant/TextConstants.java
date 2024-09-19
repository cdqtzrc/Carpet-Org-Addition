package org.carpet_org_addition.util.constant;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
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
    /**
     * [这里]
     */
    public static final Text CLICK_HERE = TextUtils.getTranslate("carpet.command.text.click.here");

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
}
