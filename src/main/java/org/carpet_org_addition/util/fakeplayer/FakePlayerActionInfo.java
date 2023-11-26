package org.carpet_org_addition.util.fakeplayer;

import carpet.patches.EntityPlayerMPFake;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.screen.*;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.TradeOffer;
import net.minecraft.world.World;
import org.carpet_org_addition.util.StringUtils;
import org.carpet_org_addition.util.TextUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Optional;

public class FakePlayerActionInfo {
    // 假玩家没有任何动作时显示的详细信息
    public static ArrayList<MutableText> showStopInfo(EntityPlayerMPFake fakePlayer) {
        ArrayList<MutableText> list = new ArrayList<>();
        // 直接将假玩家没有任何动作的信息加入集合然后返回
        list.add(TextUtils.getTranslate("carpet.commands.playerTools.action.info.stop", fakePlayer.getDisplayName()));
        return list;
    }

    // 显示工作台中物品合成的详细信息
    public static ArrayList<MutableText> showCraftingTableCraftInfo(CommandContext<ServerCommandSource> context, EntityPlayerMPFake fakePlayer) {
        // 获取为假玩家设置的合成配方
        Item[] arr = FakePlayerActionInterface.getInstance(fakePlayer).get3x3Craft();
        // 创建一个集合用来存储可变文本对象，这个集合用来在聊天栏输出多行聊天信息，集合中的每个元素单独占一行
        ArrayList<MutableText> list = new ArrayList<>();
        // 将可变文本“<玩家>正在合成物品，配方:”添加到集合
        list.add(TextUtils.getTranslate("carpet.commands.playerTools.action.info.craft.recipe", fakePlayer.getDisplayName()));
        // 将每一个合成材料以及配方的输出组装成一个大的可变文本对象并添加到集合中
        list.add(TextUtils.appendAll("    ", getHoverText(arr[0]), " ", getHoverText(arr[1]), " ", getHoverText(arr[2])));
        list.add(TextUtils.appendAll("    ", getHoverText(arr[3]), " ", getHoverText(arr[4]), " ", getHoverText(arr[5]),
                " -> ", getHoverText(getCraftOutPut(context, arr))));
        list.add(TextUtils.appendAll("    ", getHoverText(arr[6]), " ", getHoverText(arr[7]), " ", getHoverText(arr[8])));
        // 判断假玩家是否打开了一个工作台
        if (fakePlayer.currentScreenHandler instanceof CraftingScreenHandler currentScreenHandler) {
            // 将可变文本“<玩家>当前合成物品的状态:”添加到集合中
            list.add(TextUtils.getTranslate("carpet.commands.playerTools.action.info.craft.state", fakePlayer.getDisplayName()));
            // 如果打开了，将每一个合成槽位（包括输出槽位）中的物品的名称和堆叠数组装成一个可变文本对象并添加到集合
            // 合成格第一排
            list.add(TextUtils.appendAll(
                    "    ", getWithCountHoverText(currentScreenHandler.getSlot(1).getStack()),
                    " ", getWithCountHoverText(currentScreenHandler.getSlot(2).getStack()),
                    " ", getWithCountHoverText(currentScreenHandler.getSlot(3).getStack())));
            // 合成格第二排和输出槽
            list.add(TextUtils.appendAll(
                    "    ", getWithCountHoverText(currentScreenHandler.getSlot(4).getStack()),
                    " ", getWithCountHoverText(currentScreenHandler.getSlot(5).getStack()),
                    " ", getWithCountHoverText(currentScreenHandler.getSlot(6).getStack()),
                    " -> ", getWithCountHoverText(currentScreenHandler.getSlot(0).getStack())));
            // 合成格第三排
            list.add(TextUtils.appendAll(
                    "    ", getWithCountHoverText(currentScreenHandler.getSlot(7).getStack()),
                    " ", getWithCountHoverText(currentScreenHandler.getSlot(8).getStack()),
                    " ", getWithCountHoverText(currentScreenHandler.getSlot(9).getStack())));
        } else {
            // 如果没有打开工作台，将未打开工作台的信息添加到集合
            list.add(TextUtils.getTranslate("carpet.commands.playerTools.action.info.craft.no_crafting_table",
                    fakePlayer.getDisplayName(), Items.CRAFTING_TABLE.getName()));
        }
        return list;
    }

