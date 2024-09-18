package org.carpet_org_addition.util.express;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.carpet_org_addition.CarpetOrgAddition;
import org.carpet_org_addition.util.CommandUtils;
import org.carpet_org_addition.util.MessageUtils;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.WorldUtils;
import org.carpet_org_addition.util.constant.TextConstants;
import org.carpet_org_addition.util.wheel.WorldFormat;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 快递
 */
public class Express implements Comparable<Express> {
    /**
     * 寄件人
     */
    private final String sender;
    /**
     * 收件人
     */
    private final String recipient;
    /**
     * 快递的内容
     */
    private final ItemStack express;
    /**
     * 快递是否已被撤回
     */
    private boolean cancel = false;
    /**
     * 快递单号
     */
    private final int id;
    MinecraftServer server;
    private final LocalDateTime time;
    private final WorldFormat worldFormat;
    public static final String EXPRESS = "express";

    public Express(MinecraftServer server, ServerPlayerEntity sender, ServerPlayerEntity recipient, int id) throws CommandSyntaxException {
        this.server = server;
        this.sender = sender.getName().getString();
        this.recipient = recipient.getName().getString();
        ItemStack mainHandStack = sender.getMainHandStack();
        if (mainHandStack.isEmpty()) {
            ItemStack offHandStack = sender.getOffHandStack();
            if (offHandStack.isEmpty()) {
                throw CommandUtils.createException("carpet.commands.mail.structure");
            }
            this.express = offHandStack.copyAndEmpty();
        } else {
            this.express = mainHandStack.copyAndEmpty();
        }
        this.id = id;
        this.time = LocalDateTime.now();
        this.worldFormat = new WorldFormat(server, EXPRESS);
    }

    public Express(MinecraftServer server, ServerPlayerEntity sender, ServerPlayerEntity recipient, ItemStack itemStack, int id) {
        this.server = server;
        this.sender = sender.getName().getString();
        this.recipient = recipient.getName().getString();
        this.express = itemStack;
        this.id = id;
        this.time = LocalDateTime.now();
        this.worldFormat = new WorldFormat(server, EXPRESS);
    }

    private Express(MinecraftServer server, String sender, String recipient, ItemStack express, int id, LocalDateTime time) {
        this.server = server;
        this.sender = sender;
        this.recipient = recipient;
        this.express = express;
        this.id = id;
        this.time = time;
        worldFormat = new WorldFormat(server, EXPRESS);
    }

