package org.carpet_org_addition.util.helpers;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ItemPredicateArgumentType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import org.carpet_org_addition.CarpetOrgAddition;
import org.carpet_org_addition.util.CommandUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

@SuppressWarnings("unused")
public class CraftPresets {
    private final String[] itemOrTag = new String[9];
    /**
     * 配方预设的名称
     */
    private transient final String name;
    /**
     * 包含配方的长字符串
     */
    private transient final String presets;

    public CraftPresets(String name, String presets) {
        this.name = suppFileName(name);
        this.presets = presets;
    }

    // 将合成预设保存到本地文件
    public void saveCraftRecipe(MinecraftServer server) throws CommandSyntaxException {
        // 将字符串以空格或逗号切割
        String[] split = this.presets.split("[ ,，]+");
        if (split.length != 9) {
            CarpetOrgAddition.LOGGER.warn("无法解析字符串:“" + this.presets + "”");
            throw CommandUtils.getException("carpet.commands.presets.parse.string.fail", presets);
        }
        // 填充数组，填充前源数组中的元素去除头尾的空格（虽然可能不会有空格）
        for (int index = 0; index < split.length; index++) {
            itemOrTag[index] = split[index].strip();
        }
        // 将本类的对象转换为json字符串
        Gson gson = new Gson();
        String json = gson.toJson(this);
        try {
            // 将json字符串以UTF-8编码写入本地文件
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(getFile(server), name), StandardCharsets.UTF_8));
            try (bw) {
                bw.write(json);
            }
        } catch (IOException e) {
            CarpetOrgAddition.LOGGER.warn("将" + name + "写入本地文件时出现意外问题", e);
        }
    }

    // 从本地文件读取合成预设
    public static CraftPresets fromFileLoadCraftRecipe(MinecraftServer server, String fileName) throws CommandSyntaxException {
        // 找到指定的文件
        File file = new File(getFile(server), suppFileName(fileName));
        if (!file.exists()) {
            // 如果文件不存在，直接抛出异常
            throw CommandUtils.createNotJsonFileException();
        }
        // 读取json文件的内容
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            // 以UTF-8编码读取json文件中所有内容
            BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            // 无法读取json文件
            throw CommandUtils.createReadJsonFileException();
        }
        Gson gson = new Gson();
        try {
            // 将json字符串反序列化为合成预设对象
            return gson.fromJson(sb.toString(), CraftPresets.class);
        } catch (JsonParseException e) {
            // 无法解析json文件时抛出异常
            throw CommandUtils.createJsonParseException();
        }
    }

    // 根据字符串数组获取物品匹配器数组
    public ItemMatcher[] getItemMarcher(CommandRegistryAccess commandRegistryAccess, String fileName) throws CommandSyntaxException {
        ItemMatcher[] itemMatcherArr = new ItemMatcher[9];
        for (int index = 0; index < itemMatcherArr.length; index++) {
            // 获取字符串中的每一个元素
            String itemOrTag;
            try {
                // 处理索引越界，越界的部分填入空气
                itemOrTag = this.itemOrTag[index];
            } catch (ArrayIndexOutOfBoundsException e) {
                CarpetOrgAddition.LOGGER.warn("获取物品匹配器数组时出现索引越界", e);
                itemMatcherArr[index] = new ItemMatcher();
                continue;
            }
            // 如果字符串为null，或字符串中没有内容，创建一个空气的物品匹配器对象并添加进数组
            if (itemOrTag == null || itemOrTag.isEmpty()) {
                itemMatcherArr[index] = new ItemMatcher();
                continue;
            }
            if (itemOrTag.startsWith("#")) {
                // 创建一个字符串读取器对象
                StringReader stringReader = new StringReader(itemOrTag);
                // 从字符串读取器获取物品标签
                ItemPredicateArgumentType.ItemStackPredicateArgument parse =
                        new ItemPredicateArgumentType(commandRegistryAccess).parse(stringReader);
                // 创建一个以物品标签匹配物品的物品匹配器对象并添加进数组
                itemMatcherArr[index] = new ItemMatcher(parse);
            } else {
                // 将字符串切割为命名空间和物品名称两部分
                String[] split = itemOrTag.split(":");
                Item item;
                if (split.length == 1) {
                    // 如果数组长度为1，获取一个命名空间为默认，物品名称为指定名称的物品
                    item = Registries.ITEM.get(Identifier.of("minecraft", split[0]));
                } else if (split.length == 2) {
                    // 如果数组长度为2，返回一个命名空间和物品名称都为指定名称的物品
                    item = Registries.ITEM.get(Identifier.of(split[0], split[1]));
                } else {
                    // 否则抛出异常
                    throw CommandUtils.getException("carpet.commands.presets.parse.item.fail", extractFileName(fileName), (index + 1));
                }
                // 创建一个直接以物品匹配物品的物品匹配器对象并添加进数组
                itemMatcherArr[index] = new ItemMatcher(item);
            }
        }
        return itemMatcherArr;
    }

    // 获取世界根目录下的presets文件夹对象，如果没有则创建
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static File getFile(MinecraftServer server) throws CommandSyntaxException {
        File file = server.getSavePath(WorldSavePath.ROOT).resolve("presets").resolve("craft").toFile();
        // 如果文件已存在并且不是文件夹，直接抛出异常
        if (file.isFile()) {
            throw CommandUtils.createJsonFileAlreadyExistException();
        }
        // 如果文件夹已存在，直接返回文件夹
        if (file.exists()) {
            return file;
        }
        // 否则，先创建文件夹，然后返回
        file.mkdirs();
        return file;
    }

    // 去除文件扩展名
    public static String extractFileName(String fileName) {
        if (fileName.endsWith(".json")) {
            // 从0索引截取到最后一个'.'字符的索引
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
        }
        return fileName;
    }

    // 补全文件扩展名
    public static String suppFileName(String fileName) {
        if (!fileName.endsWith(".json")) {
            fileName += ".json";
        }
        return fileName;
    }
}
