package org.carpet_org_addition.util.fakeplayer;

import carpet.patches.EntityPlayerMPFake;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.command.ServerCommandSource;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.util.SendMessageUtils;
import org.carpet_org_addition.util.TextUtils;

public class FakePlayerUtils {

    /**
     * 槽位外部的索引，相当于点击GUI外面，用来丢弃光标上的物品
     */
    public static final int EMPTY_SPACE_SLOT_INDEX = ScreenHandler.EMPTY_SPACE_SLOT_INDEX;//-999
    /**
     * 模拟左键单击槽位
     */
    public static final int PICKUP_LEFT_CLICK = 0;
    /**
     * 模拟右键单击槽位
     */
    public static final int PICKUP_RIGHT_CLICK = 1;
    /**
     * 模拟按Q键丢弃物品
     */
    public static final int THROW_Q = 0;
    /**
     * 模拟Ctrl+Q丢弃物品
     */
    public static final int THROW_CTRL_Q = 1;

    /**
     * 工具类，私有化构造方法
     */
    private FakePlayerUtils() {
    }

    /**
     * 丢弃物品<br/>
     * 将要丢弃的物品堆栈对象复制一份并丢出，然后将原本的物品堆栈对象删除，这种方式丢弃物品不会更新比较器，如果是工作台的输出槽，也不会自动清空合成槽内的物品，因此，不建议使用本方法操作GUI
     *
     * @param player    当前要丢弃物品的玩家
     * @param itemStack 要丢弃的物品堆栈对象
     */
    public static void dropItem(EntityPlayerMPFake player, ItemStack itemStack) {
        player.dropItem(itemStack.copy(), false, false);
        itemStack.setCount(0);
    }

    /**
     * 模拟Ctrl+Q丢弃物品<br/>
     * 如果当前槽位索引为0，表示按Ctrl+Q丢弃0索引槽位的物品<br/>
     * 使用这种方式丢弃物品可以更新比较器，如果需要丢出一些功能方块输出槽位的物品，例如工作台或切石机的输出槽位，应该使用本方法，因为这会同时清除合成槽位的物品
     *
     * @param screenHandler 假玩家当前打开的GUI
     * @param slotIndex     假玩家当前操作槽位的索引
     * @param player        当前操作的假玩家
     */
    public static void throwItem(ScreenHandler screenHandler, int slotIndex, EntityPlayerMPFake player) {
        screenHandler.onSlotClick(slotIndex, THROW_CTRL_Q, SlotActionType.THROW, player);
    }

    /**
     * 与快捷栏中的物品交互位置<br/>
     * 如果当前操作的槽位索引为0，数字键为1，表示0索引槽位的物品与玩家1号快捷栏的物品交换位置
     *
     * @param screenHandler 假玩家当前打开的GUI
     * @param slotIndex     假玩家当前操作槽位的索引
     * @param key           模拟按下的数字键
     * @param player        当前操作的假玩家
     */
    @SuppressWarnings("unused")
    public static void swapItem(ScreenHandler screenHandler, int slotIndex, int key, EntityPlayerMPFake player) {
        screenHandler.onSlotClick(slotIndex, key, SlotActionType.SWAP, player);
    }

    /**
     * 让假玩家停止当前的操作
     *
     * @param source       发送消息的的消息源
     * @param playerMPFake 要停止操作的假玩家
     * @param key          停止操作时在聊天栏输出的内容的翻译键
     */
    public static void stopAction(ServerCommandSource source, EntityPlayerMPFake playerMPFake, String key, Object... obj) {
        ((FakePlayerActionInterface) playerMPFake).setAction(FakePlayerActionType.STOP);
        SendMessageUtils.broadcastTextMessage(source, TextUtils.appendAll(playerMPFake.getDisplayName(), ": ",
                TextUtils.getTranslate(key, obj)));
    }

