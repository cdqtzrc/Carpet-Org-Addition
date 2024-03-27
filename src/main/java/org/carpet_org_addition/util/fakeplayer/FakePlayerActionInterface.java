package org.carpet_org_addition.util.fakeplayer;

import carpet.patches.EntityPlayerMPFake;

//假玩家动作接口
public interface FakePlayerActionInterface {
    FakePlayerActionManager getActionManager();

    // 获取假玩家动作管理器对象
    static FakePlayerActionManager getManager(EntityPlayerMPFake fakePlayer) {
        return ((FakePlayerActionInterface) fakePlayer).getActionManager();
    }
}
