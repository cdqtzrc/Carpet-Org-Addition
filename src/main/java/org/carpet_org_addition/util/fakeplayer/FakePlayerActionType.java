package org.carpet_org_addition.util.fakeplayer;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.Items;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.carpet_org_addition.util.TextUtils;

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
    STONE_CUTTING,
    //假玩家自动交易
    TRADE;

    //获取假玩家操作类型的字符串或可变文本形式
    public MutableText getActionText(CommandContext<ServerCommandSource> context) {
        FakePlayerActionInterface fakePlayerActionInterface;
        try {
            if (context == null) {
                return TextUtils.getTranslate("carpet.commands.playerTools.action.type.stop");
            }
            // 现在的命令执行上下文来自/playerTools <玩家名> action query，这条命令的命令执行上下文中只记录了一个玩家的信息
            // 需要获取该玩家的对象，然后根据这个假玩家获取假玩家当前的命令执行上下文，这一个命令执行上下文对象中才有获取文本所需要的信息
            fakePlayerActionInterface = (FakePlayerActionInterface) EntityArgumentType.getPlayer(context, "player");
            context = fakePlayerActionInterface.getContext();
        } catch (Exception e) {
            //应该不会执行到这里吧
            return TextUtils.getTranslate("carpet.commands.playerTools.action.type.fail");
        }
        return switch (this) {
            case STOP -> TextUtils.getTranslate("carpet.commands.playerTools.action.type.stop");
            case SORTING ->
                    TextUtils.getTranslate("carpet.commands.playerTools.action.type.sorting", getItemStatsName(context, "item"));
            case CLEAN ->
                    TextUtils.getTranslate("carpet.commands.playerTools.action.type.clean", TextUtils.getItemName(Items.SHULKER_BOX));
            case FILL ->
                    TextUtils.getTranslate("carpet.commands.playerTools.action.type.fill", TextUtils.getItemName(Items.SHULKER_BOX), getItemStatsName(context, "item"));
            case CRAFT_ONE ->
                    TextUtils.getTranslate("carpet.commands.playerTools.action.type.craft_one", getItemStatsName(context, "item"));
            case CRAFT_FOUR ->
                    TextUtils.getTranslate("carpet.commands.playerTools.action.type.craft_four", getItemStatsName(context, "item"));
            case CRAFT_NINE ->
                    TextUtils.getTranslate("carpet.commands.playerTools.action.type.craft_nine", getItemStatsName(context, "item"));
            case CRAFT_3X3 -> TextUtils.getTranslate("carpet.commands.playerTools.action.type.craft_3x3",
                    getCraftItemName(0, fakePlayerActionInterface),
                    getCraftItemName(1, fakePlayerActionInterface),
                    getCraftItemName(2, fakePlayerActionInterface),
                    getCraftItemName(3, fakePlayerActionInterface),
                    getCraftItemName(4, fakePlayerActionInterface),
                    getCraftItemName(5, fakePlayerActionInterface),
                    getCraftItemName(6, fakePlayerActionInterface),
                    getCraftItemName(7, fakePlayerActionInterface),
                    getCraftItemName(8, fakePlayerActionInterface)
            );
            case CRAFT_2X2 -> TextUtils.getTranslate("carpet.commands.playerTools.action.type.craft_2x2",
                    getItemStatsName(context, "item1"),
                    getItemStatsName(context, "item2"),
                    getItemStatsName(context, "item3"),
                    getItemStatsName(context, "item4"));
            case RENAME -> TextUtils.getTranslate("carpet.commands.playerTools.action.type.rename",
                    getItemStatsName(context, "item"), StringArgumentType.getString(context, "name"));
            case STONE_CUTTING -> TextUtils.getTranslate("carpet.commands.playerTools.action.type.stone_cutting",
                    getItemStatsName(context, "item"));
            case TRADE -> TextUtils.getTranslate("carpet.commands.playerTools.action.type.trade");
        };
    }

    //获取物品名
    private static Text getItemStatsName(CommandContext<ServerCommandSource> context, String name) {
        return ItemStackArgumentType.getItemStackArgument(context, name).getItem().getDefaultStack().toHoverableText();
    }

    // 获取合成材料名称
    private static Text getCraftItemName(int number, FakePlayerActionInterface fakePlayerActionInterface) {
        return fakePlayerActionInterface.getCraft()[number].getDefaultStack().toHoverableText();
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
            case STONE_CUTTING -> "切石";
            case TRADE -> "交易";
        };
    }
}