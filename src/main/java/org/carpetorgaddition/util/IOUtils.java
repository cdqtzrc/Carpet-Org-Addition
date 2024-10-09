package org.carpetorgaddition.util;

import com.google.gson.*;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class IOUtils {
    public static final String JSON_EXTENSION = ".json";
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private IOUtils() {
    }

    /**
     * 创建一个UTF-8编码的字符输入流对象
     */
    public static BufferedReader toReader(File file) throws IOException {
        return new BufferedReader(new FileReader(file, StandardCharsets.UTF_8));
    }

    /**
     * 创建一个UTF-8编码的字符输出流对象
     */
    public static BufferedWriter toWriter(File file) throws IOException {
        return new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8));
    }

    /**
     * 将一个json对象保存到文件
     */
    public static void saveJson(File file, JsonObject json) throws IOException {
        String jsonString = GSON.toJson(json, JsonObject.class);
        try (BufferedWriter writer = toWriter(file)) {
            writer.write(jsonString);
        }
    }

    /**
     * 从文件加载一个json对象
     */
    public static JsonObject loadJson(File file) throws IOException {
        BufferedReader reader = toReader(file);
        try (reader) {
            return GSON.fromJson(reader, JsonObject.class);
        }
    }

    /**
     * json对象中是否包含指定元素
     *
     * @param elements 一个字符串数组，数组中只要有一个元素不存在于json中方法就返回false
     */
    public static boolean jsonHasElement(JsonObject json, String... elements) {
        for (String element : elements) {
            if (json.has(element)) {
                continue;
            }
            return false;
        }
        return true;
    }

    /**
     * 根据键获取json中对应的值，如果不存在，返回默认值
     *
     * @param defaultValue 如果为获取到值，返回默认值
     * @param type         返回值的类型
     */
    @NotNull
    public static <T> T getJsonElement(JsonObject json, String key, T defaultValue, Class<T> type) {
        JsonElement element = json.get(key);
        if (element == null) {
            return defaultValue;
        }
        // 布尔值
        if (type == boolean.class || type == Boolean.class) {
            return type.cast(element.getAsBoolean());
        }
        // 数值
        if (Number.class.isAssignableFrom(type)) {
            return type.cast(element.getAsNumber());
        }
        // 字符串
        if (type == String.class) {
            return type.cast(element.getAsString());
        }
        // jsonObject
        if (JsonObject.class.isAssignableFrom(type)) {
            return type.cast(element.getAsJsonObject());
        }
        // JsonArray
        if (JsonArray.class.isAssignableFrom(type)) {
            return type.cast(element.getAsJsonArray());
        }
        throw new IllegalArgumentException();
    }

    /**
     * 删除文件扩展名
     */
    public static String removeExtension(String fileName) {
        if (fileName.endsWith(JSON_EXTENSION)) {
            return fileName.substring(0, fileName.lastIndexOf("."));
        }
        return fileName;
    }
}
