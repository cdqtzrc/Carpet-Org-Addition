package org.carpet_org_addition.util.fakeplayer;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.item.Item;
import net.minecraft.server.command.ServerCommandSource;

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
    Item[] ITEMS_3X3 = new Item[9];
    // 假玩家3x3合成时的配方

    Item[] get3x3Craft();

    void set3x3Craft(Item[] items);

    Item[] ITEMS_2X2 = new Item[4];

    Item[] get2x2Craft();

    void set2x2Craft(Item[] items);
}
