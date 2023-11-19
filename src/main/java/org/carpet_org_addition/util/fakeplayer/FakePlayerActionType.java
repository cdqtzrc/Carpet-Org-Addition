package org.carpet_org_addition.util.fakeplayer;

import carpet.patches.EntityPlayerMPFake;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;

import java.util.ArrayList;

public enum FakePlayerActionType {
    //假玩家停止操作
    STOP,
    //假玩家物品分拣
    SORTING,
    //假玩家清空容器
    CLEAN,
    //假玩家填充容器
    FILL,
    //假玩家自动合成物品（单个材料）
    CRAFT_ONE,
    //假玩家合成物品（四个相同的材料）
    CRAFT_FOUR,
    //假玩家自动合成物品（九个相同的材料）
    CRAFT_NINE,
    //假玩家合成物品（3x3自定义合成）
    CRAFT_3X3,
    //假玩家合成物品（2x2自定义合成）
    CRAFT_2X2,
    //假玩家自动重命名
    RENAME,
    //假玩家切石机
    STONECUTTING,
    //假玩家自动交易
    TRADE;

    //获取假玩家操作类型的字符串或可变文本形式
    public ArrayList<MutableText> getActionText(CommandContext<ServerCommandSource> context, EntityPlayerMPFake fakePlayer) throws CommandSyntaxException {
        FakePlayerActionInterface fakePlayerActionInterface;
        if (context == null) {
            return FakePlayerActionInfo.showStopInfo(fakePlayer);
        }
        // 现在的命令执行上下文来自/playerTools <玩家名> action query，这条命令的命令执行上下文中只记录了一个玩家的信息
        // 需要获取该玩家的对象，然后根据这个假玩家获取假玩家当前的命令执行上下文，这一个命令执行上下文对象中才有获取文本所需要的信息
        fakePlayerActionInterface = (FakePlayerActionInterface) EntityArgumentType.getPlayer(context, "player");
        context = fakePlayerActionInterface.getContext();
        return switch (this) {
            case STOP -> FakePlayerActionInfo.showStopInfo(fakePlayer);
            case SORTING -> FakePlayerActionInfo.showSortingInfo(context, fakePlayer);
            case CLEAN -> FakePlayerActionInfo.showCleanInfo(fakePlayer);
            case FILL -> FakePlayerActionInfo.showFillInfo(context, fakePlayer);
            case CRAFT_NINE, CRAFT_3X3 -> FakePlayerActionInfo.showCraftingTableCraftInfo(context, fakePlayer);
            case CRAFT_ONE, CRAFT_FOUR, CRAFT_2X2 ->
                    FakePlayerActionInfo.showSurvivalInventoryCraftInfo(context, fakePlayer);
            case RENAME -> FakePlayerActionInfo.showRenameInfo(context, fakePlayer);
            case STONECUTTING -> FakePlayerActionInfo.showStoneCuttingInfo(context, fakePlayer);
            case TRADE -> FakePlayerActionInfo.showTradeInfo(context, fakePlayer);
        };
    }

    //是合成物品的操作类型
    public boolean isCraftAction() {
        return this == CRAFT_ONE || this == CRAFT_FOUR || this == CRAFT_NINE || this == CRAFT_2X2 || this == CRAFT_3X3;
    }

    @Override
    public String toString() {
        return switch (this) {
            case STOP -> "停止";
            case SORTING -> "分拣";
            case CLEAN -> "清空潜影盒";
            case FILL -> "填充潜影盒";
            case CRAFT_ONE -> "合成(单个材料)";
            case CRAFT_FOUR -> "合成(四个相同材料)";
            case CRAFT_NINE -> "合成(九个相同材料)";
            case CRAFT_3X3 -> "合成(3x3自定义合成)";
            case CRAFT_2X2 -> "合成(2x2自定义合成)";
            case RENAME -> "重命名";
            case STONECUTTING -> "切石";
            case TRADE -> "交易";
        };
    }
}
