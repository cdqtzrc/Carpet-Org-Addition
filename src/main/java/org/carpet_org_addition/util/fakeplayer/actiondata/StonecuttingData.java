package org.carpet_org_addition.util.fakeplayer.actiondata;

import carpet.patches.EntityPlayerMPFake;
import com.google.gson.JsonObject;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.screen.StonecutterScreenHandler;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.matcher.Matcher;

import java.util.ArrayList;

public class StonecuttingData extends AbstractActionData {
    private static final String ITEM = "item";
    private static final String BUTTON = "button";
    /**
     * 要使用切石机切制的物品
     */
    private final Item item;
    /**
     * 切石机内按钮的索引
     */
    private final int button;

    public StonecuttingData(Item item, int button) {
        this.item = item;
        this.button = button;
    }

    public static StonecuttingData load(JsonObject json) {
        Item item = Matcher.asItem(json.get(ITEM).getAsString());
        int index = json.get(BUTTON).getAsInt();
        return new StonecuttingData(item, index);
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty(ITEM, Registries.ITEM.getId(item).toString());
        json.addProperty(BUTTON, this.button);
        return json;
    }

    @Override
    public ArrayList<MutableText> info(EntityPlayerMPFake fakePlayer) {
        // 创建一个物品栏对象用来获取配方的输出物品
        SimpleInventory simpleInventory = new SimpleInventory(1);
        // 获取要切制的材料物品
        ItemStack inputItemStack = this.item.getDefaultStack();
        simpleInventory.setStack(0, inputItemStack);
        // 获取假玩家所在的世界对象
        World world = fakePlayer.getWorld();
        ItemStack outputItemStack;
        try {
            // 获取与材料和按钮索引对应的配方对象
            StonecuttingRecipe stonecuttingRecipe = world.getRecipeManager().getAllMatches(RecipeType.STONECUTTING,
                    simpleInventory, world).get(button).value();
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
        list.add(TextUtils.getTranslate("carpet.commands.playerAction.info.stonecutting.item",
                fakePlayer.getDisplayName(), Items.STONECUTTER.getName(),
                this.item.getDefaultStack().toHoverableText(), itemName));
        if (fakePlayer.currentScreenHandler instanceof StonecutterScreenHandler stonecutterScreenHandler) {
            // 将按钮索引的信息添加到集合，按钮在之前减去了1，这里再加回来
            list.add(TextUtils.getTranslate("carpet.commands.playerAction.info.stonecutting.button",
                    (button + 1)));
            // 将切石机当前的状态的信息添加到集合
            list.add(TextUtils.appendAll("    ",
                    getWithCountHoverText(stonecutterScreenHandler.getSlot(0).getStack()), " -> ",
                    getWithCountHoverText(stonecutterScreenHandler.getSlot(1).getStack())));
        } else {
            // 将假玩家没有打开切石机的消息添加到集合
            list.add(TextUtils.getTranslate("carpet.commands.playerAction.info.stonecutting.no_stonecutting",
                    fakePlayer.getDisplayName(), Items.STONECUTTER.getName()));
        }
        return list;
    }

    public Item getItem() {
        return item;
    }

    public int getButton() {
        return button;
    }
}
