package org.carpet_org_addition.util.fakeplayer;

import carpet.patches.EntityPlayerMPFake;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import org.carpet_org_addition.util.fakeplayer.actiondata.*;

import java.util.ArrayList;

public enum FakePlayerAction {
    /**
     * 假玩家停止操作
     */
    STOP,
    /**
     * 假玩家物品分拣
     */
    SORTING,
    /**
     * 假玩家清空容器
     */
    CLEAN,
    /**
     * 假玩家填充容器
     */
    FILL,
    /**
     * 假玩家在工作台合成物品
     */
    CRAFTING_TABLE_CRAFT,
    /**
     * 假玩家在生存模式物品栏合成物品
     */
    INVENTORY_CRAFT,
    /**
     * 假玩家自动重命名
     */
    RENAME,
    /**
     * 假玩家切石机
     */
    STONECUTTING,
    /**
     * 假玩家自动交易
     */
    TRADE;

    //获取假玩家操作类型的字符串或可变文本形式
    public ArrayList<MutableText> getActionText(CommandContext<ServerCommandSource> context, EntityPlayerMPFake fakePlayer) throws CommandSyntaxException {
        if (context == null) {
            return StopData.STOP.info(fakePlayer);
        }
        FakePlayerActionManager actionManager = ((FakePlayerActionInterface) EntityArgumentType.getPlayer(context, "player")).getActionManager();
        return actionManager.getActionData().info(fakePlayer);
    }

    // 检查当前动作是否与指定动作数据匹配
    public void checkActionData(Class<? extends AbstractActionData> clazz) {
        if (clazz != switch (this) {
            case STOP -> StopData.class;
            case SORTING -> SortingData.class;
            case CLEAN -> CleanData.class;
            case FILL -> FillData.class;
            case INVENTORY_CRAFT -> InventoryCraftData.class;
            case CRAFTING_TABLE_CRAFT -> CraftingTableCraftData.class;
            case RENAME -> RenameData.class;
            case STONECUTTING -> StonecuttingData.class;
            case TRADE -> TradeData.class;
        }) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public String toString() {
        return switch (this) {
            case STOP -> "停止";
            case SORTING -> "分拣";
            case CLEAN -> "清空潜影盒";
            case FILL -> "填充潜影盒";
            case CRAFTING_TABLE_CRAFT -> "在工作台合成物品";
            case INVENTORY_CRAFT -> "在生存模式物品栏合成物品";
            case RENAME -> "重命名";
            case STONECUTTING -> "切石";
            case TRADE -> "交易";
        };
    }
}