    // 显示生存模式物品栏中合成物品的详细信息
    public static ArrayList<MutableText> showSurvivalInventoryCraftInfo(CommandContext<ServerCommandSource> context, EntityPlayerMPFake fakePlayer) {
        Item[] arr = FakePlayerActionInterface.getInstance(fakePlayer).get2x2Craft();
        // 创建一个集合用来存储可变文本对象，这个集合用来在聊天栏输出多行聊天信息，集合中的每个元素单独占一行
        ArrayList<MutableText> list = new ArrayList<>();
        // 获取假玩家的显示名称
        Text PlayerName = fakePlayer.getDisplayName();
        // 将可变文本“<玩家>正在合成物品，配方:”添加到集合
        list.add(TextUtils.getTranslate("carpet.commands.playerTools.action.info.craft.recipe", PlayerName));
        // 将每一个合成材料以及配方的输出组装成一个大的可变文本对象并添加到集合中
        list.add(TextUtils.appendAll("    ", getHoverText(arr[0]), " ", getHoverText(arr[1])));
        list.add(TextUtils.appendAll("    ", getHoverText(arr[2]), " ", getHoverText(arr[3]),
                " -> ", getHoverText(getCraftOutPut(context, arr))));
        // 将可变文本“<玩家>当前合成物品的状态:”添加到集合中
        list.add(TextUtils.getTranslate("carpet.commands.playerTools.action.info.craft.state", PlayerName));
        // 获取玩家的生存模式物品栏对象
        PlayerScreenHandler playerScreenHandler = fakePlayer.playerScreenHandler;
        // 将每一个合成槽位（包括输出槽位）中的物品的名称和堆叠数组装成一个可变文本对象并添加到集合
        // 合成格第一排
        list.add(TextUtils.appendAll(
                "    ", getWithCountHoverText(playerScreenHandler.getSlot(1).getStack()),
                " ", getWithCountHoverText(playerScreenHandler.getSlot(2).getStack())
        ));
        // 合成格第二排和输出槽
        list.add(TextUtils.appendAll(
                "    ", getWithCountHoverText(playerScreenHandler.getSlot(3).getStack()),
                " ", getWithCountHoverText(playerScreenHandler.getSlot(4).getStack()),
                " -> ", getWithCountHoverText(playerScreenHandler.getSlot(0).getStack())));
        return list;
    }

    // 显示分拣物品的详细信息
    public static ArrayList<MutableText> showSortingInfo(CommandContext<ServerCommandSource> context, EntityPlayerMPFake fakePlayer) {
        ArrayList<MutableText> list = new ArrayList<>();
        // 获取要分拣的物品名称
        Text itemName = getItemStatsName(context);
        // 获取假玩家的显示名称
        Text fakeName = fakePlayer.getDisplayName();
        // 将假玩家正在分拣物品的消息添加到集合中
        list.add(TextUtils.getTranslate("carpet.commands.playerTools.action.info.sorting.item", fakeName, itemName));
        // 获取分拣物品要丢出的方向
        Vec3d thisVec = Vec3ArgumentType.getVec3(context, "this");
        MutableText thisPos = Text.literal(StringUtils.keepTwoDecimalPlaces(thisVec.getX(), thisVec.getY(), thisVec.getZ()));
        // 获取非分拣物品要丢出的方向
        Vec3d otherVec = Vec3ArgumentType.getVec3(context, "other");
        MutableText otherPos = Text.literal(StringUtils.keepTwoDecimalPlaces(otherVec.getX(), otherVec.getY(), otherVec.getZ()));
        // 将丢要分拣物品的方向的信息添加到集合
        list.add(TextUtils.getTranslate("carpet.commands.playerTools.action.info.sorting.this", itemName, thisPos));
        // 将丢其他物品的方向的信息添加到集合
        list.add(TextUtils.getTranslate("carpet.commands.playerTools.action.info.sorting.other", otherPos));
        return list;
    }

    // 显示清空潜影盒的详细信息
    public static ArrayList<MutableText> showCleanInfo(EntityPlayerMPFake fakePlayer) {
        ArrayList<MutableText> list = new ArrayList<>();
        // 将玩家清空潜影盒的信息添加到集合
        list.add(TextUtils.getTranslate("carpet.commands.playerTools.action.info.clean.item",
                fakePlayer.getDisplayName(),
                Items.SHULKER_BOX.getName()));
        return list;
    }

    // 显示假玩家填充潜影盒的详细信息
    public static ArrayList<MutableText> showFillInfo(CommandContext<ServerCommandSource> context, EntityPlayerMPFake fakePlayer) {
        ArrayList<MutableText> list = new ArrayList<>();
        // 将“<玩家名> 正在向 潜影盒 填充 [item] 物品”信息添加到集合
        list.add(TextUtils.getTranslate("carpet.commands.playerTools.action.info.fill.item",
                fakePlayer.getDisplayName(), Items.SHULKER_BOX.getName(), getItemStatsName(context)));
        return list;
    }

