package org.carpet_org_addition.util.task;

import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.MinecraftServer;
import org.carpet_org_addition.CarpetOrgAddition;
import org.carpet_org_addition.util.fakeplayer.FakePlayerSerial;
import org.carpet_org_addition.util.wheel.WorldFormat;

import java.io.IOException;

public class DelayedLoginTask extends ServerTask {

    private final MinecraftServer server;
    private final String name;
    private long delayed;

    public DelayedLoginTask(MinecraftServer server, String name, long delayed) {
        this.server = server;
        this.name = name;
        this.delayed = delayed;
    }

    @Override
    public void tick() {
        if (this.delayed == 0) {
            WorldFormat worldFormat = new WorldFormat(this.server, FakePlayerSerial.PLAYER_DATA);
            try {
                // 生成假玩家
                JsonObject jsonObject = WorldFormat.loadJson(worldFormat.getFile(this.name));
                FakePlayerSerial.spawn(this.name, this.server, jsonObject);
            } catch (CommandSyntaxException | IOException | NullPointerException e) {
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
    public boolean isEndOfExecution() {
        return this.delayed < 0;
    }
}
