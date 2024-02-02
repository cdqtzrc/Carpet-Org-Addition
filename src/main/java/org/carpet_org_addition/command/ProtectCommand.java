package org.carpet_org_addition.command;

import carpet.patches.EntityPlayerMPFake;
import carpet.utils.CommandHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.exception.NotFoundPlayerException;
import org.carpet_org_addition.util.CommandUtils;
import org.carpet_org_addition.util.GameUtils;
import org.carpet_org_addition.util.MessageUtils;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.fakeplayer.FakePlayerProtectManager;
import org.carpet_org_addition.util.fakeplayer.FakePlayerProtectType;

import java.util.List;

public class ProtectCommand {
    //用来补全受保护玩家的名字
    private static SuggestionProvider<ServerCommandSource> getServerCommandSourceSuggestionProvider() {
        return (context, builder) -> {
            List<ServerPlayerEntity> list = context.getSource().getServer().getPlayerManager().getPlayerList();
            return CommandSource.suggestMatching(
                    list.stream().filter(player -> player instanceof EntityPlayerMPFake fakePlayer
                                                   && FakePlayerProtectManager.isProtect(fakePlayer))
                            .map(player -> player.getName().getString()), builder);
        };
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((CommandManager.literal("protect")
                        .requires(source -> CommandHelper.canUseCommand(source, CarpetOrgAdditionSettings.commandProtect))
                        .then(CommandManager.literal("add")
                                .then(CommandManager.argument("targets", EntityArgumentType.player())
                                        .executes(context -> setFakePlayerProtectType(context.getSource(), EntityArgumentType.getPlayer(context, "targets"), FakePlayerProtectType.KILL))
                                        .then(CommandManager.literal("kill").executes(context -> setFakePlayerProtectType(context.getSource(), EntityArgumentType.getPlayer(context, "targets"), FakePlayerProtectType.KILL)))
                                        .then(CommandManager.literal("damage").executes(context -> setFakePlayerProtectType(context.getSource(), EntityArgumentType.getPlayer(context, "targets"), FakePlayerProtectType.DAMAGE)))
                                        .then(CommandManager.literal("death").executes(context -> setFakePlayerProtectType(context.getSource(), EntityArgumentType.getPlayer(context, "targets"), FakePlayerProtectType.DEATH)))))
                        .then(CommandManager.literal("list").executes(ProtectCommand::ListProtectPlayer
                                )
                        ).then(CommandManager.literal("remove").then(CommandManager.literal("name").then(CommandManager.argument("player", StringArgumentType.string())
                                        .suggests(getServerCommandSourceSuggestionProvider())
                                        .executes(context -> {
                                            String name = StringArgumentType.getString(context, "player");
                                            try {
                                                return setFakePlayerProtectType(context.getSource(), GameUtils.getPlayer(context.getSource().getServer(), name), FakePlayerProtectType.NONE);
                                            } catch (NotFoundPlayerException e) {
                                                throw CommandUtils.createException("carpet.commands.protect.remove.not_found", name);
                                            }
                                        }))
                                ).then(CommandManager.literal("all").executes(context -> fromProtectListRemoveAllPlayer(context.getSource()))
                                )
                        )
                )
        );
    }

    //列出所有受保护的玩家
    private static int ListProtectPlayer(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        List<ServerPlayerEntity> list = source.getServer().getPlayerManager().getPlayerList();
        int count = getProtectPlayerCount(source.getServer());
        if (count == 0) {
            MessageUtils.sendCommandFeedback(source, "carpet.commands.protect.list.no_players");
        } else {
            MessageUtils.sendCommandFeedback(source, "carpet.commands.protect.list", count);
        }
        for (ServerPlayerEntity player : list) {
            if (player instanceof EntityPlayerMPFake fakePlayer && FakePlayerProtectManager.isProtect(fakePlayer)) {
                MessageUtils.sendTextMessage(source, TextUtils.appendAll(fakePlayer.getDisplayName(), ": "
                        , FakePlayerProtectManager.getProtect(fakePlayer).getText()));
            }
        }
        return count;
    }

    //设置假玩家的保护类型
    private static int setFakePlayerProtectType(ServerCommandSource source, PlayerEntity player, FakePlayerProtectType protectType) throws CommandSyntaxException {
        if (player instanceof EntityPlayerMPFake fakePlayer) {
            if (protectType.isProtect()) {
                MutableText type = protectType.getText();
                return addPlayerToProtectList(source, fakePlayer, protectType, type);
            } else {
                return fromProtectListRemovePlayer(source, fakePlayer);
            }
        } else {
            //不是假玩家的反馈消息
            throw CommandUtils.createException("carpet.command.not_fake_player", player.getDisplayName());
        }
    }

    //添加玩家到受保护玩家列表
    private static int addPlayerToProtectList(ServerCommandSource source, EntityPlayerMPFake fakePlayer, FakePlayerProtectType protectType, MutableText type) {
        Text playerName = fakePlayer.getDisplayName();
        if (FakePlayerProtectManager.isProtect(fakePlayer)) {
            boolean flag = FakePlayerProtectManager.setProtect(fakePlayer, protectType);
            if (flag) {
                MessageUtils.sendCommandFeedback(source, "carpet.commands.protect.modify", playerName, type);
                return 1;
            } else {
                MessageUtils.sendCommandFeedback(source, "carpet.commands.protect.already", playerName, type);
                return 0;
            }
        } else {
            FakePlayerProtectManager.setProtect(fakePlayer, protectType);
            // 将%s加入受保护玩家列表(类型:%s)
            MessageUtils.sendCommandFeedback(source, "carpet.commands.protect.add", playerName, type);
            return 1;
        }
    }

    //设置假玩家不受保护
    private static int fromProtectListRemovePlayer(ServerCommandSource source, EntityPlayerMPFake fakePlayer) {
        Text playerName = fakePlayer.getDisplayName();
        if (FakePlayerProtectManager.isProtect(fakePlayer)) {
            FakePlayerProtectManager.setProtect(fakePlayer, FakePlayerProtectType.NONE);
            MessageUtils.sendCommandFeedback(source, "carpet.commands.protect.remove", playerName);
            return 1;
        } else {
            MessageUtils.sendCommandFeedback(source, "carpet.commands.protect.not_found");
            return 0;
        }
    }

    //设置所有玩家不受保护
    private static int fromProtectListRemoveAllPlayer(ServerCommandSource source) {
        List<ServerPlayerEntity> list = source.getServer().getPlayerManager().getPlayerList();
        int count = 0;
        for (ServerPlayerEntity player : list) {
            if (player instanceof EntityPlayerMPFake fakePlayer && FakePlayerProtectManager.isProtect(fakePlayer)) {
                FakePlayerProtectManager.setProtect(fakePlayer, FakePlayerProtectType.NONE);
                count++;
            }
        }
        if (count == 0) {
            MessageUtils.sendCommandFeedback(source, "carpet.commands.protect.remove.all.no_players");
        } else {
            MessageUtils.sendCommandFeedback(source, "carpet.commands.protect.remove.all", count);
        }
        return count;
    }

    //获取受保护玩家数量
    private static int getProtectPlayerCount(MinecraftServer server) {
        List<ServerPlayerEntity> list = server.getPlayerManager().getPlayerList();
        int count = 0;
        for (ServerPlayerEntity player : list) {
            if (player instanceof EntityPlayerMPFake fakePlayer && FakePlayerProtectManager.isProtect(fakePlayer)) {
                count++;
            }
        }
        return count;
    }
}