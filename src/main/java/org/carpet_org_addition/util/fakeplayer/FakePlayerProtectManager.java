package org.carpet_org_addition.util.fakeplayer;

import carpet.patches.EntityPlayerMPFake;

/**
 * 假玩家保护管理器
 */
public class FakePlayerProtectManager {
    private FakePlayerProtectManager() {
    }

    /**
     * 假玩家是否不能被/player命令杀死
     *
     * @param player 要判断是否能被kill的假玩家
     * @return 假玩家能否被命令杀死
     */
    public static boolean isNotKill(EntityPlayerMPFake player) {
        return getProtectInterface(player).getProtect() == FakePlayerProtectType.KILL;
    }

    /**
     * 假玩家是否能受到来自虚空和玩家以外的伤害
     *
     * @param player 判断能否被伤害的假玩家
     * @return 假玩家能否受到伤害
     */
    public static boolean isNotDamage(EntityPlayerMPFake player) {
        return getProtectInterface(player).getProtect() == FakePlayerProtectType.DAMAGE;
    }

    /**
     * 假玩家是否不会死亡
     *
     * @param player 判断是否不会死亡的假玩家
     * @return 假玩家是否不会死亡
     */
    public static boolean isNotDeath(EntityPlayerMPFake player) {
        return getProtectInterface(player).getProtect() == FakePlayerProtectType.DEATH;
    }

    /**
     * 判断假玩家是否受保护，同时决定假玩家能否被/playerTools命令传送
     *
     * @param player 要判断是否受保护的假玩家
     * @return 该假玩家是否受保护
     */
    public static boolean isProtect(EntityPlayerMPFake player) {
        return getProtectInterface(player).getProtect() != FakePlayerProtectType.NONE;
    }

    /**
     * 获取假玩家保护接口对象
     *
     * @param player 假玩家保护接口的实现类对象，即假玩家对象
     * @return {@link FakePlayerProtectInterface}的实现类对象
     */
    private static FakePlayerProtectInterface getProtectInterface(EntityPlayerMPFake player) {
        return (FakePlayerProtectInterface) player;
    }

    /**
     * 设置假玩家的操作类型
     *
     * @param player      要设置操作类型的假玩家
     * @param protectType 要设置的操作类型
     * @return 是否设置成功
     */
    public static boolean setProtect(EntityPlayerMPFake player, FakePlayerProtectType protectType) {
        FakePlayerProtectInterface protectedPlayer = getProtectInterface(player);
        if (protectedPlayer.getProtect() != protectType) {
            protectedPlayer.setProtected(protectType);
            return true;
        }
        return false;
    }

    /**
     * 获取假玩家的保护类型
     *
     * @param player 要获取保护类型的假玩家
     * @return 假玩家的保护类型
     */
    public static FakePlayerProtectType getProtect(EntityPlayerMPFake player) {
        return getProtectInterface(player).getProtect();
    }
}
