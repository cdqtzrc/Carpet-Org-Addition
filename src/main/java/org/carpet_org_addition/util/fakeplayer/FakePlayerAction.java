package org.carpet_org_addition.util.fakeplayer;

import org.carpet_org_addition.util.fakeplayer.actiondata.*;

public enum FakePlayerAction {
    /**
     * 停止操作
     */
    STOP,
    /**
     * 物品分拣
     */
    SORTING,
    /**
     * 清空潜影盒
     */
    CLEAN,
    /**
     * 填充潜影盒
     */
    FILL,
    /**
     * 在工作台合成物品
     */
    CRAFTING_TABLE_CRAFT,
    /**
     * 在生存模式物品栏合成物品
     */
    INVENTORY_CRAFT,
    /**
     * 自动重命名物品
     */
    RENAME,
    /**
     * 自动使用切石机
     */
    STONECUTTING,
    /**
     * 自动交易
     */
    TRADE;

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
