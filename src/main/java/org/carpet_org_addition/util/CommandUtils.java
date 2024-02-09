package org.carpet_org_addition.util;

import carpet.patches.EntityPlayerMPFake;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class CommandUtils {
    private CommandUtils() {
    }

    /**
     * 根据命令执行上下文获取玩家对象
     *
     * @param context 用来获取玩家的命令执行上下文
     * @return 命令的执行玩家
     * @throws CommandSyntaxException 如果命令执行者不是玩家，则抛出该异常
     */
    public static ServerPlayerEntity getPlayer(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        return getPlayer(context.getSource());
    }

    /**
     * 根据命令源获取玩家对象
     *
     * @param source 用来获取玩家的命令源
     * @return 命令的执行玩家
     * @throws CommandSyntaxException 如果命令执行者不是玩家，则抛出该异常
     */
    public static ServerPlayerEntity getPlayer(ServerCommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) {
            throw new SimpleCommandExceptionType(TextUtils.getTranslate("carpet.command.source.not_player")).create();
        }
        return player;
    }

    /**
     * 创建一个命令语法参数异常对象
     *
     * @param key 异常信息的翻译键
     * @return 命令语法参数异常
     */
    public static CommandSyntaxException createException(String key, Object... obj) {
        return new SimpleCommandExceptionType(TextUtils.getTranslate(key, obj)).create();
    }

    /**
     * 获取命令执行上下文中的玩家对象
     */
    public static ServerPlayerEntity getPlayerEntity(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        return EntityArgumentType.getPlayer(context, "player");
    }

    /**
     * 判断指定玩家是否为假玩家，如果不是会直接抛出异常。<br/>
     *
     * @param fakePlayer 要检查是否为假玩家的玩家对象
     * @return 要么抛出异常，要么返回true，永远不会返回false
     * @throws CommandSyntaxException 如果指定玩家不是假玩家抛出异常
     */
    public static boolean checkFakePlayer(PlayerEntity fakePlayer) throws CommandSyntaxException {
        if (fakePlayer instanceof EntityPlayerMPFake) {
            return true;
        } else {
            //不是假玩家的反馈消息
            throw createException("carpet.command.not_fake_player", fakePlayer.getDisplayName());
        }
    }

    /**
     * json文件已存在
     */
    public static CommandSyntaxException createJsonFileAlreadyExistException() {
        return new SimpleCommandExceptionType(TextUtils.getTranslate("carpet.command.file.json.file_already_exist")).create();
    }

    /**
     * 无法解析json文件
     */
    public static CommandSyntaxException createJsonParseException() {
        return new SimpleCommandExceptionType(TextUtils.getTranslate("carpet.command.file.json.json_parse")).create();
    }

    /**
     * 无法读取json文件
     */
    public static CommandSyntaxException createReadJsonFileException() {
        return new SimpleCommandExceptionType(TextUtils.getTranslate("carpet.command.file.json.read")).create();
    }

    /**
     * 找不到json文件
     */
    public static CommandSyntaxException createNotJsonFileException() {
        return new SimpleCommandExceptionType(TextUtils.getTranslate("carpet.command.file.json.not_file")).create();
    }

}
