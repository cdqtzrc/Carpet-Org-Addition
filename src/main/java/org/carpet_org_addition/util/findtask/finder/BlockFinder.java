package org.carpet_org_addition.util.findtask.finder;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.carpet_org_addition.util.findtask.result.AbstractFindResult;

import java.util.ArrayList;

public class BlockFinder extends AbstractFinder {
    public BlockFinder(World world, BlockPos sourcePos, int range, CommandContext<ServerCommandSource> context) {
        super(world, sourcePos, range, context);
    }

    @Override
    public ArrayList<? extends AbstractFindResult> startSearch() throws CommandSyntaxException {
        return null;
    }
}
