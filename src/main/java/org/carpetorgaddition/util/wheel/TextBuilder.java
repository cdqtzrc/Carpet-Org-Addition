package org.carpetorgaddition.util.wheel;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.carpetorgaddition.util.TextUtils;

import java.util.ArrayList;

@SuppressWarnings("UnusedReturnValue")
public class TextBuilder {
    private final ArrayList<Text> list = new ArrayList<>();
    /**
     * 换行
     */
    private final Text NEW_LINE = TextUtils.createText("\n");

    public TextBuilder() {
    }

    /**
     * 追加文本
     */
    public TextBuilder append(Text text) {
        this.list.add(text);
        return this;
    }

    /**
     * 追加字符串
     */
    public TextBuilder appendString(String text) {
        return this.append(TextUtils.createText(text));
    }

    /**
     * 追加文本
     */
    public TextBuilder append(String key, Object... args) {
        return this.append(TextUtils.translate(key, args));
    }

    /**
     * 追加文本并换行
     */
    public TextBuilder appendLine(String key, Object... args) {
        this.append(TextUtils.translate(key, args));
        return this.append(NEW_LINE);
    }

    /**
     * 换行
     */
    public TextBuilder newLine() {
        return this.append(NEW_LINE);
    }

    /**
     * 追加缩进
     */
    public TextBuilder indentation() {
        return this.appendString("    ");
    }

    /**
     * 将当前对象转换为文本对象，每个元素之间不换行
     */
    public MutableText build() {
        MutableText text = list.getFirst().copy();
        for (int i = 1; i < this.list.size(); i++) {
            text.append(this.list.get(i));
        }
        return text;
    }

    @Override
    public String toString() {
        return this.build().getString();
    }
}
