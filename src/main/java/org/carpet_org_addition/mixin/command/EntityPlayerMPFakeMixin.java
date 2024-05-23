package org.carpet_org_addition.mixin.command;

import carpet.patches.EntityPlayerMPFake;
import org.carpet_org_addition.CarpetOrgAddition;
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
public class EntityPlayerMPFakeMixin implements FakePlayerActionInterface {
    @Unique
    private final EntityPlayerMPFake thisPlayer = (EntityPlayerMPFake) (Object) this;

    @Unique
    private final FakePlayerActionManager actionManager = new FakePlayerActionManager(thisPlayer);

    @Override
    public FakePlayerActionManager getActionManager() {
        return this.actionManager;
    }

    @Override
    public void copyActionManager(EntityPlayerMPFake oldPlayer) {
        this.actionManager.copyActionData(oldPlayer);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void fakePlayerTick(CallbackInfo ci) {
        try {
            // 根据假玩家动作类型执行动作
            this.getActionManager().executeAction();
        } catch (RuntimeException e) {
            // 将错误信息写入日志
            CarpetOrgAddition.LOGGER.error(thisPlayer.getName().getString() + "在执行操作“" + this.getActionManager().getAction().toString() + "”时遇到意外错误:", e);
            // 让假玩家停止当前操作
            this.getActionManager().stop();
            // 向聊天栏发送错误消息的反馈
            MessageUtils.broadcastTextMessage(thisPlayer, TextUtils.getTranslate("carpet.commands.playerAction.exception.runtime",
                    thisPlayer.getDisplayName()));
        }
    }
}
