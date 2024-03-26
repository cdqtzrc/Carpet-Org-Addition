package org.carpet_org_addition.util.fakeplayer;

import carpet.patches.EntityPlayerMPFake;

//假玩家动作接口
public interface FakePlayerActionInterface {
    FakePlayerActionManager getActionManager();

    // 将假玩家类型强转为假玩家动作接口类型
    static FakePlayerActionInterface getInstance(EntityPlayerMPFake fakePlayer) {
        return (FakePlayerActionInterface) fakePlayer;
    }

    // 获取假玩家动作管理器对象
    static FakePlayerActionManager getManager(EntityPlayerMPFake fakePlayer) {
        return getInstance(fakePlayer).getActionManager();
    }
}
