package org.carpet_org_addition.util;

import carpet.patches.EntityPlayerMPFake;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class CommandUtils {
    public static final String PLAYER = "player";

    private CommandUtils() {
    }

    /**
     * 根据命令执行上下文获取命令执行者玩家对象
     *
     * @param context 用来获取玩家的命令执行上下文
     * @return 命令的执行玩家
     * @throws CommandSyntaxException 如果命令执行者不是玩家，则抛出该异常
     */
    public static ServerPlayerEntity getSourcePlayer(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        return getSourcePlayer(context.getSource());
    }

    /**
     * 根据命令源获取命令执行者玩家对象
     *
     * @param source 用来获取玩家的命令源
     * @return 命令的执行玩家
     * @throws CommandSyntaxException 如果命令执行者不是玩家，则抛出该异常
     */
    public static ServerPlayerEntity getSourcePlayer(ServerCommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) {
            throw new SimpleCommandExceptionType(TextUtils.getTranslate("carpet.command.source.not_player")).create();
        }
        return player;
    }

    /**
     * 获取命令参数中的玩家对象
     */
    public static ServerPlayerEntity getArgumentPlayer(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        return EntityArgumentType.getPlayer(context, PLAYER);
    }

    /**
     * 获取命令参数中的玩家对象，并检查是不是假玩家
     */
    public static EntityPlayerMPFake getArgumentFakePlayer(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, PLAYER);
        checkFakePlayer(player);
        return (EntityPlayerMPFake) player;
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
     * 让一名玩家执行一条命令
     */
    public static void execute(ServerPlayerEntity player, String command) {
        CommandUtils.execute(player.getCommandSource(), command);
    }

    public static void execute(ServerCommandSource source, String command) {
        CommandManager commandManager = source.getServer().getCommandManager();
        commandManager.executeWithPrefix(source, command);
    }
}
