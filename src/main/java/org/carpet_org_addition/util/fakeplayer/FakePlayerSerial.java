package org.carpet_org_addition.util.fakeplayer;

import carpet.fakes.ServerPlayerInterface;
import carpet.patches.EntityPlayerMPFake;
import com.google.gson.JsonObject;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.carpet_org_addition.CarpetOrgAddition;
import org.carpet_org_addition.util.*;
import org.carpet_org_addition.util.constant.CommandSyntaxExceptionConstants;
import org.carpet_org_addition.util.constant.TextConstants;
import org.carpet_org_addition.util.helpers.JsonSerial;
import org.carpet_org_addition.util.helpers.WorldFormat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FakePlayerSerial implements JsonSerial {
    public static final String PLAYER_DATA = "player_data";
    private final EntityPlayerMPFake fakePlayer;
    private String annotation;
    private final Vec3d playerPos;
    private final float yaw;
    private final float pitch;
    private final String dimension;
    private final GameMode gameMode;
    private final boolean flying;
    private final boolean sneaking;
    private final EntityPlayerActionPackSerial actionPack;
    private final FakePlayerActionManager actionManager;

    public FakePlayerSerial(EntityPlayerMPFake fakePlayer) {
        this.fakePlayer = fakePlayer;
        this.playerPos = fakePlayer.getPos();
        this.yaw = fakePlayer.getYaw();
        this.pitch = fakePlayer.getPitch();
        this.dimension = WorldUtils.getDimensionId(fakePlayer.getWorld());
        this.gameMode = fakePlayer.interactionManager.getGameMode();
        this.flying = fakePlayer.getAbilities().flying;
        this.sneaking = fakePlayer.isSneaking();
        this.actionPack = new EntityPlayerActionPackSerial(((ServerPlayerInterface) fakePlayer).getActionPack());
        this.actionManager = FakePlayerActionInterface.getManager(fakePlayer);
    }

    public FakePlayerSerial(EntityPlayerMPFake fakePlayer, String annotation) {
        this(fakePlayer);
        this.annotation = annotation;
    }

    /**
     * 将当前对象保存到本地文件
     *
     * @return 是否为重新保存
     */
    public boolean save(MinecraftServer server, boolean resave) throws IOException, CommandSyntaxException {
        WorldFormat worldFormat = new WorldFormat(server, PLAYER_DATA);
        File file = worldFormat.createFileObject(fakePlayer.getName().getString());
        boolean exists = file.exists();
        if (exists && !resave) {
            throw CommandSyntaxExceptionConstants.JSON_FILE_ALREADY_EXIST_EXCEPTION;
        }
        WorldFormat.saveJson(file, WorldFormat.GSON, this.toJson());
        return exists;
    }

    // 从json加载并生成假玩家
    public static void spawn(String playerName, MinecraftServer server, JsonObject json) throws CommandSyntaxException {
        if (server.getPlayerManager().getPlayer(playerName) != null) {
            throw CommandUtils.createException("carpet.commands.playerManager.spawn.player_exist");
        }
        // 玩家位置
        JsonObject pos = json.get("pos").getAsJsonObject();
        Vec3d vec3d = new Vec3d(pos.get("x").getAsDouble(), pos.get("y").getAsDouble(), pos.get("z").getAsDouble());
        // 获取朝向
        JsonObject direction = json.get("direction").getAsJsonObject();
        float yaw = direction.get("yaw").getAsFloat();
        float pitch = direction.get("pitch").getAsFloat();
        // 维度
        String dimension = json.get("dimension").getAsString();
        // 游戏模式
        GameMode gamemode = GameMode.byName(json.get("gamemode").getAsString());
        // 是否飞行
        boolean flying = json.get("flying").getAsBoolean();
        // 是否潜行
        boolean sneaking = json.get("sneaking").getAsBoolean();
        EntityPlayerMPFake fakePlayer = EntityPlayerMPFake.createFake(playerName, server, vec3d, yaw, pitch,
                WorldUtils.getWorld(dimension), gamemode, flying);
        fakePlayer.setSneaking(sneaking);
        if (json.has("hand_action")) {
            EntityPlayerActionPackSerial.startAction(fakePlayer, json.get("hand_action").getAsJsonObject());
        }
        if (json.has("script_action")) {
            FakePlayerActionManager.load(fakePlayer, json.get("script_action").getAsJsonObject());
        }
    }

    // 列出每一条玩家信息
    public static int list(CommandContext<ServerCommandSource> context, WorldFormat worldFormat) {
        MutableText online = TextUtils.getTranslate("carpet.commands.playerManager.click.online");
        MutableText offline = TextUtils.getTranslate("carpet.commands.playerManager.click.offline");
        int count = 0;
        for (File file : worldFormat.listFiles()) {
            try {
                JsonObject json = WorldFormat.loadJson(file);
                ArrayList<MutableText> list = info(json);
                boolean hasAnnotation = json.has("annotation");
                if (hasAnnotation) {
                    // 添加注释
                    list.add(TextUtils.getTranslate("carpet.commands.playerManager.info.annotation", json.get("annotation").getAsString()));
                }
                MutableText info = TextUtils.createText("");
                for (int i = 0; i < list.size(); i++) {
                    info.append(list.get(i));
                    if (i < list.size() - 1) {
                        info.append("\n");
                    }
                }
                String playerName = WorldFormat.removeExtension(file.getName());
                String onlineCommand = "/playerManager spawn " + playerName;
                String offlineCommand = "/player " + playerName + " kill";
                MutableText mutableText = TextUtils.appendAll(
                        TextUtils.command(TextUtils.createText("[↑]"), onlineCommand, online, Formatting.GREEN, false), " ",
                        TextUtils.command(TextUtils.createText("[↓]"), offlineCommand, offline, Formatting.RED, false), " ",
                        TextUtils.hoverText(TextUtils.createText("[?]"), info, Formatting.GRAY), " ",
                        hasAnnotation ? TextUtils.hoverText(playerName, json.get("annotation").getAsString()) : playerName);
                // 发送消息
                MessageUtils.sendCommandFeedback(context.getSource(), mutableText);
                count++;
            } catch (IOException e) {
                CarpetOrgAddition.LOGGER.warn("无法从文件" + file.getName() + "加载玩家信息");
            }
        }
        return count;
    }

    // 显示json信息
    private static ArrayList<MutableText> info(JsonObject json) {
        ArrayList<MutableText> list = new ArrayList<>();
        // 玩家位置
        JsonObject pos = json.get("pos").getAsJsonObject();
        list.add(TextUtils.getTranslate("carpet.commands.playerManager.info.pos",
                MathUtils.keepTwoDecimalPlaces(pos.get("x").getAsDouble(),
                        pos.get("y").getAsDouble(), pos.get("z").getAsDouble())));
        // 获取朝向
        JsonObject direction = json.get("direction").getAsJsonObject();
        float yaw = direction.get("yaw").getAsFloat();
        float pitch = direction.get("pitch").getAsFloat();
        list.add(TextUtils.getTranslate("carpet.commands.playerManager.info.direction",
                MathUtils.keepTwoDecimalPlaces(yaw), MathUtils.keepTwoDecimalPlaces(pitch)));
        // 维度
        String dimension = json.get("dimension").getAsString();
        list.add(TextUtils.getTranslate("carpet.commands.playerManager.info.dimension", switch (dimension) {
            case "minecraft:overworld", "overworld" -> TextConstants.OVERWORLD;
            case "minecraft:the_nether", "the_nether" -> TextConstants.THE_NETHER;
            case "minecraft:the_end", "the_end" -> TextConstants.THE_END;
            default -> TextUtils.createText(dimension);
        }));
        // 游戏模式
        GameMode gamemode = GameMode.byName(json.get("gamemode").getAsString());
        list.add(TextUtils.getTranslate("carpet.commands.playerManager.info.gamemode", gamemode.getTranslatableName()));
        // 是否飞行
        boolean flying = json.get("flying").getAsBoolean();
        list.add(TextUtils.getTranslate("carpet.commands.playerManager.info.flying", TextConstants.getBoolean(flying)));
        // 是否潜行
        boolean sneaking = json.get("sneaking").getAsBoolean();
        list.add(TextUtils.getTranslate("carpet.commands.playerManager.info.sneaking", TextConstants.getBoolean(sneaking)));
        return list;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        // 玩家位置
        JsonObject pos = new JsonObject();
        pos.addProperty("x", this.playerPos.x);
        pos.addProperty("y", this.playerPos.y);
        pos.addProperty("z", this.playerPos.z);
        json.add("pos", pos);
        // 玩家朝向
        JsonObject direction = new JsonObject();
        direction.addProperty("yaw", this.yaw);
        direction.addProperty("pitch", this.pitch);
        json.add("direction", direction);
        // 维度
        json.addProperty("dimension", this.dimension);
        // 游戏模式
        json.addProperty("gamemode", this.gameMode.getName());
        // 是否飞行
        json.addProperty("flying", this.flying);
        // 是否潜行
        json.addProperty("sneaking", this.sneaking);
        // 注释
        if (this.annotation != null) {
            json.addProperty("annotation", this.annotation);
        }
        // 添加左键右键动作
        json.add("hand_action", actionPack.toJson());
        // 添加玩家动作
        json.add("script_action", this.actionManager.toJson());
        return json;
    }
}
