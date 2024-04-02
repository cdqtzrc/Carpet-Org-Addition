package org.carpet_org_addition.util.findtask.finder;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.carpet_org_addition.command.FinderCommand;
import org.carpet_org_addition.util.CommandUtils;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.findtask.result.BlockFindResult;
import org.carpet_org_addition.util.helpers.SelectionArea;

import java.util.ArrayList;

public class BlockFinder extends AbstractFinder {
    private final BlockStateArgument argument;
    private final ArrayList<BlockFindResult> list = new ArrayList<>();

    public BlockFinder(ServerWorld world, BlockPos sourcePos, int range, BlockStateArgument blockStateArgument) {
        super(world, sourcePos, range);
        this.argument = blockStateArgument;
    }

    @Override
    public ArrayList<BlockFindResult> startSearch() throws CommandSyntaxException {
        SelectionArea selectionArea = new SelectionArea(this.world, this.sourcePos, this.range);
        long startTimeMillis = System.currentTimeMillis();
        for (BlockPos blockPos : selectionArea) {
            checkTimeOut(startTimeMillis);
            // 如果找到的方块数量过多，直接抛出异常结束方法，不再进行排序
            if (list.size() > FinderCommand.MAXIMUM_STATISTICS) {
                throw CommandUtils.createException("carpet.commands.finder.block.too_much_blocks",
                        TextUtils.getBlockName(this.argument.getBlockState().getBlock()));
            }
            if (argument.test((ServerWorld) this.world, blockPos)) {
                list.add(new BlockFindResult(blockPos, this.sourcePos));
            }
        }
        return this.list;
    }
}
