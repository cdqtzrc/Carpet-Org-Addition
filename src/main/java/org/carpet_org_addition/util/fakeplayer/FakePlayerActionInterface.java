package org.carpet_org_addition.util.fakeplayer;

import carpet.patches.EntityPlayerMPFake;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import org.carpet_org_addition.util.helpers.Counter;
import org.carpet_org_addition.util.matcher.Matcher;

//假玩家动作接口
public interface FakePlayerActionInterface {
    // 获取命令的上下文属性
    CommandContext<ServerCommandSource> getContext();

    void setContext(CommandContext<ServerCommandSource> context);

    // 假玩家操作类型
    FakePlayerAction getAction();

    void setAction(FakePlayerAction action);

    // 假玩家3x3合成时的配方
    Matcher[] get3x3Craft();

    void set3x3Craft(Matcher[] items);

    Matcher[] get2x2Craft();

    void set2x2Craft(Matcher[] items);

    Counter<Object> getTickCounter();

    // 将假玩家类型强转为假玩家动作接口类型
    static FakePlayerActionInterface getInstance(EntityPlayerMPFake fakePlayer) {
        return (FakePlayerActionInterface) fakePlayer;
    }
}
