package org.carpet_org_addition.util.helpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import org.carpet_org_addition.CarpetOrgAddition;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;

/**
 * @see <a href="https://zh.minecraft.wiki/w/Java%E7%89%88%E4%B8%96%E7%95%8C%E6%A0%BC%E5%BC%8F">世界格式</a>
 */
public class WorldFormat {
    public static final String JSON_EXTENSION = ".json";
    private final File modFileDirectory;

    /**
     * 尝试创建一个存档目录下的文件夹
     *
     * @param server      游戏当前运行的服务器，用来获取操作系统下服务器存档的路径
     * @param directories 一个字符串数组，数组中第一个元素表示第二级子目录，第二个元素表示第三级子目录，以此类推
     * @apiNote 第一级目录是carpetorgaddition文件夹
     */
    public WorldFormat(MinecraftServer server, String... directories) {
        // 获取服务器存档保存文件的路径
        Path path = server.getSavePath(WorldSavePath.ROOT).resolve(CarpetOrgAddition.MOD_NAME_LOWER_CASE);
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
        CarpetOrgAddition.LOGGER.warn(this.modFileDirectory + "文件夹创建失败");
    }

    /**
     * 创建一个当前目录下的文件对象，只创建文件对象，不创建文件
     *
     * @param fileName 文件名，如果没有扩展名，则自动添加json作为扩展名
     */
    public File getModFile(String fileName) {
        return new File(this.modFileDirectory, suppFileName(fileName));
    }

    public File getFile(String fileName) {
        fileName = suppFileName(fileName);
        return new File(this.modFileDirectory, fileName);
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
     * @return 构建一个可以格式化json内容的Gson对象
     */
    public static Gson createGson() {
        return new GsonBuilder().setPrettyPrinting().create();
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
        Gson gson = createGson();
        BufferedReader reader = toReader(file);
        try (reader) {
            return gson.fromJson(reader, JsonObject.class);
        }
    }

    public static boolean jsonHasElement(JsonObject json, String... elements) {
        for (String element : elements) {
            if (json.has(element)) {
                continue;
            }
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.modFileDirectory.toString();
    }

    // 补全文件扩展名
    private static String suppFileName(String fileName) {
        if (fileName.split("\\.").length == 1) {
            return fileName + JSON_EXTENSION;
        }
        return fileName;
    }

    // 列出当前目录下的所有文件
    public HashSet<File> listFiles() {
        File[] files = this.modFileDirectory.listFiles();
        if (files == null) {
            return new HashSet<>();
        }
        return new HashSet<>(Arrays.asList(files));
    }

    // 检查该目录下的文件是否存在
    public boolean fileExists(String fileName) {
        fileName = suppFileName(fileName);
        File file = this.getModFile(fileName);
        return file.exists();
    }


    /**
     * 复制一个文件夹
     *
     * @param from 复制哪个文件夹
     * @param to   把文件夹复制到哪里
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void copyFolder(File from, File to) throws IOException {
        to.mkdirs();
        // 列出文件夹下的所有文件
        File[] files = from.listFiles();
        if (files != null) {
            for (File file : files) {
                // 如果是文件，复制文件
                if (file.isFile()) {
                    FileInputStream input = new FileInputStream(file);
                    FileOutputStream output = new FileOutputStream(new File(to, file.getName()));
                    try (input; output) {
                        byte[] bytes = new byte[1024];
                        int len;
                        while ((len = input.read(bytes)) != -1)
                            output.write(bytes, 0, len);
                    }
                } else {
                    // 如果是文件夹，递归复制文件
                    copyFolder(file, new File(to, file.getName()));
                }
            }
        }
    }
}
