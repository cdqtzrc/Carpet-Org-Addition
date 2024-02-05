package org.carpet_org_addition.util.findtask.result;

import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.carpet_org_addition.util.MathUtils;
import org.carpet_org_addition.util.TextUtils;

public class BlockFindResult extends AbstractFindResult {
    /**
     * 方块所在的位置
     */
    private final BlockPos blockPos;
    /**
     * 命令执行前玩家所在的位置，用来计算玩家与方块的距离
     */
    private final BlockPos sourceBlockPos;

    public BlockFindResult(BlockPos blockPos, BlockPos sourceBlockPos) {
        this.blockPos = blockPos;
        this.sourceBlockPos = sourceBlockPos;
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    @Override
    public MutableText toText() {
        return TextUtils.getTranslate(
                "carpet.commands.finder.block.feedback",
                MathUtils.getBlockIntegerDistance(sourceBlockPos, blockPos),
                TextUtils.blockPos(blockPos, Formatting.GREEN));
    }
}
