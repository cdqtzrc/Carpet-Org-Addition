package org.carpet_org_addition.util.fakeplayer;

import carpet.patches.EntityPlayerMPFake;
import net.minecraft.screen.ShulkerBoxScreenHandler;

public class FakePlayerClean {
    private FakePlayerClean() {
    }

    public static void clean(EntityPlayerMPFake fakePlayer) {
        // TODO 清空容器不局限于潜影盒
        //判断假玩家打开的界面是不是潜影盒的GUI
        if (fakePlayer.currentScreenHandler instanceof ShulkerBoxScreenHandler shulkerBoxScreenHandler) {
            // 使用循环一个个丢出潜影盒中的物品
            for (int index = 0; index < 27; index++) {
                if (shulkerBoxScreenHandler.getSlot(index).hasStack()) {
                    FakePlayerUtils.throwItem(shulkerBoxScreenHandler, index, fakePlayer);
                }
            }
            // 物品全部丢出后自动关闭潜影盒
            fakePlayer.closeHandledScreen();
        }
    }
}
