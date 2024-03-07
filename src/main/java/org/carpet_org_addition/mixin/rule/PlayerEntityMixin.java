package org.carpet_org_addition.mixin.rule;

import carpet.patches.EntityPlayerMPFake;
import carpet.utils.CommandHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.util.CommandUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Shadow
    public abstract HungerManager getHungerManager();

    @Unique
    private final PlayerEntity thisPlayer = (PlayerEntity) (Object) this;

    //血量不满时也可以进食
    @Inject(method = "canConsume", at = @At("HEAD"), cancellable = true)
    private void canEat(boolean ignoreHunger, CallbackInfoReturnable<Boolean> cir) {
        if (CarpetOrgAdditionSettings.healthNotFullCanEat && thisPlayer.getHealth() < thisPlayer.getMaxHealth() - 0.3//-0.3：可能生命值不满但是显示的心满了
                && this.getHungerManager().getSaturationLevel() <= 5) {
            cir.setReturnValue(true);
        }
    }

    // 快速设置假玩家合成
    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void interact(Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (thisPlayer instanceof ServerPlayerEntity serverPlayer && !thisPlayer.isSpectator()) {
            switch (CarpetOrgAdditionSettings.quickSettingFakePlayerCraft) {
                case FALSE:
                    break;
                case SNEAKING:
                    if (!thisPlayer.isSneaking()) {
                        break;
                    }
                case TRUE:
                    if (serverPlayer.getMainHandStack().isOf(Items.CRAFTING_TABLE)) {
                        if (entity instanceof EntityPlayerMPFake fakePlayer) {
                            CommandUtils.execute(serverPlayer, "/playerAction " + fakePlayer.getName().getString()
                                    + " craft gui", player -> CommandHelper.canUseCommand(player.getCommandSource(),
                                    CarpetOrgAdditionSettings.commandPlayerAction));
                            cir.setReturnValue(ActionResult.SUCCESS);
                        }
                    }
                default: {
                }
            }
        }
    }

}