package org.carpetorgaddition.rulevalidator;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import org.carpetorgaddition.network.WaypointClearS2CPack;
import org.carpetorgaddition.util.navigator.NavigatorInterface;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SyncNavigateWaypointObserver extends AbstractValidator<Boolean> {
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
        if (source == null) {
            return;
        }
        // TODO 先执行命令，再启用规则可能不会渲染路径点
        // 清除所有玩家的导航点
        if (Boolean.FALSE.equals(newValue)) {
            source.getServer().getPlayerManager().getPlayerList().stream()
                    .filter(player -> NavigatorInterface.getInstance(player).getNavigator() != null)
                    .forEach(player -> ServerPlayNetworking.send(player, new WaypointClearS2CPack()));
        }
    }
}
