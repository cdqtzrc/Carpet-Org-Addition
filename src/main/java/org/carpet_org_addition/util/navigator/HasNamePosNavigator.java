package org.carpet_org_addition.util.navigator;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.carpet_org_addition.util.MathUtils;
import org.carpet_org_addition.util.MessageUtils;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.WorldUtils;
import org.jetbrains.annotations.NotNull;

public class HasNamePosNavigator extends BlockPosNavigator {
    private final Text name;

    public HasNamePosNavigator(@NotNull ServerPlayerEntity player, BlockPos blockPos, World world, Text name) {
        super(player, blockPos, world);
        this.name = name;
    }

    @Override
    public void tick() {
        if (this.terminate()) {
            return;
        }
        MutableText text;
        MutableText posText = TextUtils.simpleBlockPos(this.blockPos);
        // 玩家与目的地是否在同一维度
        if (this.player.getWorld().equals(this.world)) {
            MutableText distance = TextUtils.translate(DISTANCE, MathUtils.getBlockIntegerDistance(this.player.getBlockPos(), this.blockPos));
            text = getHUDText(this.blockPos.toCenterPos(), TextUtils.translate(IN, this.name, posText), distance);
        } else {
            text = TextUtils.translate(IN, this.name, TextUtils.appendAll(WorldUtils.getDimensionName(this.world), posText));
        }
        MessageUtils.sendTextMessageToHud(this.player, text);
    }

    @Override
    public HasNamePosNavigator copy(ServerPlayerEntity player) {
        return new HasNamePosNavigator(player, this.blockPos, this.world, this.name);
    }
}
