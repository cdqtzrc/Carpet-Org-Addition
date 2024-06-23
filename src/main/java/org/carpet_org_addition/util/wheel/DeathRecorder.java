package org.carpet_org_addition.util.wheel;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import org.carpet_org_addition.util.WorldUtils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SuppressWarnings("unused")
public class DeathRecorder {
    public static final String DEATH_LOG = "death_log";
    public static final String NBT = ".nbt";
    private final ServerPlayerEntity player;
    private final String playerName;
    private final String deathMessage;
    private final String deathTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    private final int[] deathPos;
    private final String deathDimension;

    public DeathRecorder(String deathMessage, ServerPlayerEntity player) {
        this.player = player;
        this.playerName = player.getName().getString();
        this.deathMessage = deathMessage;
        BlockPos blockPos = player.getBlockPos();
        this.deathPos = new int[]{blockPos.getX(), blockPos.getY(), blockPos.getZ()};
        this.deathDimension = WorldUtils.getDimensionId(player.getWorld());
    }

    // 将死亡信息NBT写入本地文件
    public void write() throws IOException {
        NbtCompound nbt = new NbtCompound();
        // 死亡消息
        nbt.putString("DeathMessage", this.deathMessage);
        // 死亡玩家的名字
        nbt.putString("PlayerName", this.playerName);
        // 死亡时间
        nbt.putString("DeathTime", this.deathTime);
        // 死亡时的位置
        nbt.putIntArray("DeathPos", this.deathPos);
        // 死亡时的维度
        nbt.putString("DeathDimension", this.deathDimension);
        // 死亡时玩家的物品栏
        nbt.put("Inventory", this.player.getInventory().writeNbt(new NbtList()));
        // 死亡时玩家的经验等级
        nbt.putInt("XpLevel", this.player.experienceLevel);
        WorldFormat worldFormat = new WorldFormat(player.server, DEATH_LOG, this.playerName);
        // 死亡信息是在统计信息更新之前记录的，所以这里获取的死亡次数需要+1
        File file = worldFormat.file((getDeathCount(this.player) + 1) + ".nbt");
        // 将NBT写入本地文件
        NbtIo.write(nbt, file.toPath());
    }

    // 从本地文件加载死亡信息
    public static NbtCompound load(ServerPlayerEntity player, int number) throws IOException {
        String playerName = player.getName().getString();
        WorldFormat worldFormat = new WorldFormat(player.server, DEATH_LOG, playerName);
        File file = worldFormat.file(number + NBT);
        return NbtIo.read(file.toPath());
    }

    // 获取死亡次数
    public static int getDeathCount(ServerPlayerEntity player) {
        return player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.DEATHS));
    }
}
