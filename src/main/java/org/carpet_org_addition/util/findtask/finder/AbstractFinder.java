package org.carpet_org_addition.util.findtask.finder;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.carpet_org_addition.util.CommandUtils;
import org.carpet_org_addition.util.findtask.feedback.AbstractFindFeedback;
import org.carpet_org_addition.util.findtask.result.AbstractFindResult;

import java.util.ArrayList;

public abstract class AbstractFinder {
    protected final World world;
    protected final BlockPos sourcePos;
    protected final int range;
    protected final CommandContext<ServerCommandSource> context;

    protected AbstractFinder(World world, BlockPos sourcePos, int range, CommandContext<ServerCommandSource> context) {
        this.world = world;
        this.sourcePos = sourcePos;
        this.range = range;
        this.context = context;
    }

    /**
     * 开始查找
     *
     * @return 存储查找结果的集合
     * @throws CommandSyntaxException 方法执行超时后抛出
     */
    public abstract ArrayList<? extends AbstractFindResult> startSearch() throws CommandSyntaxException;

    // 检查查找是否超时
    protected final void checkTimeOut(long startTimeMillis) throws CommandSyntaxException {
        if (System.currentTimeMillis() - startTimeMillis > 3000) {
            //3秒内未完成方块查找，通过抛出异常结束方法
            throw CommandUtils.createException(AbstractFindFeedback.TIME_OUT);
        }
    }
}
