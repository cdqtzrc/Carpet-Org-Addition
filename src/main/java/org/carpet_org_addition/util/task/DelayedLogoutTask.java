package org.carpet_org_addition.util.task;

import carpet.patches.EntityPlayerMPFake;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class DelayedLogoutTask extends ServerTask {
    private final MinecraftServer server;
    private final EntityPlayerMPFake fakePlayer;
    private long delayed;

    public DelayedLogoutTask(MinecraftServer server, EntityPlayerMPFake fakePlayer, long delayed) {
        this.server = server;
        this.fakePlayer = fakePlayer;
        this.delayed = delayed;
    }

    @Override
    public void tick() {
        if (this.delayed == 0L) {
            if (this.fakePlayer.isRemoved()) {
                // 假玩家可能被真玩家顶替，或者假玩家穿过了末地返回传送门，或者假玩家退出游戏后重新上线
                ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(this.fakePlayer.getUuid());
                if (player instanceof EntityPlayerMPFake) {
                    player.kill();
                }
            } else {
                this.fakePlayer.kill();
            }
            this.delayed = -1L;
        } else {
            this.delayed--;
        }
    }

    public void setDelayed(long delayed) {
        this.delayed = delayed;
    }

    public EntityPlayerMPFake getFakePlayer() {
        return fakePlayer;
    }

    @Override
    public boolean isEndOfExecution() {
        return this.delayed < 0L;
    }
}
