package org.carpet_org_addition.util.wheel;

import com.google.gson.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import org.carpet_org_addition.CarpetOrgAddition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @see <a href="https://zh.minecraft.wiki/w/Java%E7%89%88%E4%B8%96%E7%95%8C%E6%A0%BC%E5%BC%8F">世界格式</a>
 */
public class WorldFormat {
    public static final String JSON_EXTENSION = ".json";
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    /**
     * 文件是否为{@code json}扩展名
     */
    public static final Predicate<File> JSON_EXTENSIONS = file -> file.getName().endsWith(JSON_EXTENSION);

    private final File modFileDirectory;

    /**
     * 尝试创建一个存档目录下的文件夹
     *
     * @param server      游戏当前运行的服务器，用来获取操作系统下服务器存档的路径
     * @param directory   一个字符串，表示第二级子目录，有这个参数的原因是至少要传入一个字符串参数
     * @param directories 一个字符串数组，数组中第一个元素表示第三级子目录，第二个元素表示第四级子目录，以此类推
     * @apiNote 第一级目录是carpetorgaddition文件夹
     */
    public WorldFormat(MinecraftServer server, @Nullable String directory, String... directories) {
        // 获取服务器存档保存文件的路径
        Path path = server.getSavePath(WorldSavePath.ROOT).resolve(CarpetOrgAddition.MOD_NAME_LOWER_CASE);
        if (directory != null) {
            path = path.resolve(directory);
        }
        // 拼接路径
        for (String name : directories) {
            path = path.resolve(name);
        }
        // 将路径转为文件对象
        this.modFileDirectory = path.toFile();
        // 文件夹必须存在或者成功创建
        if (this.modFileDirectory.isDirectory() || this.modFileDirectory.mkdirs()) {
            return;
        }
        // 如果这个文件夹不存在并且没有创建成功，将信息写入日志
        CarpetOrgAddition.LOGGER.error("{}文件夹创建失败", this.modFileDirectory);
    }

    /**
     * 创建一个当前目录下的文件对象，只创建文件对象，不创建文件
     *
     * @param fileName 文件名，如果没有扩展名，则自动添加json作为扩展名
     */
    public File file(String fileName) {
        return new File(this.modFileDirectory, suppFileName(fileName));
    }

    /**
     * 获取一个当前目录下的指定名称的文件对象
     *
     * @param fileName 文件的名称，如果没有扩展名，则自动添加一个.json作为扩展名
     */
    public File getFile(String fileName) {
        fileName = suppFileName(fileName);
        return new File(this.modFileDirectory, fileName);
    }

    // 补全文件扩展名
    private static String suppFileName(String fileName) {
        if (fileName.split("\\.").length == 1) {
            return fileName + JSON_EXTENSION;
        }
        return fileName;
    }

    /**
     * @return 包含目录下所有文件的Set集合
     * @deprecated 因为是Set集合，所以集合内的元素是无序的，并且，该集合可变，可以任意添加或修改元素
     */
    @Deprecated
    public HashSet<File> listFiles() {
        File[] files = this.modFileDirectory.listFiles();
        if (files == null) {
            // 返回空集合
            return new HashSet<>();
        }
        return new HashSet<>(Arrays.asList(files));
    }

    /**
     * @return 包含该目录所有文件的不可变的List集合
     * @apiNote Java貌似没有对中文的拼音排序做很好的支持，因此，中文的排序依然是无序的
     */
    public List<File> toImmutableFileList() {
        File[] files = this.modFileDirectory.listFiles();
        if (files == null) {
            return List.of();
        }
        // 一些操作系统下文件排序可能不是按字母排序
        return Stream.of(files).sorted(Comparator.comparing(file -> file.getName().toLowerCase())).toList();
    }

    public List<File> toImmutableFileList(Predicate<File> filter) {
        File[] files = this.modFileDirectory.listFiles();
        if (files == null) {
            return List.of();
        }
        // 一些操作系统下文件排序可能不是按字母排序
        return Stream.of(files).filter(filter).sorted(Comparator.comparing(file -> file.getName().toLowerCase())).toList();
    }

    // 检查该目录下的文件是否存在
    public boolean fileExists(String fileName) {
        fileName = suppFileName(fileName);
        File file = this.file(fileName);
        return file.exists();
    }

    @Override
    public String toString() {
        return this.modFileDirectory.toString();
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

    // 保存json文件
    public static void saveJson(File file, Gson gson, JsonObject json) throws IOException {
        String jsonString = gson.toJson(json, JsonObject.class);
        try (BufferedWriter writer = WorldFormat.toWriter(file)) {
            writer.write(jsonString);
        }
    }

    // 加载json文件
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
     *
     * @apiNote 不要在本类中使用此方法
     */
    public static String removeExtension(String fileName) {
        if (fileName.endsWith(JSON_EXTENSION)) {
            return fileName.substring(0, fileName.lastIndexOf("."));
        }
        return fileName;
    }
}
