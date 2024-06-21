package org.carpet_org_addition.util.task;

import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.MinecraftServer;
import org.carpet_org_addition.CarpetOrgAddition;
import org.carpet_org_addition.util.fakeplayer.FakePlayerSerial;

public class DelayedLoginTask extends ServerTask {

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

    public String getName() {
        return name;
    }

    public void setDelayed(long delayed) {
        this.delayed = delayed;
    }

    public JsonObject getJsonObject() {
        return jsonObject;
    }

    @Override
    public boolean isEndOfExecution() {
        return this.delayed < 0L;
    }
}
