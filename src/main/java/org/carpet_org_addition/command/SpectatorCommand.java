package org.carpet_org_addition.command;

import carpet.utils.CommandHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import org.carpet_org_addition.CarpetOrgAddition;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.util.CommandUtils;
import org.carpet_org_addition.util.GameUtils;
import org.carpet_org_addition.util.MathUtils;
import org.carpet_org_addition.util.WorldUtils;
import org.carpet_org_addition.util.helpers.ModLevelFormat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

// 在生存模式和旁观模式间切换
public class SpectatorCommand {
    private static final String SPECTATOR = "spectator";
    private static final String JSON_EXTENSION = ".json";

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("spectator")
                .requires(source -> CommandHelper.canUseCommand(source, CarpetOrgAdditionSettings.commandSpectator))
                .executes(context -> setGameMode(context, false))
                .then(CommandManager.argument(CommandUtils.PLAYER, EntityArgumentType.player())
                        .executes(context -> setGameMode(context, true))));
    }

    // 更改游戏模式
    private static int setGameMode(CommandContext<ServerCommandSource> context, boolean isFakePlayer) throws CommandSyntaxException {
        ServerPlayerEntity player = isFakePlayer
                ? CommandUtils.getArgumentFakePlayer(context)
                : CommandUtils.getSourcePlayer(context);
        // 如果玩家当前是旁观模式，就切换到生存模式，否则切换到旁观模式
        GameMode gameMode;
        if (player.isSpectator()) {
            gameMode = GameMode.SURVIVAL;
            loadPlayerPos(player.getServer(), player);
        } else {
            gameMode = GameMode.SPECTATOR;
            savePlayerPos(player.getServer(), player);
        }
        player.changeGameMode(gameMode);
        // 发送命令反馈
        MutableText text = Text.translatable("gameMode." + gameMode.getName());
        player.sendMessage(Text.translatable("commands.gamemode.success.self", text), true);
        return gameMode == GameMode.SURVIVAL ? 1 : 0;
    }

    private static void savePlayerPos(MinecraftServer server, ServerPlayerEntity player) {
        ModLevelFormat levelFormat = new ModLevelFormat(server, SPECTATOR);
        JsonObject json = new JsonObject();
        json.addProperty("x", MathUtils.keepTwoDecimalPlaces(player.getX()));
        json.addProperty("y", MathUtils.keepTwoDecimalPlaces(player.getY()));
        json.addProperty("z", MathUtils.keepTwoDecimalPlaces(player.getZ()));
        json.addProperty("yaw", MathUtils.keepTwoDecimalPlaces(player.getYaw()));
        json.addProperty("pitch", MathUtils.keepTwoDecimalPlaces(player.getPitch()));
        json.addProperty("dimension", WorldUtils.getDimensionId(player.getWorld()));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString = gson.toJson(json, JsonObject.class);
        File file = levelFormat.createModFile(player.getUuidAsString() + JSON_EXTENSION);
        try {
            try (BufferedWriter writer = ModLevelFormat.toWriter(file)) {
                writer.write(jsonString);
            }
        } catch (IOException e) {
            CarpetOrgAddition.LOGGER.warn("无法正常" + GameUtils.getPlayerName(player) + "的位置信息写入文件", e);
        }
    }

    private static void loadPlayerPos(MinecraftServer server, ServerPlayerEntity player) {
        ModLevelFormat levelFormat = new ModLevelFormat(server, SPECTATOR);
        File file = levelFormat.createModFile(player.getUuidAsString() + JSON_EXTENSION);
        try {
            BufferedReader reader = ModLevelFormat.toReader(file);
            try (reader) {
                Gson gson = new Gson();
                JsonObject json = gson.fromJson(reader, JsonObject.class);
                double x = json.get("x").getAsDouble();
                double y = json.get("y").getAsDouble();
                double z = json.get("z").getAsDouble();
                float yaw = json.get("yaw").getAsFloat();
                float pitch = json.get("pitch").getAsFloat();
                String dimension = json.get("dimension").getAsString();
                ServerWorld world = WorldUtils.getWorld(server, dimension);
                player.teleport(world, x, y, z, yaw, pitch);
            }
        } catch (IOException e) {
            CarpetOrgAddition.LOGGER.warn("无法正常读取" + GameUtils.getPlayerName(player) + "的位置信息");
        }
    }
}
