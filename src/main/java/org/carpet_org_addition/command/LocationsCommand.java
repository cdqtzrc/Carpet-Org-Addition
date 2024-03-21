package org.carpet_org_addition.command;

import carpet.utils.CommandHelper;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.carpet_org_addition.CarpetOrgAddition;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.util.*;
import org.carpet_org_addition.util.helpers.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

//路径点管理器
public class LocationsCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("locations")
                .requires(source -> CommandHelper.canUseCommand(source, CarpetOrgAdditionSettings.commandLocations))
                .then(CommandManager.literal("add")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                                .executes(context -> addWayPoint(context, null))
                                .then(CommandManager.argument("pos", BlockPosArgumentType.blockPos())
                                        .executes(context -> addWayPoint(context, BlockPosArgumentType.getBlockPos(context, "pos"))))))
                .then(CommandManager.literal("list")
                        .executes(context -> listWayPoint(context, null)).then(CommandManager.argument("filter", StringArgumentType.string())
                                .executes(context -> listWayPoint(context, StringArgumentType.getString(context, "filter")))))
                .then(CommandManager.literal("supplement")
                        .then(CommandManager.argument("supp", StringArgumentType.string())
                                .suggests(getServerCommandSourceSuggestionProvider())
                                .then(CommandManager.literal("illustrate")
                                        .executes(context -> addIllustrateText(context, null))
                                        .then(CommandManager.argument("illustrate", StringArgumentType.string())
                                                .executes(context -> addIllustrateText(context, StringArgumentType.getString(context, "illustrate")))))
                                .then(CommandManager.literal("another_pos")
                                        .executes(context -> addAnotherPos(context, null))
                                        .then(CommandManager.argument("anotherPos", BlockPosArgumentType.blockPos())
                                                .executes(context -> addAnotherPos(context, BlockPosArgumentType.getBlockPos(context, "anotherPos")))))))
                .then(CommandManager.literal("info")
                        .then(CommandManager.argument("info", StringArgumentType.string())
                                .suggests(getServerCommandSourceSuggestionProvider())
                                .executes(LocationsCommand::showInfo)))
                .then(CommandManager.literal("delete")
                        .then(CommandManager.argument("delete", StringArgumentType.string())
                                .suggests(getServerCommandSourceSuggestionProvider())
                                .executes(LocationsCommand::deleteWayPoint)))
                .then(CommandManager.literal("set")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                                .suggests(getServerCommandSourceSuggestionProvider())
                                .executes(context -> setWayPoint(context, null))
                                .then(CommandManager.argument("pos", BlockPosArgumentType.blockPos())
                                        .executes(context -> setWayPoint(context, BlockPosArgumentType.getBlockPos(context, "pos")))))));
    }

    @NotNull
    //用来自动补全命令
    private static SuggestionProvider<ServerCommandSource> getServerCommandSourceSuggestionProvider() {
        return (context, builder) -> {
            File file = getFile(context.getSource().getWorld());
            File[] files = file.listFiles();
            if (files != null) {
                return CommandSource.suggestMatching(Arrays.stream(files).map(File::getName)
                        .filter(name -> name.endsWith(".json")).map(LocationsCommand::removeExtension)
                        .map(StringArgumentType::escapeIfRequired), builder);
            }
            CarpetOrgAddition.LOGGER.warn("无法列出/locations命令的建议");
            return null;
        };
    }

    //添加路径点
    private static int addWayPoint(CommandContext<ServerCommandSource> context, @Nullable BlockPos blockPos) throws CommandSyntaxException {
        ServerPlayerEntity player = CommandUtils.getSourcePlayer(context);
        //获取路径点名称和位置对象
        String name = StringArgumentType.getString(context, "name");
        if (blockPos == null) {
            blockPos = player.getBlockPos();
        }
        //创建路径点对象，但不赋值
        Location location;
        try {
            //从本地文件读取路径点对象
            location = new Location(blockPos, WorldUtils.getDimensionId(player.getWorld()), player);
        } catch (IllegalArgumentException e) {
            //不能为自定义维度添加路径点
            throw CommandUtils.createException("carpet.commands.locations.add.fail.unknown_dimension");
        }
        //获取文件对象
        File file = getFile(player.getWorld());
        File[] files = file.listFiles();
        String tempName = name + ".json";
        //遍历文件夹，检查该路径点是否已存在，如果存在，添加失败
        if (files != null) {
            for (File f : files) {
                if (tempName.equals(f.getName())) {
                    //路径点已存在
                    throw CommandUtils.createException("carpet.commands.locations.add.fail.already_exists", name);
                }
            }
        }
        try {
            //添加路径点并写入本地文件
            Location.saveLoc(file, location, name);
            //成功添加路径点
            MessageUtils.sendCommandFeedback(context.getSource(), "carpet.commands.locations.add.success", name, WorldUtils.toPosString(blockPos));
        } catch (IOException e) {
            CarpetOrgAddition.LOGGER.error(GameUtils.getPlayerName(player) + "在尝试将路径点写入本地文件时出现意外问题:", e);
        }
        return 1;
    }

    //列出所有路径点
    private static int listWayPoint(CommandContext<ServerCommandSource> context, @Nullable String filter) throws CommandSyntaxException {
        ServerPlayerEntity player = CommandUtils.getSourcePlayer(context);
        //获取并遍历文件夹
        File file = getFile(player.getWorld());
        File[] files = file.listFiles();
        int count = 0;
        if (files != null) {
            ServerCommandSource source = context.getSource();
            MessageUtils.sendStringMessage(source, "------------------------------");
            for (File f : files) {
                try {
                    String name = f.getName();
                    //只显示包含指定字符串的路径点，如果为null，显示所有路径点
                    if (filter != null && !name.contains(filter)) {
                        continue;
                    }
                    //从本地文件读取路径点对象
                    Location location;
                    try {
                        location = Location.loadLoc(file, name);
                    } catch (JsonParseException e) {
                        //无法解析坐标
                        MessageUtils.sendCommandFeedback(source, "carpet.commands.locations.list.parse", removeExtension(name));
                        continue;
                    }
                    //删除文件扩展名，然后在聊天栏输出路径点的文本
                    name = removeExtension(name);
                    MessageUtils.sendTextMessage(source, location.getText("[" + name + "] "));
                    count++;
                } catch (IOException e) {
                    CarpetOrgAddition.LOGGER.error(GameUtils.getPlayerName(player) + "在尝试将列出路径点时出现意外问题:", e);
                }
            }
            MessageUtils.sendStringMessage(source, "------------------------------");
        }
        return count;
    }

    //添加说明文本
    private static int addIllustrateText(CommandContext<ServerCommandSource> context, @Nullable String illustrate) throws CommandSyntaxException {
        ServerPlayerEntity player = CommandUtils.getSourcePlayer(context);
        String name = StringArgumentType.getString(context, "supp");
        ServerCommandSource source = context.getSource();
        try {
            //从本地文件读取路径点对象
            Location location = Location.loadLoc(getFile(player.getWorld()), name + ".json");
            boolean remove = false;
            if (illustrate == null || illustrate.isEmpty()) {
                illustrate = null;
                remove = true;
            }
            //为路径点对象设置说明文本
            location.setIllustrate(illustrate);
            //将路径点对象写入本地文件
            Location.saveLoc(getFile(player.getWorld()), location, name);
            if (remove) {
                //移除路径点的说明文本
                MessageUtils.sendCommandFeedback(source, "carpet.commands.locations.illustrate.remove", name);
            } else {
                //为路径点添加说明文本
                MessageUtils.sendCommandFeedback(source, "carpet.commands.locations.illustrate.add", illustrate, name);
            }
        } catch (JsonParseException e) {
            //无法解析坐标
            throw CommandUtils.createException("carpet.commands.locations.illustrate.parse");
        } catch (IOException e) {
            //无法添加说明文本
            CarpetOrgAddition.LOGGER.error("无法为路径点[" + name + "]添加说明文本", e);
            throw CommandUtils.createException("carpet.commands.locations.illustrate.io", name);
        }
        return 1;
    }

    //添加另一个坐标
    private static int addAnotherPos(CommandContext<ServerCommandSource> context, @Nullable BlockPos blockPos) throws CommandSyntaxException {
        ServerPlayerEntity player = CommandUtils.getSourcePlayer(context);
        ServerCommandSource source = context.getSource();
        String name = StringArgumentType.getString(context, "supp");
        try {
            //从本地文件读取路径点对象
            Location location = Location.loadLoc(getFile(player.getWorld()), name + ".json");
            try {
                //为路径点对象添加对向坐标
                if (blockPos == null) {
                    blockPos = player.getBlockPos();
                }
                location.addAnotherPos(blockPos);
                //将路径点对象写入本地文件
                Location.saveLoc(getFile(player.getWorld()), location, name);
                //添加对向坐标
                MessageUtils.sendCommandFeedback(source, "carpet.commands.locations.another.add");
            } catch (UnsupportedOperationException e) {
                //不能为末地坐标添加对向坐标
                MessageUtils.sendCommandFeedback(source, "carpet.commands.locations.another.add.not");
            }
        } catch (JsonParseException e) {
            //无法解析坐标
            throw CommandUtils.createException("carpet.commands.locations.another.parse");
        } catch (IOException e) {
            CarpetOrgAddition.LOGGER.error("无法解析路径点[" + name + "]:", e);
            throw CommandUtils.createException("carpet.commands.locations.another.io", name);
        }
        return 1;
    }

    //显示详细信息
    private static int showInfo(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = CommandUtils.getSourcePlayer(context);
        ServerCommandSource source = context.getSource();
        String name = StringArgumentType.getString(context, "info");
        try {
            //从本地文件读取路径点对象
            Location location = Location.loadLoc(getFile(player.getWorld()), name + ".json");
            //显示路径点对象的详细信息
            location.showInfo(source, player, name);
        } catch (JsonParseException e) {
            //无法解析坐标
            throw CommandUtils.createException("carpet.commands.locations.info.parse");
        } catch (IOException e) {
            //无法显示路径点的详细信息
            CarpetOrgAddition.LOGGER.error("无法显示路径点详细信息:", e);
            throw CommandUtils.createException("carpet.commands.locations.info.io");
        }
        return 1;
    }

    //删除路径点
    private static int deleteWayPoint(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = CommandUtils.getSourcePlayer(context);
        ServerCommandSource source = context.getSource();
        //获取路径点文件名
        String delete = StringArgumentType.getString(context, "delete");
        //获取路径点文件对象
        File file = new File(getFile(player.getWorld()), delete + ".json");
        //从本地文件删除路径点
        // 是否删除成功
        boolean successRemove = file.delete();
        if (successRemove) {
            //成功删除
            MessageUtils.sendCommandFeedback(source, "carpet.commands.locations.remove.success", delete);
        } else {
            //删除失败
            throw CommandUtils.createException("carpet.commands.locations.remove.fail", delete);
        }
        return 1;
    }

    //修改路径点
    private static int setWayPoint(CommandContext<ServerCommandSource> context, @Nullable BlockPos blockPos) throws CommandSyntaxException {
        ServerPlayerEntity player = CommandUtils.getSourcePlayer(context);
        ServerCommandSource source = context.getSource();
        if (blockPos == null) {
            blockPos = player.getBlockPos();
        }
        String fileName = StringArgumentType.getString(context, "name");
        try {
            //从本地文件获取路径点对象
            Location location = Location.loadLoc(getFile(player.getWorld()), fileName);
            //修改路径点的坐标
            location.setWayPoint(blockPos);
            //将路径点写入本地文件
            Location.saveLoc(getFile(player.getWorld()), location, fileName);
            //发送命令执行后的反馈
            MessageUtils.sendCommandFeedback(source, "carpet.commands.locations.set", fileName);
        } catch (JsonParseException e) {
            throw CommandUtils.createException("carpet.commands.locations.set.parse", fileName);
        } catch (IOException e) {
            throw CommandUtils.createException("carpet.commands.locations.set.io", fileName);
        }
        return 1;
    }

    //获取文件
    private static File getFile(World world) {
        return Objects.requireNonNull(world.getServer())
                .getSavePath(WorldSavePath.ROOT).resolve("locations").toFile();
    }

    //删除扩展名
    private static String removeExtension(String fileName) {
        if (fileName.endsWith(".json")) {
            return fileName.substring(0, fileName.lastIndexOf("."));
        }
        return fileName;
    }
}