    // 显示重命名的详细信息
    public static ArrayList<MutableText> showRenameInfo(CommandContext<ServerCommandSource> context, EntityPlayerMPFake fakePlayer) {
        ArrayList<MutableText> list = new ArrayList<>();
        // 获取假玩家的显示名称
        Text playerName = fakePlayer.getDisplayName();
        // 获取物品重命名后的名称
        String newName = StringArgumentType.getString(context, "name");
        // 将假玩家要重命名的物品和物品新名称的信息添加到集合
        list.add(TextUtils.getTranslate("carpet.commands.playerTools.action.info.rename.item",
                playerName, getItemStatsName(context), newName));
        // 将假玩家剩余经验的信息添加到集合
        list.add(TextUtils.getTranslate("carpet.commands.playerTools.action.info.rename.xp",
                fakePlayer.experienceLevel));
        if (fakePlayer.currentScreenHandler instanceof AnvilScreenHandler anvilScreenHandler) {
            // 将铁砧GUI上的物品信息添加到集合
            list.add(TextUtils.appendAll("    ",
                    getWithCountHoverText(anvilScreenHandler.getSlot(0).getStack()), " ",
                    getWithCountHoverText(anvilScreenHandler.getSlot(1).getStack()), " -> ",
                    getWithCountHoverText(anvilScreenHandler.getSlot(2).getStack())));
        } else {
            // 将假玩家没有打开铁砧的信息添加到集合
            list.add(TextUtils.getTranslate("carpet.commands.playerTools.action.info.rename.no_anvil",
                    playerName, Items.ANVIL.getName()));
        }
        return list;
    }

    // 显示假玩家使用切石机的详细信息
    public static ArrayList<MutableText> showStoneCuttingInfo(CommandContext<ServerCommandSource> context, EntityPlayerMPFake fakePlayer) {
        // 创建一个物品栏对象用来获取配方的输出物品
        SimpleInventory simpleInventory = new SimpleInventory(1);
        // 获取要切制的材料物品
        ItemStack inputItemStack = ItemStackArgumentType.getItemStackArgument(context, "item").getItem().getDefaultStack();
        simpleInventory.setStack(0, inputItemStack);
        // 获取假玩家所在的世界对象
        World world = fakePlayer.getWorld();
        // 获取要点击切石机左侧按钮的索引
        int buttonIndex = IntegerArgumentType.getInteger(context, "button") - 1;
        // 输出物品的对象
        ItemStack outputItemStack;
        try {
            // 获取与材料和按钮索引对应的配方对象
            StonecuttingRecipe stonecuttingRecipe = world.getRecipeManager().getAllMatches(RecipeType.STONECUTTING,
                    simpleInventory, world).get(buttonIndex).value();
            // 获取与配方对应的物品
            outputItemStack = stonecuttingRecipe.craft(simpleInventory, world.getRegistryManager());
        } catch (IndexOutOfBoundsException e) {
            // 如果索引越界了，将输出物品设置为空
            outputItemStack = ItemStack.EMPTY;
        }
        // 获取输出物品的名称
        Text itemName;
        if (outputItemStack.isEmpty()) {
            // 如果物品为EMPTY，设置物品名称为空气的物品悬停文本
            itemName = Items.AIR.getDefaultStack().toHoverableText();
        } else {
            itemName = outputItemStack.toHoverableText();
        }
        ArrayList<MutableText> list = new ArrayList<>();
        list.add(TextUtils.getTranslate("carpet.commands.playerTools.action.info.stonecutting.item",
                fakePlayer.getDisplayName(), Items.STONECUTTER.getName(), getItemStatsName(context), itemName));
        if (fakePlayer.currentScreenHandler instanceof StonecutterScreenHandler stonecutterScreenHandler) {
            // 将按钮索引的信息添加到集合，按钮在之前减去了1，这里再加回来
            list.add(TextUtils.getTranslate("carpet.commands.playerTools.action.info.stonecutting.button",
                    (buttonIndex + 1)));
            // 将切石机当前的状态的信息添加到集合
            list.add(TextUtils.appendAll("    ",
                    getWithCountHoverText(stonecutterScreenHandler.getSlot(0).getStack()), " -> ",
                    getWithCountHoverText(stonecutterScreenHandler.getSlot(1).getStack())));
        } else {
            // 将假玩家没有打开切石机的消息添加到集合
            list.add(TextUtils.getTranslate("carpet.commands.playerTools.action.info.stonecutting.no_stonecutting",
                    fakePlayer.getDisplayName(), Items.STONECUTTER.getName()));
        }
        return list;
    }

