package org.carpet_org_addition.util.constant;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import org.carpet_org_addition.util.TextUtils;

public class CommandSyntaxExceptionConstants {

    /**
     * json文件已存在
     */
    public static final CommandSyntaxException JSON_FILE_ALREADY_EXIST_EXCEPTION = new SimpleCommandExceptionType(TextUtils.getTranslate("carpet.command.file.json.file_already_exist")).create();

    /**
     * 无法解析json文件
     */
    public static final CommandSyntaxException JSON_PARSE_EXCEPTION = new SimpleCommandExceptionType(TextUtils.getTranslate("carpet.command.file.json.json_parse")).create();

    /**
     * 无法读取json文件
     */
    public static final CommandSyntaxException READ_JSON_FILE_EXCEPTION = new SimpleCommandExceptionType(TextUtils.getTranslate("carpet.command.file.json.read")).create();

    /**
     * 找不到json文件
     */
    public static final CommandSyntaxException NOT_JSON_FILE_EXCEPTION = new SimpleCommandExceptionType(TextUtils.getTranslate("carpet.command.file.json.not_file")).create();
}
