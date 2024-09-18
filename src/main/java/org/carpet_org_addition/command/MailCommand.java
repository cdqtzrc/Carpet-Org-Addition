package org.carpet_org_addition.command;

import carpet.patches.EntityPlayerMPFake;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import org.carpet_org_addition.exception.CommandExecuteIOException;
import org.carpet_org_addition.util.CommandUtils;
import org.carpet_org_addition.util.MessageUtils;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.express.Express;
import org.carpet_org_addition.util.express.ExpressManager;
import org.carpet_org_addition.util.express.ExpressManagerInterface;
import org.carpet_org_addition.util.screen.ShipExpressScreenHandler;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MailCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("mail")
                .requires(source -> true)
                .then(CommandManager.literal("ship")
                        .then(CommandManager.argument(CommandUtils.PLAYER, EntityArgumentType.player())
                                .executes(MailCommand::ship)))
                .then(CommandManager.literal("receive")
                        .then(CommandManager.argument("id", IntegerArgumentType.integer(1))
                                .suggests(receiveSuggests(true))
                                .executes(MailCommand::receive)))
                .then(CommandManager.literal("cancel")
                        .then(CommandManager.argument("id", IntegerArgumentType.integer(1))
                                .suggests(receiveSuggests(false))
                                .executes(MailCommand::cancel)))
                .then(CommandManager.literal("list")
                        .executes(MailCommand::list))
                .then(CommandManager.literal("multiple")
                        .then(CommandManager.argument(CommandUtils.PLAYER, EntityArgumentType.player())
                                .executes(MailCommand::shipMultipleExpress))));
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
    private static int ship(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity sourcePlayer = CommandUtils.getSourcePlayer(context);
        ServerPlayerEntity targetPlayer = CommandUtils.getArgumentPlayer(context);
        // 限制只允许发送给其他真玩家
        checkPlayer(sourcePlayer, targetPlayer);
        MinecraftServer server = context.getSource().getServer();
        ExpressManager expressManager = ((ExpressManagerInterface) server).getExpressManager();
        try {
            // 将快递信息添加到快递管理器
            expressManager.put(new Express(server, sourcePlayer, targetPlayer, expressManager.generateNumber()));
        } catch (IOException e) {
            throw new CommandExecuteIOException(e);
        }
        return 1;
    }

    // 发送多个快递
    private static int shipMultipleExpress(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity sourcePlayer = CommandUtils.getSourcePlayer(context);
        ServerPlayerEntity targetPlayer = CommandUtils.getArgumentPlayer(context);
        checkPlayer(sourcePlayer, targetPlayer);
        SimpleInventory inventory = new SimpleInventory(27);
        SimpleNamedScreenHandlerFactory screen = new SimpleNamedScreenHandlerFactory((i, inv, player)
                -> new ShipExpressScreenHandler(i, inv, sourcePlayer, targetPlayer, inventory),
                TextUtils.getTranslate("carpet.commands.multiple.gui"));
        sourcePlayer.openHandledScreen(screen);
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
                throw new CommandExecuteIOException(e);
            }
            return 1;
        }
        throw CommandUtils.createException("carpet.commands.mail.receive.recipient");
    }

    // 撤回快递
    private static int cancel(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = CommandUtils.getSourcePlayer(context);
        Express express = getExpress(context);
        if (express.isSender(player)) {
            try {
                express.cancel();
            } catch (IOException e) {
                throw new CommandExecuteIOException(e);
            }
            return 1;
        }
        throw CommandUtils.createException("carpet.commands.mail.cancel.recipient");
    }

    // 列出快递
    private static int list(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        final ServerPlayerEntity player = CommandUtils.getSourcePlayer(context);
        MinecraftServer server = context.getSource().getServer();
        ExpressManager expressManager = ((ExpressManagerInterface) server).getExpressManager();
        List<Express> list = expressManager.stream().toList();
        if (list.isEmpty()) {
            // 没有快递被列出
            MessageUtils.sendCommandFeedback(context, "carpet.commands.mail.list.empty");
        }
        list.forEach(express -> list(player, express));
        return list.size();
    }

    private static void list(ServerPlayerEntity player, Express express) {
        ArrayList<MutableText> list = new ArrayList<>();
        MutableText text;
        if (express.isRecipient(player)) {
            text = TextUtils.createText("[R]");
            // 点击接收
            TextUtils.command(text, "/mail receive " + express.getId(), null, Formatting.AQUA, false);
            list.add(TextUtils.getTranslate("carpet.commands.mail.list.receive"));
            list.add(TextUtils.createEmpty());
        } else if (express.isSender(player)) {
            text = TextUtils.createText("[C]");
            // 点击撤回
            TextUtils.command(text, "/mail cancel " + express.getId(), null, Formatting.AQUA, false);
            list.add(TextUtils.getTranslate("carpet.commands.mail.list.sending"));
            list.add(TextUtils.createEmpty());
        } else {
            text = TextUtils.createText("[?]");
        }
        list.add(TextUtils.getTranslate("carpet.commands.mail.list.id", express.getId()));
        list.add(TextUtils.getTranslate("carpet.commands.mail.list.sender", express.getSender()));
        list.add(TextUtils.getTranslate("carpet.commands.mail.list.recipient", express.getRecipient()));
        list.add(TextUtils.getTranslate("carpet.commands.mail.list.item",
                TextUtils.getTranslate(express.getExpress().getTranslationKey()), express.getExpress().getCount()));
        list.add(TextUtils.getTranslate("carpet.commands.mail.list.time", express.getTime()));
        // 拼接字符串
        text = TextUtils.hoverText(text, TextUtils.appendList(list));
        MessageUtils.sendCommandFeedback(player.getCommandSource(), "carpet.commands.mail.list.each",
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
            throw CommandUtils.createException("carpet.commands.mail.receive.non_existent", id);
        }
        return optional.get();
    }

    // 检查玩家是否是自己或假玩家
    private static void checkPlayer(ServerPlayerEntity sourcePlayer, ServerPlayerEntity targetPlayer) throws CommandSyntaxException {
        if (sourcePlayer == targetPlayer || targetPlayer instanceof EntityPlayerMPFake) {
            throw CommandUtils.createException("carpet.commands.mail.check_player");
        }
    }
}
