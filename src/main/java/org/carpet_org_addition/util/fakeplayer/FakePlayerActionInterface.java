package org.carpet_org_addition.util.fakeplayer;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

//假玩家动作接口
public interface FakePlayerActionInterface {
    //获取命令的上下文属性
    CommandContext<ServerCommandSource> getContext();

    void setContext(CommandContext<ServerCommandSource> context);

    //假玩家操作类型
    FakePlayerActionType getAction();

    void setAction(FakePlayerActionType action);
}
