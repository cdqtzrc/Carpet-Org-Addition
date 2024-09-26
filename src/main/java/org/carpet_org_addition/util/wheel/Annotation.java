package org.carpet_org_addition.util.wheel;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.text.Text;
import org.carpet_org_addition.util.TextUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * 用来给一些功能添加注释
 */
public class Annotation {
    @NotNull
    private String annotation;

    public Annotation() {
        this.annotation = "";
    }

    public Annotation(String annotation) {
        this.annotation = Objects.requireNonNullElse(annotation, "");
    }

    public Annotation(JsonObject json) {
        JsonElement element = json.get("annotation");
        this.annotation = element == null ? "" : element.getAsString();
    }

    /**
     * @return 此注释是否有内容
     */
    public boolean hasContent() {
        return !this.annotation.isBlank();
    }

    public @NotNull String getAnnotation() {
        return this.annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation == null ? "" : annotation;
    }

    public Text getText() {
        return TextUtils.createText(this.annotation);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("annotation", this.annotation);
        return json;
    }

    @Override
    public String toString() {
        return this.annotation;
    }
}
