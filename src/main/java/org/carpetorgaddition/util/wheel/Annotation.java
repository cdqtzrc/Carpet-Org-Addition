package org.carpetorgaddition.util.wheel;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.text.Text;
import org.carpetorgaddition.util.TextUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 用来给一些功能添加注释
 */
public class Annotation {
    @NotNull
    private String annotation = "";

    public Annotation() {
    }

    /**
     * @return 此注释是否有内容
     */
    public boolean hasContent() {
        return !this.annotation.isBlank();
    }

    public boolean isEmpty() {
        return this.annotation.isBlank();
    }

    public @NotNull String getAnnotation() {
        if (this.isEmpty()) {
            return "";
        }
        return this.annotation;
    }

    public void setAnnotation(@Nullable String annotation) {
        this.annotation = annotation == null ? "" : annotation;
    }

    public void setAnnotation(JsonObject json) {
        JsonElement element = json.get("annotation");
        this.annotation = element == null ? "" : element.getAsString();
    }

    public Text getText() {
        return TextUtils.createText(this.annotation);
    }

    @Override
    public String toString() {
        return this.annotation;
    }
}
