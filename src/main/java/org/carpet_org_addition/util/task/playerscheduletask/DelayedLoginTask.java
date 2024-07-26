package org.carpet_org_addition.util.task.playerscheduletask;

import com.google.gson.JsonObject;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import org.carpet_org_addition.CarpetOrgAddition;
import org.carpet_org_addition.util.GameUtils;
import org.carpet_org_addition.util.MessageUtils;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.fakeplayer.FakePlayerSerial;
import org.jetbrains.annotations.NotNull;

public class DelayedLoginTask extends PlayerScheduleTask {
    private final MinecraftServer server;
    private final String name;
    private final JsonObject jsonObject;
    private long delayed;

    public DelayedLoginTask(MinecraftServer server, String name, JsonObject jsonObject, long delayed) {
        this.server = server;
        this.name = name;
        this.jsonObject = jsonObject;
        this.delayed = delayed;
    }

    @Override
    public void tick() {
        if (this.delayed == 0L) {
            try {
                // 生成假玩家
                FakePlayerSerial.spawn(this.name, this.server, jsonObject);
            } catch (CommandSyntaxException | NullPointerException e) {
                CarpetOrgAddition.LOGGER.error("玩家{}未能在指定时间上线", this.name, e);
            } finally {
                // 将此任务设为已执行结束
                this.delayed = -1L;
            }
        } else {
            this.delayed--;
        }
    }

    @Override
    public String getPlayerName() {
        return name;
    }

    @Override
    public void onCancel(CommandContext<ServerCommandSource> context) {
        MutableText time = getDisplayTime();
        MutableText displayName = getDisplayName();
        MessageUtils.sendCommandFeedback(context, "carpet.commands.playerManager.schedule.login.cancel", displayName, time);
    }

    // 获取带有悬停提示的时间
    private @NotNull MutableText getDisplayTime() {
        return TextUtils.hoverText(GameUtils.tickToTime(this.delayed), GameUtils.tickToRealTime(this.delayed));
    }

    // 获取带有悬停提示的显示名称
    public MutableText getDisplayName() {
        MutableText info = FakePlayerSerial.info(this.jsonObject);
        return TextUtils.hoverText(this.name, info);
    }

    @Override
    public void sendEachMessage(ServerCommandSource source) {
        MessageUtils.sendCommandFeedback(source, "carpet.commands.playerManager.schedule.login",
                this.getDisplayName(), this.getDisplayTime());
    }

    public void setDelayed(long delayed) {
        this.delayed = delayed;
    }

    public JsonObject getJsonObject() {
        return jsonObject;
    }

    @Override
    public boolean stopped() {
        return this.delayed < 0L;
    }

    @Override
    public String toString() {
        return this.name + "延迟上线";
    }
}
