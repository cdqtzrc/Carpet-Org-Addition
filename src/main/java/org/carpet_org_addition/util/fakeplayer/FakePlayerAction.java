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
     * 假玩家清空容器中的指定物品
     */
    CLEAN_DESIGNATED,
    /**
     * 假玩家填充容器
     */
    FILL,
    /**
     * 假玩家自动向容器内填充所有物品
     */
    FILL_ALL,
    /**
     * 假玩家自动合成物品（单个材料）
     */
    CRAFT_ONE,
    /**
     * 假玩家合成物品（四个相同的材料）
     */
    CRAFT_FOUR,
    /**
     * 假玩家自动合成物品（九个相同的材料）
     */
    CRAFT_NINE,
    /**
     * 假玩家合成物品（3x3自定义合成）
     */
    CRAFT_3X3,
    /**
     * 假玩家合成物品（2x2自定义合成）
     */
    CRAFT_2X2,
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
    TRADE,
    /**
     * 假玩家自动虚空交易
     */
    VOID_TRADE;

    //获取假玩家操作类型的字符串或可变文本形式
    public ArrayList<MutableText> getActionText(CommandContext<ServerCommandSource> context, EntityPlayerMPFake fakePlayer) throws CommandSyntaxException {
        if (context == null) {
            return StopData.STOP.info(fakePlayer);
        }
        FakePlayerActionManager actionManager = ((FakePlayerActionInterface) EntityArgumentType.getPlayer(context, "player")).getActionManager();
        return actionManager.getActionData().info(fakePlayer);
    }

    //是合成物品的操作类型
    public boolean isCraftAction() {
        return this == CRAFT_ONE || this == CRAFT_FOUR || this == CRAFT_NINE || this == CRAFT_2X2 || this == CRAFT_3X3;
    }

    // 检查当前动作是否与指定动作数据匹配
    public void checkActionData(Class<? extends AbstractActionData> clazz) {
        if (clazz != switch (this) {
            case STOP -> StopData.class;
            case SORTING -> SortingData.class;
            case CLEAN, CLEAN_DESIGNATED -> CleanData.class;
            case FILL, FILL_ALL -> FillData.class;
            case CRAFT_ONE, CRAFT_FOUR, CRAFT_2X2 -> InventoryCraftData.class;
            case CRAFT_NINE, CRAFT_3X3 -> CraftingTableCraftData.class;
            case RENAME -> RenameData.class;
            case STONECUTTING -> StonecuttingData.class;
            case TRADE, VOID_TRADE -> TradeData.class;
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
            case CLEAN_DESIGNATED -> "清空潜影盒内指定物品";
            case FILL -> "填充潜影盒";
            case CRAFT_ONE -> "合成(单个材料)";
            case CRAFT_FOUR -> "合成(四个相同材料)";
            case CRAFT_NINE -> "合成(九个相同材料)";
            case CRAFT_3X3 -> "合成(3x3自定义合成)";
            case CRAFT_2X2 -> "合成(2x2自定义合成)";
            case RENAME -> "重命名";
            case STONECUTTING -> "切石";
            case TRADE -> "交易";
            case VOID_TRADE -> "虚空交易";
            case FILL_ALL -> "填充任意物品到潜影盒";
        };
    }
}