    /**
     * 发送快递
     */
    public void sending() {
        PlayerManager playerManager = this.server.getPlayerManager();
        ServerPlayerEntity senderPlayer = playerManager.getPlayer(this.sender);
        ServerPlayerEntity recipientPlayer = playerManager.getPlayer(this.recipient);
        if (senderPlayer == null) {
            CarpetOrgAddition.LOGGER.error("快递由不存在的玩家发出");
            return;
        }
        if (recipientPlayer == null) {
            CarpetOrgAddition.LOGGER.error("向不存在的玩家发送快递");
            return;
        }
        // 向快递发送者发送发出快递的消息
        MutableText cancelText = TextConstants.clickRun("/mail cancel " + this.getId());
        Object[] senderArray = {recipientPlayer.getDisplayName(), this.express.getCount(), this.express.toHoverableText(), cancelText};
        MessageUtils.sendTextMessage(senderPlayer, TextUtils.getTranslate("carpet.commands.mail.sending.sender", senderArray));
        // 向快递接受者发送发出快递的消息
        MutableText receiveText = TextConstants.clickRun("/mail receive " + this.getId());
        Object[] recipientArray = {senderPlayer.getDisplayName(), this.express.getCount(), this.express.toHoverableText(), receiveText};
        MessageUtils.sendTextMessage(recipientPlayer, TextUtils.getTranslate("carpet.commands.mail.sending.recipient", recipientArray));
        // 在接受者位置播放音效
        WorldUtils.playSound(recipientPlayer.getWorld(), recipientPlayer.getBlockPos(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS);
    }

    /**
     * 接收快递
     */
    public void receive() throws IOException {
        ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(this.recipient);
        if (player == null) {
            CarpetOrgAddition.LOGGER.error("接收快递的玩家不存在");
            return;
        }
        if (this.cancel) {
            // 快递已被撤回
            MessageUtils.sendTextMessage(player, TextUtils.getTranslate("carpet.commands.mail.receive.cancel"));
            return;
        }
        int count = this.express.getCount();
        Text text = this.express.toHoverableText();
        player.getInventory().insertStack(this.express);
        // 将快递内容放入物品栏
        if (this.express.getCount() == count) {
            // 物品未能成功放入物品栏
            MessageUtils.sendTextMessage(player, TextUtils.getTranslate("carpet.commands.mail.receive.insufficient_capacity"));
        } else {
            if (this.express.isEmpty()) {
                // 物品完全放入物品栏
                MessageUtils.sendTextMessage(player, TextUtils.getTranslate("carpet.commands.mail.receive.success", count, text));
                // 删除文件
                this.delete();
            } else {
                // 剩余的物品数量
                int surplusCount = this.express.getCount();
                // 物品部分放入物品栏
                MessageUtils.sendTextMessage(player, TextUtils.getTranslate("carpet.commands.mail.receive.partial_reception",
                        count - surplusCount, surplusCount));
                // 重新保存文件
                this.save();
            }
            // 播放物品拾取音效
            WorldUtils.playSound(player, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS);
        }
    }

    /**
     * 撤回快递
     */
    public void cancel() throws IOException {
        PlayerManager playerManager = this.server.getPlayerManager();
        ServerPlayerEntity player = playerManager.getPlayer(this.sender);
        if (player == null) {
            CarpetOrgAddition.LOGGER.error("撤回快递的玩家不存在");
            return;
        }
        int count = this.express.getCount();
        Text text = this.express.toHoverableText();
        player.getInventory().insertStack(this.express);
        // 将快递内容放入物品栏
        if (this.express.getCount() == count) {
            // 物品未能成功放入物品栏
            MessageUtils.sendTextMessage(player, TextUtils.getTranslate("carpet.commands.mail.cancel.insufficient_capacity"));
        } else {
            // 物品完全放入物品栏
            if (this.express.isEmpty()) {
                MessageUtils.sendTextMessage(player, TextUtils.getTranslate("carpet.commands.mail.cancel.success", count, text));
                this.delete();
            } else {
                // 剩余的物品数量
                int surplusCount = this.express.getCount();
                // 物品部分放入物品栏
                MessageUtils.sendTextMessage(player, TextUtils.getTranslate("carpet.commands.mail.cancel.partial_reception",
                        count - surplusCount, surplusCount));
                this.save();
            }
            // 播放物品拾取音效
            WorldUtils.playSound(player, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS);
        }
        ServerPlayerEntity recipientPlayer = playerManager.getPlayer(this.recipient);
        if (recipientPlayer != null) {
            // 如果接收者在线，通知接收者快递已经撤回
            MessageUtils.sendTextMessage(recipientPlayer, TextUtils.getTranslate("carpet.commands.mail.cancel.notice", player.getDisplayName()));
        }
        this.cancel = true;
    }

    /**
     * 将快递信息保存到本地文件
     */
    public void save() throws IOException {
        NbtIo.write(this.writeNbt(), this.worldFormat.file(this.getId() + ".nbt"));
    }

    /**
     * 删除已经完成的快递
     */
    public void delete() {
        File file = this.worldFormat.getFile(this.getId() + ".nbt");
        if (file.delete()) {
            return;
        }
        CarpetOrgAddition.LOGGER.warn("未能成功删除名为{}的文件", file);
    }

    /**
     * 完成寄件
     */
    public boolean complete() {
        return this.express.isEmpty();
    }

    /**
     * 将快递内容写入NBT
     */
    public NbtCompound writeNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("sender", this.sender);
        nbt.putString("recipient", this.recipient);
        nbt.putBoolean("cancel", this.cancel);
        nbt.putInt("id", this.id);
        int[] args = {time.getYear(), time.getMonthValue(), time.getDayOfMonth(), time.getHour(), time.getMinute(), time.getSecond()};
        nbt.putIntArray("time", args);
        nbt.put("item", this.express.writeNbt(new NbtCompound()));
        return nbt;
    }

    /**
     * 从NBT读取快递信息
     */
    public static Express readNbt(MinecraftServer server, NbtCompound nbt) {
        String sender = nbt.getString("sender");
        String recipient = nbt.getString("recipient");
        boolean cancel = nbt.getBoolean("cancel");
        ItemStack stack = ItemStack.fromNbt(nbt.getCompound("item"));
        int id = nbt.getInt("id");
        int[] times = nbt.getIntArray("time");
        LocalDateTime localDateTime = LocalDateTime.of(times[0], times[1], times[2], times[3], times[4], times[5]);
        Express express = new Express(server, sender, recipient, stack, id, localDateTime);
        express.cancel = cancel;
        return express;
    }

    /**
     * @return 快递单号
     */
    public int getId() {
        return this.id;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public MutableText getTime() {
        return TextUtils.getTranslate("carpet.command.time.format",
                this.time.getYear(),
                this.time.getMonthValue(),
                this.time.getDayOfMonth(),
                this.time.getHour(),
                this.time.getMinute(),
                this.time.getSecond());
    }

    public ItemStack getExpress() {
        return express;
    }

    /**
     * @return 指定玩家是否是当前快递的发送者
     */
    public boolean isSender(ServerPlayerEntity player) {
        return Objects.equals(this.sender, player.getName().getString());
    }

    /**
     * @return 指定玩家是否是当前快递的接收者
     */
    public boolean isRecipient(ServerPlayerEntity player) {
        return Objects.equals(this.recipient, player.getName().getString());
    }

    @Override
    public boolean equals(Object obj) {
        if (this.getClass() == obj.getClass()) {
            return this.id == ((Express) obj).id;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    @Override
    public int compareTo(@NotNull Express o) {
        return this.id - o.id;
    }
}
