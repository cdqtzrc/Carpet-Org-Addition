package org.carpet_org_addition.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import org.carpet_org_addition.util.CommandUtils;
import org.carpet_org_addition.util.MessageUtils;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.express.Express;
import org.carpet_org_addition.util.express.ExpressManager;
import org.carpet_org_addition.util.express.ExpressManagerInterface;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

public class ExpressCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("express")
                .requires(source -> true)
                .then(CommandManager.literal("post")
                        .then(CommandManager.argument(CommandUtils.PLAYER, EntityArgumentType.player())
                                .executes(ExpressCommand::post)))
                .then(CommandManager.literal("receive")
                        .then(CommandManager.argument("id", IntegerArgumentType.integer(1))
                                .suggests(receiveSuggests(true))
                                .executes(ExpressCommand::receive)))
                .then(CommandManager.literal("cancel")
                        .then(CommandManager.argument("id", IntegerArgumentType.integer(1))
                                .suggests(receiveSuggests(false))
                                .executes(ExpressCommand::cancel)))
                .then(CommandManager.literal("list")
                        .executes(ExpressCommand::list)));
    }

    // 自动补全快递单号
    private static @NotNull SuggestionProvider<ServerCommandSource> receiveSuggests(boolean recipient) {
        return (context, builder) -> {
            ServerPlayerEntity player = context.getSource().getPlayer();
            if (player == null) {
                return CommandSource.suggestMatching(List.of(), builder);
            }
            MinecraftServer server = context.getSource().getServer();
            ExpressManager expressManager = ((ExpressManagerInterface) server).getExpressManager();
            // 获取所有发送给自己的快递（或所有自己发送的快递）
            List<String> list = expressManager.stream()
                    .filter(express -> recipient ? express.isRecipient(player) : express.isSender(player))
                    .map(express -> Integer.toString(express.getId())).toList();
            return CommandSource.suggestMatching(list, builder);
        };
    }

    // 发送快递
    private static int post(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = CommandUtils.getArgumentPlayer(context);
        MinecraftServer server = context.getSource().getServer();
        ExpressManager expressManager = ((ExpressManagerInterface) server).getExpressManager();
        try {
            // 将快递信息添加到快递管理器
            expressManager.put(new Express(server, CommandUtils.getSourcePlayer(context), player, expressManager.generateNumber()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return 1;
    }

    // 接收快递
    private static int receive(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = CommandUtils.getSourcePlayer(context);
        // 获取快递
        Express express = getExpress(context);
        // 只能接收发送给自己的快递
        if (express.isRecipient(player)) {
            try {
                express.receive();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return 1;
        }
        throw CommandUtils.createException("carpet.commands.express.receive.recipient");
    }

    // 撤回快递
    private static int cancel(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = CommandUtils.getSourcePlayer(context);
        Express express = getExpress(context);
        if (express.isSender(player)) {
            try {
                express.cancel();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return 1;
        }
        throw CommandUtils.createException("carpet.commands.express.cancel.recipient");
    }

    // 列出快递
    private static int list(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        final ServerPlayerEntity player = CommandUtils.getSourcePlayer(context);
        MinecraftServer server = context.getSource().getServer();
        ExpressManager expressManager = ((ExpressManagerInterface) server).getExpressManager();
        expressManager.stream().forEach(express -> list(player, express));
        return (int) expressManager.stream().count();
    }

    private static void list(ServerPlayerEntity player, Express express) {
        ArrayList<MutableText> list = new ArrayList<>();
        MutableText text;
        if (express.isRecipient(player)) {
            text = TextUtils.createText("[R]");
            // 点击接收
            TextUtils.command(text, "/express receive " + express.getId(), null, Formatting.AQUA, false);
            list.add(TextUtils.getTranslate("carpet.commands.express.list.receive"));
            list.add(TextUtils.createEmpty());
        } else if (express.isSender(player)) {
            text = TextUtils.createText("[C]");
            // 点击撤回
            TextUtils.command(text, "/express cancel " + express.getId(), null, Formatting.AQUA, false);
            list.add(TextUtils.getTranslate("carpet.commands.express.list.sending"));
            list.add(TextUtils.createEmpty());
        } else {
            text = TextUtils.createText("[?]");
        }
        list.add(TextUtils.getTranslate("carpet.commands.express.list.id", express.getId()));
        list.add(TextUtils.getTranslate("carpet.commands.express.list.sender", express.getSender()));
        list.add(TextUtils.getTranslate("carpet.commands.express.list.recipient", express.getRecipient()));
        list.add(TextUtils.getTranslate("carpet.commands.express.list.item",
                express.getExpress().toHoverableText(), express.getExpress().getCount()));
        list.add(TextUtils.getTranslate("carpet.commands.express.list.time", express.getTime()));
        // 拼接字符串
        StringJoiner sj = new StringJoiner("\n");
        for (MutableText mutableText : list) {
            sj.add(mutableText.getString());
        }
        text = TextUtils.hoverText(text, TextUtils.createText(sj.toString()));
        MessageUtils.sendCommandFeedback(player.getCommandSource(), "carpet.commands.express.list.each",
                express.getId(), express.getExpress().toHoverableText(), express.getSender(), express.getRecipient(), text);
    }

    // 获取快递
    private static @NotNull Express getExpress(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        MinecraftServer server = context.getSource().getServer();
        ExpressManager expressManager = ((ExpressManagerInterface) server).getExpressManager();
        // 获取快递单号
        int id = IntegerArgumentType.getInteger(context, "id");
        // 查找指定单号的快递
        Optional<Express> optional = expressManager.binarySearch(id);
        if (optional.isEmpty()) {
            throw CommandUtils.createException("carpet.commands.express.receive.non_existent", id);
        }
        return optional.get();
    }
}
