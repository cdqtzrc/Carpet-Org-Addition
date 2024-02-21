package org.carpet_org_addition.command;

import carpet.CarpetSettings;
import carpet.patches.EntityPlayerMPFake;
import carpet.utils.CommandHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.ItemPredicateArgumentType;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.util.CommandUtils;
import org.carpet_org_addition.util.MessageUtils;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.fakeplayer.FakePlayerActionInterface;
import org.carpet_org_addition.util.fakeplayer.FakePlayerActionType;
import org.carpet_org_addition.util.fakeplayer.FakePlayerGuiCraftScreenHandler;
import org.carpet_org_addition.util.helpers.Counter;
import org.carpet_org_addition.util.helpers.ItemMatcher;

import java.util.Arrays;
import java.util.function.Predicate;

public class PlayerActionCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandBuildContext) {
        dispatcher.register(CommandManager.literal("playerAction").requires(source -> CommandHelper.canUseCommand(source, CarpetOrgAdditionSettings.commandPlayerAction))
                .then(CommandManager.argument("player", EntityArgumentType.player())
                        .then(CommandManager.literal("sorting").then(CommandManager.argument("item", ItemStackArgumentType.itemStack(commandBuildContext)).then(CommandManager.argument("this", Vec3ArgumentType.vec3())
                                .then(CommandManager.argument("other", Vec3ArgumentType.vec3()).executes(context -> setAction(context, CommandUtils.getPlayerEntity(context), FakePlayerActionType.SORTING))))))
                        .then(CommandManager.literal("clean").executes(context -> setAction(context, CommandUtils.getPlayerEntity(context), FakePlayerActionType.CLEAN)))
                        .then(CommandManager.literal("fill").then(CommandManager.argument("item", ItemStackArgumentType.itemStack(commandBuildContext))
                                .executes(context -> setAction(context, CommandUtils.getPlayerEntity(context), FakePlayerActionType.FILL))))
                        .then(CommandManager.literal("stop").executes(context -> setAction(context, CommandUtils.getPlayerEntity(context), FakePlayerActionType.STOP)))
                        .then(CommandManager.literal("craft")
                                .then(CommandManager.literal("one").then(CommandManager.argument("item", ItemPredicateArgumentType.itemPredicate(commandBuildContext))
                                        .executes(context -> setAction(context, CommandUtils.getPlayerEntity(context), FakePlayerActionType.CRAFT_ONE))))
                                .then(CommandManager.literal("nine").then(CommandManager.argument("item", ItemPredicateArgumentType.itemPredicate(commandBuildContext))
                                        .executes(context -> setAction(context, CommandUtils.getPlayerEntity(context), FakePlayerActionType.CRAFT_NINE))))
                                .then(CommandManager.literal("four").then(CommandManager.argument("item", ItemPredicateArgumentType.itemPredicate(commandBuildContext))
                                        .executes(context -> setAction(context, CommandUtils.getPlayerEntity(context), FakePlayerActionType.CRAFT_FOUR))))
                                .then(CommandManager.literal("3x3")
                                        .then(CommandManager.argument("item1", ItemPredicateArgumentType.itemPredicate(commandBuildContext))
                                                .then(CommandManager.argument("item2", ItemPredicateArgumentType.itemPredicate(commandBuildContext))
                                                        .then(CommandManager.argument("item3", ItemPredicateArgumentType.itemPredicate(commandBuildContext))
                                                                .then(CommandManager.argument("item4", ItemPredicateArgumentType.itemPredicate(commandBuildContext))
                                                                        .then(CommandManager.argument("item5", ItemPredicateArgumentType.itemPredicate(commandBuildContext))
                                                                                .then(CommandManager.argument("item6", ItemPredicateArgumentType.itemPredicate(commandBuildContext))
                                                                                        .then(CommandManager.argument("item7", ItemPredicateArgumentType.itemPredicate(commandBuildContext))
                                                                                                .then(CommandManager.argument("item8", ItemPredicateArgumentType.itemPredicate(commandBuildContext))
                                                                                                        .then(CommandManager.argument("item9", ItemPredicateArgumentType.itemPredicate(commandBuildContext))
                                                                                                                .executes(context -> setAction(context, CommandUtils.getPlayerEntity(context), FakePlayerActionType.CRAFT_3X3))))))))))))
                                .then(CommandManager.literal("2x2")
                                        .then(CommandManager.argument("item1", ItemPredicateArgumentType.itemPredicate(commandBuildContext))
                                                .then(CommandManager.argument("item2", ItemPredicateArgumentType.itemPredicate(commandBuildContext))
                                                        .then(CommandManager.argument("item3", ItemPredicateArgumentType.itemPredicate(commandBuildContext))
                                                                .then(CommandManager.argument("item4", ItemPredicateArgumentType.itemPredicate(commandBuildContext))
                                                                        .executes(context -> setAction(context, CommandUtils.getPlayerEntity(context), FakePlayerActionType.CRAFT_2X2)))))))
                                .then(CommandManager.literal("gui").executes(context -> openFakePlayerCraftGui(context, CommandUtils.getPlayerEntity(context)))))
                        .then(CommandManager.literal("trade").then(CommandManager.argument("index", IntegerArgumentType.integer(1))
                                .executes(context -> setAction(context, CommandUtils.getPlayerEntity(context), FakePlayerActionType.TRADE))
                                .then(CommandManager.literal("void")
                                        .executes(context -> setAction(context, CommandUtils.getPlayerEntity(context), FakePlayerActionType.VOID_TRADE)))))
                        .then(CommandManager.literal("info").executes(context -> getAction(context, CommandUtils.getPlayerEntity(context))))
                        .then(CommandManager.literal("rename").then(CommandManager.argument("item", ItemStackArgumentType.itemStack(commandBuildContext))
                                .then(CommandManager.argument("name", StringArgumentType.string())
                                        .executes(context -> setAction(context, CommandUtils.getPlayerEntity(context), FakePlayerActionType.RENAME)))))
                        .then(CommandManager.literal("stonecutting").then(CommandManager.argument("item", ItemStackArgumentType.itemStack(commandBuildContext))
                                .then(CommandManager.argument("button", IntegerArgumentType.integer(1))
                                        .executes(context -> setAction(context, CommandUtils.getPlayerEntity(context), FakePlayerActionType.STONECUTTING)))))
                )
        );
    }

    //设置假玩家操作类型
    private static int setAction(CommandContext<ServerCommandSource> context, ServerPlayerEntity fakePlayer,
                                 FakePlayerActionType action) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        if (action.isCraftAction()) {
            // 提示启用Ctrl+Q合成修复
            promptToEnableCtrlQCraftingFix(source);
        }
        //判断该玩家是否为假玩家，此处必须为假玩家，只有假玩家类实现了假玩家动作接口
        if (CommandUtils.checkFakePlayer(fakePlayer)) {
            //将假玩家类型强转为假玩家动作接口
            FakePlayerActionInterface fakePlayerActionInterface = (FakePlayerActionInterface) fakePlayer;
            //如果假玩家动作类型是自定义物品合成，为数组中的每一个元素赋值
            switch (action) {
                // 单个合成材料，在生存模式物品栏中合成，第一个元素填入指定物品，其他元素填入空气
                case CRAFT_ONE -> {
                    Predicate<ItemStack> item = ItemPredicateArgumentType.getItemStackPredicate(context, "item");
                    fakePlayerActionInterface.set2x2Craft(fillArray(new ItemMatcher(item), new ItemMatcher[4], false));
                }
                // 四个相同的合成材料，在生存模式物品栏合成，所有元素都填入指定物品
                case CRAFT_FOUR -> {
                    Predicate<ItemStack> item = ItemPredicateArgumentType.getItemStackPredicate(context, "item");
                    fakePlayerActionInterface.set2x2Craft(fillArray(new ItemMatcher(item), new ItemMatcher[4], true));
                }
                // 九个相同的合成材料，在工作台合成，所有元素都填入指定物品
                case CRAFT_NINE -> {
                    Predicate<ItemStack> item = ItemPredicateArgumentType.getItemStackPredicate(context, "item");
                    fakePlayerActionInterface.set3x3Craft(fillArray(new ItemMatcher(item), new ItemMatcher[9], true));
                }
                // 4个不同的合成材料，在生存模式物品栏合成，每个元素填入不同的物品
                case CRAFT_2X2 -> {
                    ItemMatcher[] items = new ItemMatcher[4];
                    for (int i = 1; i <= 4; i++) {
                        //获取每一个合成材料
                        items[i - 1] = new ItemMatcher(ItemPredicateArgumentType.getItemStackPredicate(context, "item" + i));
                        fakePlayerActionInterface.set2x2Craft(items);
                    }
                }
                // 九个不同的合成材料，在工作台合成，每个元素填入不同的物品
                case CRAFT_3X3 -> {
                    ItemMatcher[] items = new ItemMatcher[9];
                    for (int i = 1; i <= 9; i++) {
                        items[i - 1] = new ItemMatcher(ItemPredicateArgumentType.getItemStackPredicate(context, "item" + i));
                    }
                    fakePlayerActionInterface.set3x3Craft(items);
                }
                case VOID_TRADE -> {
                    Counter<FakePlayerActionType> tickCount = fakePlayerActionInterface.getTickCounter();
                    tickCount.set(FakePlayerActionType.VOID_TRADE, 5);
                }
                default -> {
                    // 什么也不做
                }
            }
            //设置假玩家的操作类型和命令的参数
            fakePlayerActionInterface.setAction(action);
            fakePlayerActionInterface.setContext(context);
        }
        return 1;
    }


    // 填充数组
    private static ItemMatcher[] fillArray(ItemMatcher itemMatcher, ItemMatcher[] itemArr, boolean directFill) {
        if (directFill) {
            // 直接使用元素填满整个数组
            Arrays.fill(itemArr, itemMatcher);
        } else {
            // 第一个元素填入指定物品，其他元素填入空气
            for (int i = 0; i < itemArr.length; i++) {
                if (i == 0) {
                    itemArr[i] = itemMatcher;
                } else {
                    itemArr[i] = ItemMatcher.AIR_ITEM_MATCHER;
                }
            }
        }
        return itemArr;
    }

    //获取假玩家操作类型
    private static int getAction(CommandContext<ServerCommandSource> context, ServerPlayerEntity fakePlayer)
            throws CommandSyntaxException {
        if (CommandUtils.checkFakePlayer(fakePlayer)) {
            FakePlayerActionType action = ((FakePlayerActionInterface) fakePlayer).getAction();
            MessageUtils.sendListMessage(context.getSource(), action.getActionText(context, (EntityPlayerMPFake) fakePlayer));
        }
        return 1;
    }

    // 打开控制假人合成物品的GUI
    private static int openFakePlayerCraftGui(CommandContext<ServerCommandSource> context, ServerPlayerEntity fakePlayer)
            throws CommandSyntaxException {
        ServerPlayerEntity player = CommandUtils.getPlayer(context);
        if (CommandUtils.checkFakePlayer(fakePlayer)) {
            // 打开合成GUI
            SimpleNamedScreenHandlerFactory screen = new SimpleNamedScreenHandlerFactory((i, playerInventory, playerEntity)
                    -> new FakePlayerGuiCraftScreenHandler(i, playerInventory, (EntityPlayerMPFake) fakePlayer,
                    ScreenHandlerContext.create(player.getWorld(), player.getBlockPos()), new SimpleInventory(9), context),
                    TextUtils.getTranslate("carpet.commands.playerAction.info.craft.gui"));
            player.openHandledScreen(screen);
        }
        return 1;
    }

    // 提示启用Ctrl+Q合成修复
    public static void promptToEnableCtrlQCraftingFix(ServerCommandSource source) {
        if (CarpetSettings.ctrlQCraftingFix) {
            return;
        }
        //判断当前命令执行者是否有足够的权限
        boolean hasPermission = source.hasPermissionLevel(getCarpetPermissionLevel(source));
        MutableText suggest;
        if (hasPermission) {
            suggest = TextUtils.suggest(TextUtils.getTranslate("carpet.commands.playerAction.set.here")
                            .getString(), "/carpet ctrlQCraftingFix true",
                    TextUtils.getTranslate("carpet.commands.playerAction.set.has_permission"),
                    Formatting.AQUA);
        } else {
            suggest = TextUtils.suggest(
                    TextUtils.getTranslate("carpet.commands.playerAction.set.here").getString(),
                    null, TextUtils.getTranslate("carpet.commands.playerAction.set.no_permission"),
                    Formatting.RED);
        }
        MessageUtils.sendCommandFeedback(source, "carpet.commands.playerAction.set", suggest);
    }

    //获取执行carpet命令需要的权限等级
    private static int getCarpetPermissionLevel(ServerCommandSource source) {
        if (CarpetOrgAdditionSettings.openCarpetPermissions && source.getServer().isSingleplayer()) {
            return 0;
        }
        if ("4".equals(CarpetSettings.carpetCommandPermissionLevel)) {
            return 4;
        }
        return 2;
    }
}
