package org.carpet_org_addition.util.helpers;

import com.google.gson.Gson;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionTypes;
import org.carpet_org_addition.util.GameUtils;
import org.carpet_org_addition.util.MathUtils;
import org.carpet_org_addition.util.MessageUtils;
import org.carpet_org_addition.util.TextUtils;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Location {
    // TODO 重构
    //当前坐标的类型
    private LocationType locType = LocationType.OVERWORLD;
    //坐标创建者
    private final String creatorPlayerName;
    //创建时间
    private final String creatorTime;
    //说明
    private String illustrate = null;
    //主世界坐标
    private int overworld_x;
    private int overworld_y;
    private int overworld_z;
    //下界坐标
    private int the_nether_x;
    private int the_nether_y;
    private int the_nether_z;
    //末地坐标
    private int the_end_x;
    private int the_end_y;
    private int the_end_z;

    public Location(BlockPos blockPos, String dimensionId, ServerPlayerEntity player) {
        this(blockPos.getX(), blockPos.getY(), blockPos.getZ(), dimensionId, player);
    }

    public Location(int posX, int posY, int posZ, String dimensionId, ServerPlayerEntity player) {
        switch (dimensionId) {
            case "minecraft:overworld" -> setOverworldPos(posX, posY, posZ);
            case "minecraft:the_nether" -> setTheNetherPos(posX, posY, posZ);
            case "minecraft:the_end" -> setTheEndPos(posX, posY, posZ);
            default -> throw new IllegalArgumentException();
        }
        this.creatorPlayerName = player.getName().getString();
        this.creatorTime = GameUtils.getDateString();
    }

    //设置主世界坐标
    public void setOverworldPos(BlockPos blockPos) {
        this.setOverworldPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public void setOverworldPos(int overworldPosX, int overworldPosY, int overworldPosZ) {
        this.overworld_x = overworldPosX;
        this.overworld_y = overworldPosY;
        this.overworld_z = overworldPosZ;
        this.locType = LocationType.OVERWORLD;
    }

    //设置下界坐标
    public void setTheNetherPos(BlockPos blockPos) {
        this.setTheNetherPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public void setTheNetherPos(int theNetherPosX, int theNetherPosY, int theNetherPosZ) {
        this.the_nether_x = theNetherPosX;
        this.the_nether_y = theNetherPosY;
        this.the_nether_z = theNetherPosZ;
        this.locType = LocationType.THE_NETHER;
    }

    //设置末地坐标
    public void setTheEndPos(BlockPos blockPos) {
        this.setTheEndPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public void setTheEndPos(int theEndPosX, int theEndPosY, int theEndPosZ) {
        this.the_end_x = theEndPosX;
        this.the_end_y = theEndPosY;
        this.the_end_z = theEndPosZ;
        this.locType = LocationType.THE_END;
    }

    //获取维度坐标
    public BlockPos getOverworldPos() {
        return new BlockPos(overworld_x, overworld_y, overworld_z);
    }

    public BlockPos getTheNetherPos() {
        return new BlockPos(the_nether_x, the_nether_y, the_nether_z);
    }

    public BlockPos getTheEndPos() {
        return new BlockPos(the_end_x, the_end_y, the_end_z);
    }

    //将坐标写入本地文件
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void saveLoc(File file, Location location, String fileName) throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(location, Location.class);
        file.mkdirs();
        File newFile = new File(file, fileName.endsWith(".json") ? fileName : fileName + ".json");
        newFile.createNewFile();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(newFile, StandardCharsets.UTF_8))) {
            bw.write(json);
        }
    }

    //从本地文件中读取坐标
    public static Location loadLoc(File file, String fileName) throws IOException {
        if (!fileName.endsWith(".json")) {
            fileName = fileName + ".json";
        }
        StringBuilder sb;
        try (BufferedReader br = new BufferedReader(new FileReader(new File(file, fileName), StandardCharsets.UTF_8))) {
            sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        }
        Gson gson = new Gson();
        return gson.fromJson(sb.toString(), Location.class);
    }

    //获取路径点文本
    public MutableText getText(String name) {
        MutableText mutableText = null;
        switch (locType) {
            case OVERWORLD -> mutableText = TextUtils.getTranslate("carpet.commands.locations.text.overworld",
                    TextUtils.blockPos(getOverworldPos(), Formatting.GREEN));
            case OVERWORLD_AND_THE_NETHER ->
                    mutableText = TextUtils.getTranslate("carpet.commands.locations.text.overworld_and_the_nether",
                            TextUtils.blockPos(getOverworldPos(), Formatting.GREEN),
                            TextUtils.blockPos(getTheNetherPos(), Formatting.RED));
            case THE_NETHER -> mutableText = TextUtils.getTranslate("carpet.commands.locations.text.the_nether",
                    TextUtils.blockPos(getTheNetherPos(), Formatting.RED));
            case THE_NETHER_AND_OVERWORLD ->
                    mutableText = TextUtils.getTranslate("carpet.commands.locations.text.the_nether_and_overworld",
                            TextUtils.blockPos(getTheNetherPos(), Formatting.RED),
                            TextUtils.blockPos(getOverworldPos(), Formatting.GREEN));
            case THE_END -> mutableText = TextUtils.getTranslate("carpet.commands.locations.text.the_end",
                    TextUtils.blockPos(getTheEndPos(), Formatting.DARK_PURPLE));
        }
        if (illustrate != null) {
            mutableText = TextUtils.hoverText(name, illustrate).append(mutableText);
        } else {
            mutableText = Text.literal(name).append(mutableText);
        }
        return mutableText;
    }

    //添加对向坐标
    public void addAnotherPos(BlockPos blockPos) {
        switch (locType) {
            case OVERWORLD, OVERWORLD_AND_THE_NETHER -> {
                setTheNetherPos(blockPos);
                locType = LocationType.OVERWORLD_AND_THE_NETHER;
            }
            case THE_NETHER, THE_NETHER_AND_OVERWORLD -> {
                setOverworldPos(blockPos);
                locType = LocationType.THE_NETHER_AND_OVERWORLD;
            }
            default -> throw new UnsupportedOperationException();
        }
    }

    //显示详细信息
    public void showInfo(ServerCommandSource source, PlayerEntity player, String name) {
        MessageUtils.sendTextMessage(player, getText("[" + name + "]"));
        if (illustrate != null) {
            MessageUtils.sendCommandFeedback(source, "carpet.commands.locations.text.info.illustrate", illustrate);
        }
        MessageUtils.sendCommandFeedback(source, "carpet.commands.locations.text.info.creator_player_name", creatorPlayerName);
        MessageUtils.sendCommandFeedback(source, "carpet.commands.locations.text.info.creator_time", creatorTime);
        Identifier value = player.getWorld().getDimensionKey().getValue();
        if (value.equals(DimensionTypes.OVERWORLD_ID)
                && (locType == LocationType.OVERWORLD
                || locType == LocationType.OVERWORLD_AND_THE_NETHER
                || locType == LocationType.THE_NETHER_AND_OVERWORLD)) {
            MessageUtils.sendCommandFeedback(source, "carpet.commands.locations.text.info.distance"
                    , (int) MathUtils.getBlockDistance(player.getBlockPos(), getOverworldPos()));
        } else if (value.equals(DimensionTypes.THE_NETHER_ID)
                && (locType == LocationType.THE_NETHER
                || locType == LocationType.THE_NETHER_AND_OVERWORLD
                || locType == LocationType.OVERWORLD_AND_THE_NETHER)) {
            MessageUtils.sendCommandFeedback(source, "carpet.commands.locations.text.info.distance"
                    , (int) MathUtils.getBlockDistance(player.getBlockPos(), getTheNetherPos()));
        } else if (value.equals(DimensionTypes.THE_END_ID) && locType == LocationType.THE_END) {
            MessageUtils.sendCommandFeedback(source, "carpet.commands.locations.text.info.distance"
                    , (int) MathUtils.getBlockDistance(player.getBlockPos(), getTheEndPos()));
        }
    }

    //修改坐标
    public void setWayPoint(BlockPos blockPos) {
        switch (locType) {
            case OVERWORLD -> setOverworldPos(blockPos);
            case OVERWORLD_AND_THE_NETHER -> {
                setOverworldPos(blockPos);
                locType = LocationType.OVERWORLD_AND_THE_NETHER;
            }
            case THE_NETHER -> setTheNetherPos(blockPos);
            case THE_NETHER_AND_OVERWORLD -> {
                setTheNetherPos(blockPos);
                locType = LocationType.THE_NETHER_AND_OVERWORLD;
            }
            case THE_END -> setTheEndPos(blockPos);
        }
    }

    public void setIllustrate(@Nullable String illustrate) {
        this.illustrate = illustrate;
    }

    public enum LocationType {
        OVERWORLD,
        OVERWORLD_AND_THE_NETHER,
        THE_NETHER,
        THE_NETHER_AND_OVERWORLD,
        THE_END
    }
}
