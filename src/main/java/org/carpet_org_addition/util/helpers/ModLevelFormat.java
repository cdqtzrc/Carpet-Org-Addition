package org.carpet_org_addition.util.helpers;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import org.carpet_org_addition.CarpetOrgAddition;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * @see <a href="https://zh.minecraft.wiki/w/Java%E7%89%88%E4%B8%96%E7%95%8C%E6%A0%BC%E5%BC%8F">世界格式</a>
 */
public class ModLevelFormat {
    private final File modFileDirectory;

    /**
     * 尝试创建一个存档目录下的文件夹
     *
     * @param server      游戏当前运行的服务器，用来获取操作系统下服务器存档的路径
     * @param directories 一个字符串数组，数组中第一个元素表示第二级子目录，第二个元素表示第三级子目录，以此类推
     * @apiNote 第一级目录是carpetorgaddition文件夹
     */
    public ModLevelFormat(MinecraftServer server, String... directories) {
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

    public File createModFile(String fileName) {
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

    @Override
    public String toString() {
        return this.modFileDirectory.toString();
    }
}
