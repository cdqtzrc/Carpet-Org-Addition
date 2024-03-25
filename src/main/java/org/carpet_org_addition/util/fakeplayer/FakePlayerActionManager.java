package org.carpet_org_addition.util.fakeplayer;

import carpet.patches.EntityPlayerMPFake;
import com.google.gson.JsonObject;
import org.carpet_org_addition.CarpetOrgAddition;
import org.carpet_org_addition.util.fakeplayer.actiondata.*;
import org.carpet_org_addition.util.helpers.JsonSerial;

public class FakePlayerActionManager implements JsonSerial {
    private final EntityPlayerMPFake fakePlayer;

    private final ActionFunction function = new ActionFunction();

    public FakePlayerActionManager(EntityPlayerMPFake fakePlayer) {
        this.fakePlayer = fakePlayer;
    }

    public void executeAction() {
        switch (function.getAction()) {
            case STOP -> {
            }
            case SORTING -> FakePlayerSorting.sorting((SortingData) function.getActionData(), fakePlayer);
            case CLEAN, CLEAN_DESIGNATED -> FakePlayerClean.clean((CleanData) function.getActionData(), fakePlayer);
            case FILL, FILL_ALL -> FakePlayerFill.fill((FillData) function.getActionData(), fakePlayer);
            case CRAFT_ONE, CRAFT_FOUR, CRAFT_2X2 ->
                    FakePlayerCraft.craft2x2((InventoryCraftData) function.getActionData(), fakePlayer);
            case CRAFT_NINE, CRAFT_3X3 ->
                    FakePlayerCraft.craft3x3((CraftingTableCraftData) function.getActionData(), fakePlayer);
            case RENAME -> FakePlayerRename.rename((RenameData) function.getActionData(), fakePlayer);
            case STONECUTTING ->
                    FakePlayerStonecutting.stonecutting((StonecuttingData) function.actionData, fakePlayer);
            case TRADE, VOID_TRADE -> FakePlayerTrade.trade((TradeData) function.actionData, fakePlayer);
            default -> {
                CarpetOrgAddition.LOGGER.error(this.function.getAction() + "的行为没有预先定义");
                this.function.setAction(FakePlayerAction.STOP, StopData.STOP);
            }
        }
    }

    public FakePlayerAction getAction() {
        return function.getAction();
    }

    public AbstractActionData getActionData() {
        return this.function.actionData;
    }

    public void setAction(FakePlayerAction action, AbstractActionData data) {
        this.function.setAction(action, data);
    }

    public static void load(EntityPlayerMPFake fakePlayer, JsonObject json) {
        FakePlayerActionManager actionManager = FakePlayerActionInterface.getInstance(fakePlayer).getActionManager();
        try {
            if (json.has("stop")) {
                actionManager.setAction(FakePlayerAction.STOP, StopData.STOP);
            } else if (json.has("sorting")) {
                actionManager.setAction(FakePlayerAction.SORTING, SortingData.load(json.get("sorting").getAsJsonObject()));
            } else if (json.has("clean")) {
                actionManager.setAction(FakePlayerAction.CLEAN, CleanData.load(json.get("clean").getAsJsonObject()));
            } else if (json.has("fill")) {
                actionManager.setAction(FakePlayerAction.FILL, FillData.load(json.get("fill").getAsJsonObject()));
            } else if (json.has("inventory_crafting")) {
                actionManager.setAction(FakePlayerAction.CRAFT_2X2, InventoryCraftData.load(json.get("inventory_crafting").getAsJsonObject()));
            } else if (json.has("crafting_table_craft")) {
                actionManager.setAction(FakePlayerAction.CRAFT_3X3, CraftingTableCraftData.load(json.get("crafting_table_craft").getAsJsonObject()));
            } else if (json.has("rename")) {
                actionManager.setAction(FakePlayerAction.RENAME, RenameData.load(json.get("rename").getAsJsonObject()));
            } else if (json.has("stonecutting")) {
                actionManager.setAction(FakePlayerAction.STONECUTTING, StonecuttingData.load(json.get("stonecutting").getAsJsonObject()));
            } else if (json.has("trade")) {
                actionManager.setAction(FakePlayerAction.TRADE, TradeData.load(json.get("trade").getAsJsonObject()));
            }
        } catch (RuntimeException e) {
            actionManager.setAction(FakePlayerAction.STOP, StopData.STOP);
        }
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        String action = switch (this.getAction()) {
            case STOP -> "stop";
            case SORTING -> "sorting";
            case CLEAN, CLEAN_DESIGNATED -> "clean";
            case FILL, FILL_ALL -> "fill";
            case CRAFT_ONE, CRAFT_FOUR, CRAFT_2X2 -> "inventory_crafting";
            case CRAFT_NINE, CRAFT_3X3 -> "crafting_table_craft";
            case RENAME -> "rename";
            case STONECUTTING -> "stonecutting";
            case TRADE, VOID_TRADE -> "trade";
        };
        json.add(action, this.getActionData().toJson());
        return json;
    }

    /**
     * 将动作类型和动作数据封装起来，保证类型与数据对应
     */
    private static class ActionFunction {
        private FakePlayerAction action = FakePlayerAction.STOP;
        private AbstractActionData actionData = StopData.STOP;

        // 动作类型必须和动作数据一起修改来保证类型与数据对应
        public void setAction(FakePlayerAction action, AbstractActionData actionData) {
            // 检查动作类型是否与数据匹配
            action.checkActionData(actionData.getClass());
            this.action = action;
            this.actionData = actionData;
        }

        public FakePlayerAction getAction() {
            return action;
        }

        public AbstractActionData getActionData() {
            return actionData;
        }
    }
}
