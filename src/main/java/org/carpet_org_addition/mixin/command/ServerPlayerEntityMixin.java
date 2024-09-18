package org.carpet_org_addition.mixin.command;

import carpet.patches.EntityPlayerMPFake;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.carpet_org_addition.CarpetOrgAddition;
import org.carpet_org_addition.util.MessageUtils;
import org.carpet_org_addition.util.fakeplayer.FakePlayerActionInterface;
import org.carpet_org_addition.util.navigator.*;
import org.carpet_org_addition.util.wheel.Waypoint;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements NavigatorInterface {
    @Unique
    private final ServerPlayerEntity thisPlayer = (ServerPlayerEntity) (Object) this;
    @Unique
    private AbstractNavigator navigator;

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if (this.navigator == null) {
            return;
        }
        try {
            this.navigator.tick();
        } catch (RuntimeException e) {
            MessageUtils.sendCommandErrorFeedback(thisPlayer.getCommandSource(), e, "carpet.commands.navigate.exception");
            CarpetOrgAddition.LOGGER.error("导航器没有按照预期工作", e);
            // 清除导航器
            this.clearNavigator();
        }
    }

    // 玩家穿越末地祭坛的传送门时复制身上的数据
    @Inject(method = "copyFrom", at = @At("HEAD"))
    private void copyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        AbstractNavigator oldNavigator = ((NavigatorInterface) oldPlayer).getNavigator();
        // 复制追踪器对象
        if (oldNavigator != null) {
            this.navigator = oldNavigator.copy(thisPlayer);
        }
        // 复制假玩家动作管理器对象
        if (thisPlayer instanceof FakePlayerActionInterface actionInterface && oldPlayer instanceof EntityPlayerMPFake oldFakePlayer) {
            actionInterface.copyActionManager(oldFakePlayer);
        }
    }

    @Override
    public AbstractNavigator getNavigator() {
        return this.navigator;
    }

    @Override
    public void setNavigator(Entity entity, boolean isContinue) {
        this.navigator = new EntityNavigator(thisPlayer, entity, isContinue);
    }

    @Override
    public void setNavigator(Waypoint waypoint) {
        this.navigator = new WaypointNavigator(thisPlayer, waypoint);
    }

    @Override
    public void setNavigator(BlockPos blockPos, World world) {
        this.navigator = new BlockPosNavigator(thisPlayer, blockPos, world);
    }

    @Override
    public void setNavigator(BlockPos blockPos, World world, Text name) {
        this.navigator = new HasNamePosNavigator(thisPlayer, blockPos, world, name);
    }

    @Override
    public void clearNavigator() {
        this.navigator = null;
    }
}
