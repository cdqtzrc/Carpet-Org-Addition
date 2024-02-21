package org.carpet_org_addition.util.fakeplayer;

import carpet.patches.EntityPlayerMPFake;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import org.carpet_org_addition.util.helpers.Counter;
import org.carpet_org_addition.util.helpers.ItemMatcher;

//假玩家动作接口
public interface FakePlayerActionInterface {
    // 获取命令的上下文属性
    CommandContext<ServerCommandSource> getContext();

    void setContext(CommandContext<ServerCommandSource> context);

    // 假玩家操作类型
    FakePlayerActionType getAction();

    void setAction(FakePlayerActionType action);

    /**
     * 使用数组的动态初始化把数组提前创建好，需要修改的时候只修改数组内的元素，这样能保证数组的长度是固定的
     */
    ItemMatcher[] ITEMS_3X3 = new ItemMatcher[9];
    // 假玩家3x3合成时的配方

    ItemMatcher[] get3x3Craft();

    void set3x3Craft(ItemMatcher[] items);

    ItemMatcher[] ITEMS_2X2 = new ItemMatcher[4];

    ItemMatcher[] get2x2Craft();

    void set2x2Craft(ItemMatcher[] items);

    Counter<FakePlayerActionType> getTickCounter();

    // 将假玩家类型强转为假玩家动作接口类型
    static FakePlayerActionInterface getInstance(EntityPlayerMPFake fakePlayer) {
        return (FakePlayerActionInterface) fakePlayer;
    }
}
