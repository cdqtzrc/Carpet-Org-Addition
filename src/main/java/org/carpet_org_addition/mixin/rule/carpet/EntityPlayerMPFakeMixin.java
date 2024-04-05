package org.carpet_org_addition.mixin.rule.carpet;

import carpet.patches.EntityPlayerMPFake;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.carpet_org_addition.CarpetOrgAddition;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.util.MessageUtils;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.fakeplayer.FakePlayerActionInterface;
import org.carpet_org_addition.util.fakeplayer.FakePlayerActionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(EntityPlayerMPFake.class)
public abstract class EntityPlayerMPFakeMixin extends ServerPlayerEntity implements FakePlayerActionInterface {
    @Unique
    private final EntityPlayerMPFake thisPlayer = (EntityPlayerMPFake) (Object) this;

    @Unique
    private final FakePlayerActionManager actionManager = new FakePlayerActionManager(thisPlayer);

    //私有化构造方法，防止被创建对象
    private EntityPlayerMPFakeMixin(MinecraftServer server, ServerWorld world, GameProfile profile, SyncedClientOptions clientOptions) {
        super(server, world, profile, clientOptions);
    }

    @Override
    public FakePlayerActionManager getActionManager() {
        return this.actionManager;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void fakePlayerTick(CallbackInfo ci) {
        //假玩家回血
        if (CarpetOrgAdditionSettings.fakePlayerHeal) {
            long time = thisPlayer.getWorld().getTime();
            if (time % 40 == 0) {
                thisPlayer.heal(1);
            }
        }
        try {
            //根据假玩家操作类型执行操作
            this.getActionManager().executeAction();
        } catch (RuntimeException e) {
            //将错误信息写入日志
            CarpetOrgAddition.LOGGER.error(thisPlayer.getName().getString() + "在执行操作“" + this.getActionManager().getAction().toString() + "”时遇到意外错误:", e);
            //让假玩家停止当前操作
            this.getActionManager().stop();
            //向聊天栏发送错误消息的反馈
            MessageUtils.broadcastTextMessage(thisPlayer, TextUtils.getTranslate("carpet.commands.playerAction.exception.runtime",
                    thisPlayer.getDisplayName()));
        }
    }
}