    /**
     * 模拟按住Shift快速移动物品
     *
     * @param screenHandler 当前打开的GUI
     * @param slotIndex     要操作的槽位的索引
     * @param player        当前操作的玩家
     */
    public static void quickMove(ScreenHandler screenHandler, int slotIndex, EntityPlayerMPFake player) {
        screenHandler.quickMove(player, slotIndex);
    }

    /**
     * 模拟光标拾取并丢出物品，作用与{@link #throwItem(ScreenHandler, int, EntityPlayerMPFake)}模拟Ctrl+Q丢出物品类似，但是可能有些GUI的槽位不能使用Ctrl+Q丢弃物品，这时可以尝试使用本方法
     *
     * @param screenHandler 玩家当前打开的GUI
     * @param slotIndex     玩家当前操作的索引
     * @param player        当前操作GUI的假玩家
     */
    public static void pickupAndThrow(ScreenHandler screenHandler, int slotIndex, EntityPlayerMPFake player) {
        screenHandler.onSlotClick(slotIndex, PICKUP_LEFT_CLICK, SlotActionType.PICKUP, player);
        screenHandler.onSlotClick(EMPTY_SPACE_SLOT_INDEX, PICKUP_LEFT_CLICK, SlotActionType.PICKUP, player);
    }

    /**
     * 通过模拟光标拾取放置物品来快速移动物品，如果光标在拾取物品前有物品，则先丢弃该物品，用这种方式移动物品比模拟按住Shift键移动和使用数字键移动更加灵活，因为它可以在任意两个槽位之间移动，但是这种移动方式需要点击插槽两次，比另外两种略微浪费资源，有条件时也可以使用另外两种
     *
     * @param screenHandler 玩家当前打开的GUI
     * @param fromIndex     玩家拿取物品槽位的索引索引
     * @param player        当前操作GUI的假玩家
     */
    public static void pickupAndMoveItemStack(ScreenHandler screenHandler, int fromIndex, int toIndex, EntityPlayerMPFake player) {
        // 如果鼠标光标上有物品，先把光标上的物品丢弃
        if (!screenHandler.getCursorStack().isEmpty()) {
            screenHandler.onSlotClick(EMPTY_SPACE_SLOT_INDEX, PICKUP_LEFT_CLICK, SlotActionType.PICKUP, player);
        }
        screenHandler.onSlotClick(fromIndex, PICKUP_LEFT_CLICK, SlotActionType.PICKUP, player);
        //如果规则假玩家合成保留物品启用，并且该物品的最大堆叠数大于1，就在该槽位上放回一个物品
        if (CarpetOrgAdditionSettings.fakePlayerCraftKeepItem && screenHandler.getCursorStack().getMaxCount() > 1) {
            screenHandler.onSlotClick(fromIndex, PICKUP_RIGHT_CLICK, SlotActionType.PICKUP, player);
        }
        screenHandler.onSlotClick(toIndex, PICKUP_LEFT_CLICK, SlotActionType.PICKUP, player);
    }

    /**
     * 使用循环一个个丢弃槽位中的物品<br/>
     * 如果有些功能方块的输出槽位既不能使用Ctrl+Q丢弃，也不能使用鼠标拿起再丢弃，如切石机的输出槽，那么可以尝试使用本方法，使用时，应先确定确实不能使用上述两种方法进行丢弃，相比前面两种只需要一次操作的方法，本方法需要多次丢弃物品，这会更加消耗性能，增加游戏卡顿，因此，当前两种方法可用时，应使用前两种
     *
     * @param screenHandler 玩家当前打开的GUI
     * @param slotIndex     玩家当前操作槽位的索引
     * @param player        当前操作该GUI的玩家
     */
    public static void loopThrowItem(ScreenHandler screenHandler, int slotIndex, EntityPlayerMPFake player) {
        while (screenHandler.getSlot(slotIndex).hasStack()) {
            screenHandler.onSlotClick(slotIndex, THROW_Q, SlotActionType.THROW, player);
        }
    }
}