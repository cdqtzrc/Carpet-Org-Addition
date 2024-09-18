package org.carpet_org_addition.util.express;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import org.carpet_org_addition.CarpetOrgAddition;
import org.carpet_org_addition.util.MessageUtils;
import org.carpet_org_addition.util.constant.TextConstants;
import org.carpet_org_addition.util.wheel.WorldFormat;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Stream;

/**
 * 快递管理器
 */
public class ExpressManager {
    private final TreeSet<Express> expresses = new TreeSet<>();
    private final WorldFormat worldFormat;

    public ExpressManager(MinecraftServer server) {
        this.worldFormat = new WorldFormat(server, "express");
        // 从文件读取快递信息
        for (File file : this.worldFormat.toImmutableFileList()) {
            NbtCompound nbt;
            try {
                nbt = NbtIo.read(file);
            } catch (IOException e) {
                CarpetOrgAddition.LOGGER.warn("从文件{}读取快递信息失败", file, e);
                continue;
            }
            if (nbt == null) {
                continue;
            }
            Express express = Express.readNbt(server, nbt);
            // 快递对象物品为空，删除对应的文件
            if (express.complete()) {
                express.delete();
                continue;
            }
            this.expresses.add(express);
        }
    }

    /**
     * 提示玩家接收快递
     */
    public void promptToReceive(ServerPlayerEntity player) {
        List<Express> list = this.expresses.stream().filter(express -> express.isRecipient(player)).toList();
        if (list.isEmpty()) {
            return;
        }
        ServerCommandSource source = player.getCommandSource();
        for (Express express : list) {
            MutableText clickRun = TextConstants.clickRun("/mail receive " + express.getId());
            ItemStack stack = express.getExpress();
            MessageUtils.sendCommandFeedback(source, "carpet.commands.mail.prompt_receive",
                    stack.getCount(), stack.toHoverableText(), clickRun);
        }
    }

    /**
     * 每个游戏刻删除已经寄件完成的快递
     */
    public void tick() {
        this.expresses.removeIf(Express::complete);
    }

    /**
     * 添加新快递
     */
    public void put(Express express) throws IOException {
        if (express.getExpress().isEmpty()) {
            CarpetOrgAddition.LOGGER.info("尝试发送一个空气物品，已忽略");
            return;
        }
        this.expresses.add(express);
        express.sending();
        // 将快递信息写入本地文件
        NbtIo.write(express.writeNbt(), this.worldFormat.file(express.getId() + ".nbt"));
    }

    public Stream<Express> stream() {
        return this.expresses.stream();
    }

    /**
     * 使用二分查找获取指定单号的快递
     *
     * @param id 要查找的快递单号
     */
    public Optional<Express> binarySearch(int id) {
        List<Express> list = this.expresses.stream().toList();
        int left = 0;
        int right = list.size() - 1;
        while (left <= right) {
            int mid = (left + right) >>> 1;
            int currentId = list.get(mid).getId();
            if (currentId < id) {
                left = mid + 1;
            } else if (currentId > id) {
                right = mid - 1;
            } else {
                return Optional.of(list.get(mid));
            }
        }
        return Optional.empty();
    }

    /**
     * 生成快递单号
     */
    public int generateNumber() {
        // 没有快递发出，快递单号为1
        if (this.expresses.isEmpty()) {
            return 1;
        }
        // 集合最后一个元素id等于集合长度，说明前面的单号都是连续的，新单号为集合长度+1
        if (this.expresses.last().getId() == this.expresses.size()) {
            return this.expresses.size() + 1;
        }
        // 遍历集合找到空缺的单号
        int number = 0;
        for (Express express : this.expresses) {
            number++;
            if (number == express.getId()) {
                continue;
            }
            return number;
        }
        throw new IllegalStateException();
    }
}
