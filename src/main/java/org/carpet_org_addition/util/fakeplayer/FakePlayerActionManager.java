package org.carpet_org_addition.util.fakeplayer;

import carpet.patches.EntityPlayerMPFake;
import org.carpet_org_addition.CarpetOrgAddition;
import org.carpet_org_addition.util.fakeplayer.actiondata.*;

public class FakePlayerActionManager {
    public static final String PLAYER_DATA = "player_data";
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
