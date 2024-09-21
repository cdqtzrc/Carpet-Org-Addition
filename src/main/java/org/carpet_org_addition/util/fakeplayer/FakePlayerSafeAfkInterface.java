package org.carpet_org_addition.util.fakeplayer;

public interface FakePlayerSafeAfkInterface {

    /**
     * 如果假玩家当前血量低于阈值，假玩家将立即退出游戏<br/>
     * 如果玩家血量小于0，则假玩家不会主动退出游戏
     */
    void setHealthThreshold(float threshold);
}
