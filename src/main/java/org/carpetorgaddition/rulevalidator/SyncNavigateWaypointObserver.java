package org.carpetorgaddition.rulevalidator;

import carpet.api.settings.Rule;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import org.carpetorgaddition.network.WaypointClearS2CPack;
import org.carpetorgaddition.util.navigator.NavigatorInterface;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SyncNavigateWaypointObserver extends AbstractValidator<Boolean> implements Rule.Condition{
    @Override
    public boolean validate(Boolean newValue) {
        return true;
    }

    @Override
    public @NotNull MutableText errorMessage() {
        throw new IllegalStateException("规则值设置失败");
    }

    @Override
    public void onChange(@Nullable ServerCommandSource source, @Nullable Boolean newValue) {
        if (source == null || newValue == null) {
            return;
        }
        List<ServerPlayerEntity> list = source.getServer().getPlayerManager().getPlayerList().stream()
                .filter(player -> NavigatorInterface.getInstance(player).getNavigator() != null).toList();
        // 设置玩家路径点
        if (newValue) {
            // noinspection DataFlowIssue getNavigator()方法不会为null，上面的stream流已经过滤了null值
            list.forEach(player -> NavigatorInterface.getInstance(player).getNavigator().sendWaypointUpdate());
        } else {
            list.forEach(player -> ServerPlayNetworking.send(player, new WaypointClearS2CPack()));
        }
    }

    @Override
    public boolean shouldRegister() {
        return false;
    }
}
