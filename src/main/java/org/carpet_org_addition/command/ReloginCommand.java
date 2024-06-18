package org.carpet_org_addition.command;

import carpet.utils.CommandHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.util.CommandUtils;
import org.carpet_org_addition.util.MessageUtils;
import org.carpet_org_addition.util.task.ReLoginTask;
import org.carpet_org_addition.util.task.ServerTaskManagerInterface;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class ReloginCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("relogin")
                .requires(source -> CommandHelper.canUseCommand(source, CarpetOrgAdditionSettings.commandPlayerAction))
                .then(CommandManager.argument("name", StringArgumentType.string())
                        .suggests(suggests())
                        .then(CommandManager.argument("interval", IntegerArgumentType.integer(1))
                                .executes(ReloginCommand::setReLogin))
                        .then(CommandManager.literal("stop")
                                .executes(ReloginCommand::setStop))));
    }

    // 补全玩家名称
    private static @NotNull SuggestionProvider<ServerCommandSource> suggests() {
        return (context, builder) -> {
            MinecraftServer server = context.getSource().getServer();
            ServerTaskManagerInterface instance = ServerTaskManagerInterface.getInstance(server);
            List<String> taskList = instance.getTaskList().stream()
                    .filter(task -> task instanceof ReLoginTask)
                    .map(task -> ((ReLoginTask) task).getName()).toList();
            List<String> onlineList = server.getPlayerManager().getPlayerList().stream()
                    .map(player -> player.getName().getString()).toList();
            HashSet<String> players = new HashSet<>();
            players.addAll(taskList);
            players.addAll(onlineList);
            return CommandSource.suggestMatching(players.stream(), builder);
        };
    }

    // 设置不断重新上线下线
    private static int setReLogin(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        // 获取目标假玩家名
        String name = StringArgumentType.getString(context, "name");
        int interval = IntegerArgumentType.getInteger(context, "interval");
        MinecraftServer server = context.getSource().getServer();
        ServerPlayerEntity fakePlayer = server.getPlayerManager().getPlayer(name);
        if (fakePlayer == null) {
            // 玩家不存在
            throw CommandUtils.createException("argument.entity.notfound.player");
        } else {
            // 目标玩家不是假玩家
            CommandUtils.checkFakePlayer(fakePlayer);
        }
        ServerTaskManagerInterface instance = ServerTaskManagerInterface.getInstance(server);
        ReLoginTask task = ReloginCommand.getReLoginTask(instance, name);
        if (task == null) {
            // 添加任务
            instance.addTask(new ReLoginTask(name, interval, server, fakePlayer.getServerWorld().getRegistryKey()));
        } else {
            // 修改周期时间
            task.setInterval(interval);
            MessageUtils.sendCommandFeedback(context, "carpet.commands.relogin.set_interval", name, interval);
        }
        return interval;
    }

    // 获取假玩家周期上下线任务
    private static ReLoginTask getReLoginTask(ServerTaskManagerInterface instance, String name) {
        List<ReLoginTask> list = instance.getTaskList().stream()
                .filter(task -> task instanceof ReLoginTask)
                .map(task -> (ReLoginTask) task).toList();
        for (ReLoginTask task : list) {
            if (Objects.equals(task.getName(), name)) {
                return task;
            }
        }
        return null;
    }

    // 停止重新上线下线
    private static int setStop(CommandContext<ServerCommandSource> context) {
        // 获取目标假玩家名
        String name = StringArgumentType.getString(context, "name");
        ServerTaskManagerInterface instance = ServerTaskManagerInterface.getInstance(context.getSource().getServer());
        instance.getTaskList().stream()
                .filter(task -> task instanceof ReLoginTask)
                .map(task -> (ReLoginTask) task)
                .filter(reLoginTask -> Objects.equals(name, reLoginTask.getName()))
                .forEach(ReLoginTask::stop);
        return 1;
    }
}