    // 显示假玩家与村民或流浪商人交易的的详细信息
    public static ArrayList<MutableText> showTradeInfo(CommandContext<ServerCommandSource> context, EntityPlayerMPFake fakePlayer) {
        ArrayList<MutableText> list = new ArrayList<>();
        // 获取按钮的索引，从1开始
        int index = IntegerArgumentType.getInteger(context, "index");
        list.add(TextUtils.getTranslate("carpet.commands.playerTools.action.info.trade.item", fakePlayer.getDisplayName(), index));
        if (fakePlayer.currentScreenHandler instanceof MerchantScreenHandler merchantScreenHandler) {
            // 获取当前交易内容的对象，因为按钮索引从1开始，所以此处减去1
            TradeOffer tradeOffer = merchantScreenHandler.getRecipes().get(index - 1);
            // 将“交易选项”文本信息添加到集合中
            list.add(TextUtils.getTranslate("carpet.commands.playerTools.action.info.trade.option", index));
            // 将交易的物品和价格添加到集合中
            list.add(TextUtils.appendAll("    ",
                    getWithCountHoverText(tradeOffer.getAdjustedFirstBuyItem()), " ",
                    getWithCountHoverText(tradeOffer.getSecondBuyItem()), " -> ",
                    getWithCountHoverText(tradeOffer.getSellItem())));
            // 如果当前交易已禁用，将交易已禁用的消息添加到集合，然后直接结束方法并返回集合
            if (tradeOffer.isDisabled()) {
                list.add(TextUtils.getTranslate("carpet.commands.playerTools.action.info.trade.disabled"));
                return list;
            }
            // 将“交易状态”文本信息添加到集合中
            list.add(TextUtils.getTranslate("carpet.commands.playerTools.action.info.trade.state"));
            list.add(TextUtils.appendAll("    ",
                    getWithCountHoverText(merchantScreenHandler.getSlot(0).getStack()), " ",
                    getWithCountHoverText(merchantScreenHandler.getSlot(1).getStack()), " -> ",
                    getWithCountHoverText(merchantScreenHandler.getSlot(2).getStack())));
        } else {
            // 将假玩家没有打开交易界面的消息添加到集合中
            list.add(TextUtils.getTranslate("carpet.commands.playerTools.action.info.trade.no_villager", fakePlayer.getDisplayName()));
        }
        return list;
    }

    // 获取物品名称
    private static Text getItemStatsName(CommandContext<ServerCommandSource> context) {
        return ItemStackArgumentType.getItemStackArgument(context, "item").getItem().getDefaultStack().toHoverableText();
    }

    // 获取物品的可变文本形式
    private static MutableText getHoverText(Item item) {
        if (item == Items.AIR || item == null) {
            return TextUtils.hoverText(Text.literal("[A]"), Items.AIR.getName(), Formatting.DARK_GRAY);
        }
        // 获取物品ID的首字母，然后转为大写，再放进中括号里
        String capitalizeFirstLetter = "[" + String.valueOf(item.toString().charAt(0)).toUpperCase() + "]";
        return TextUtils.hoverText(Text.literal(capitalizeFirstLetter), item.getName(), null);
    }

    // 获取物品堆栈的可变文本形式：物品名称x堆叠数量
    private static MutableText getWithCountHoverText(@NotNull ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return TextUtils.hoverText(Text.literal("[A]"), TextUtils.appendAll(Items.AIR.getName()), Formatting.DARK_GRAY);
        }
        // 获取物品堆栈对应的物品ID的首字母，然后转为大写，再放进中括号里
        String capitalizeFirstLetter = "[" + String.valueOf(itemStack.getItem().toString().charAt(0)).toUpperCase() + "]";
        return TextUtils.hoverText(Text.literal(capitalizeFirstLetter),
                TextUtils.appendAll(itemStack.getItem().getName(), "x", String.valueOf(itemStack.getCount())), null);
    }

    // 获取配方的输出物品
    @SuppressWarnings("ExtractMethodRecommender")
    private static Item getCraftOutPut(CommandContext<ServerCommandSource> context, Item[] arr) {
        // 合成格的宽高，如果数组长度为9，则表示在工作台合成，所以宽高为3，否则，物品在生存模式物品栏合成，所以宽高为2
        int widthHeight = arr.length == 9 ? 3 : 2;
        // 获取一个合成物品栏的对象，重写方法仅仅是为了代码不报错
        CraftingInventory craftingInventory = new CraftingInventory(new ScreenHandler(null, -1) {
            @Override
            public ItemStack quickMove(PlayerEntity player, int slot) {
                return ItemStack.EMPTY;
            }

            @Override
            public boolean canUse(PlayerEntity player) {
                return false;
            }
        }, widthHeight, widthHeight);
        // 设置物品栏中每一个物品为配方中的物品
        for (int i = 0; i < arr.length; i++) {
            craftingInventory.setStack(i, arr[i].getDefaultStack());
        }
        ServerCommandSource source = context.getSource();
        ServerWorld world = source.getWorld();
        // 获取配方的输出
        Optional<RecipeEntry<CraftingRecipe>> optional = source.getServer().getRecipeManager().getFirstMatch(RecipeType.CRAFTING, craftingInventory, world);
        if (optional.isEmpty()) {
            return Items.AIR;
        }
        return optional.get().value().craft(craftingInventory, world.getRegistryManager()).getItem();
    }
}
