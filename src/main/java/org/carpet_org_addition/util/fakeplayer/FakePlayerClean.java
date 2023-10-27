package org.carpet_org_addition.util.fakeplayer;

import carpet.patches.EntityPlayerMPFake;
import net.minecraft.screen.ShulkerBoxScreenHandler;

public class FakePlayerClean {
    private FakePlayerClean() {
    }

    public static void clean(EntityPlayerMPFake fakePlayer) {
        //判断假玩家打开的界面是不是潜影盒的GUI
        if (fakePlayer.currentScreenHandler instanceof ShulkerBoxScreenHandler shulkerBoxScreenHandler) {
            for (int index = 0; index < 27; index++) {
                FakePlayerUtils.throwItem(shulkerBoxScreenHandler, index, fakePlayer);
            }
        }
    }
}
