package org.carpet_org_addition.mixin.command;

import carpet.patches.EntityPlayerMPFake;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import org.carpet_org_addition.CarpetOrgAddition;
import org.carpet_org_addition.util.MathUtils;
import org.carpet_org_addition.util.MessageUtils;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.fakeplayer.FakePlayerActionInterface;
import org.carpet_org_addition.util.fakeplayer.FakePlayerActionManager;
import org.carpet_org_addition.util.fakeplayer.FakePlayerSafeAfkInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(EntityPlayerMPFake.class)
public abstract class EntityPlayerMPFakeMixin implements FakePlayerActionInterface, FakePlayerSafeAfkInterface {
    @Shadow
    public abstract void kill();

    @Unique
    private final EntityPlayerMPFake thisPlayer = (EntityPlayerMPFake) (Object) this;

    @Unique
    private final FakePlayerActionManager actionManager = new FakePlayerActionManager(thisPlayer);

    @Unique
    private float safeAfkThreshold = -1F;

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
            CarpetOrgAddition.LOGGER.error("{}在执行操作“{}”时遇到意外错误:", thisPlayer.getName().getString(),
                    this.getActionManager().getAction().toString(), e);
            // 向聊天栏发送错误消息的反馈
            MutableText message = TextUtils.getTranslate("carpet.commands.playerAction.exception.runtime",
                    thisPlayer.getDisplayName(), this.getActionManager().getAction().getDisplayName());
            MessageUtils.broadcastTextMessage(thisPlayer, TextUtils.setColor(message, Formatting.RED));
            // 让假玩家停止当前操作
            this.getActionManager().stop();
        }
    }

    @Inject(method = "tick", at = @At("TAIL"), cancellable = true)
    private void safeAfk(CallbackInfo ci) {
        if (this.safeAfkThreshold > 0 && thisPlayer.getHealth() <= this.safeAfkThreshold) {
            // 退出假人
            this.kill();
            // 假玩家剩余血量
            String health = MathUtils.keepTwoDecimalPlaces(thisPlayer.getHealth());
            MutableText message = TextUtils.getTranslate("carpet.commands.playerManager.safeafk.trigger", thisPlayer.getDisplayName(), health);
            // 广播触发消息，斜体淡灰色
            MessageUtils.broadcastTextMessage(thisPlayer, TextUtils.toGrayItalic(message));
            // 结束方法，不再执行剩余的tick方法
            ci.cancel();
        }
    }

    @Override
    public void setHealthThreshold(float threshold) {
        this.safeAfkThreshold = threshold;
    }
}
