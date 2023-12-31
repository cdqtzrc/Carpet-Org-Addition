package org.carpet_org_addition;

import carpet.api.settings.Rule;
import carpet.api.settings.RuleCategory;
import org.carpet_org_addition.rulevalidator.CheckBedrockHardness;
import org.carpet_org_addition.rulevalidator.CheckPortalSpawnZombifiedPiglinProbability;
import org.carpet_org_addition.rulevalidator.MaxBlockPlaceDistanceLegitimacyCheck;

public class CarpetOrgAdditionSettings {
    private CarpetOrgAdditionSettings() {
    }

    static {
        setBedrockHardness = -1;
        maxBlockPlaceDistance = -1;
        portalSpawnZombifiedPiglinProbability = -1;
    }

    public static final String ORG = "Org";

    //制作物品分身
    @Rule(
            categories = {ORG, RuleCategory.COMMAND},
            options = {"true", "false", "ops", "0", "1", "2", "3", "4"}
    )
    public static String commandItemShadowing = "ops";

    //设置基岩硬度
    @Rule(
            categories = {ORG},
            validators = {CheckBedrockHardness.class}
    )
    public static float setBedrockHardness;

    //绑定诅咒无效化
    @Rule(
            categories = {ORG, RuleCategory.SURVIVAL}
    )
    public static boolean bindingCurseInvalidation = false;

    //禁用钓鱼开阔水域检测
    @Rule(
            categories = {ORG, RuleCategory.SURVIVAL}
    )
    public static boolean disableOpenOrWaterDetection = false;

    //幽匿尖啸体放置时状态
    @Rule(
            categories = {ORG, RuleCategory.FEATURE}
    )
    public static boolean sculkShriekerCanSummon = false;

    //村民立即补货
    @Rule(
            categories = {ORG, RuleCategory.FEATURE}
    )
    public static boolean villagerImmediatelyRestock = false;

    //假玩家保护
    @Rule(
            categories = {ORG, RuleCategory.FEATURE}
    )
    public static boolean fakePlayerProtect = false;

    //保护玩家列表控制命令
    @Rule(
            categories = {ORG, RuleCategory.COMMAND},
            options = {"true", "false", "ops", "0", "1", "2", "3", "4"}
    )
    public static String commandProtect = "ops";

    //创造玩家免疫/kill
    @Rule(
            categories = {ORG, RuleCategory.CREATIVE}
    )
    public static boolean creativeImmuneKill = false;

    //掉落物不消失
    @Rule(
            categories = {ORG, RuleCategory.FEATURE}
    )
    public static boolean itemNeverDespawn = false;

    //荆棘不额外损耗耐久
    @Rule(
            categories = {ORG, RuleCategory.SURVIVAL, RuleCategory.FEATURE}
    )
    public static boolean thornsDamageDurability = false;

    //滑翔时不能对方块使用烟花
    @Rule(
            categories = {ORG, RuleCategory.SURVIVAL}
    )
    public static boolean flyingUseOnBlockFirework = false;

    //盯着末影人眼睛看时不会激怒末影人
    @Rule(
            categories = {ORG, RuleCategory.SURVIVAL, RuleCategory.FEATURE}
    )
    public static boolean staringEndermanNotAngry = false;

    //耕地防踩踏
    @Rule(
            categories = {ORG, RuleCategory.SURVIVAL, RuleCategory.FEATURE}
    )
    public static boolean farmlandPreventStepping = false;

    //凋零骷髅可以生成在传送门方块
    @Rule(
            categories = {ORG, RuleCategory.SURVIVAL}
    )
    public static boolean witherSkeletonCanSpawnToPortal = false;

    //最大方块交互距离
    @Rule(
            categories = {ORG, RuleCategory.SURVIVAL, RuleCategory.FEATURE},
            validators = {MaxBlockPlaceDistanceLegitimacyCheck.class}
    )
    public static double maxBlockPlaceDistance;

    //简易更新跳略器
    @Rule(
            categories = {ORG, RuleCategory.FEATURE}
    )
    public static boolean simpleUpdateSkipper = false;

    //强化引雷
    @Rule(
            categories = {ORG, RuleCategory.FEATURE}
    )
    public static boolean channelingIgnoreWeather = false;

    //破坏冰时总是变成水
    @Rule(
            categories = {ORG, RuleCategory.FEATURE}
    )
    public static boolean iceBreakPlaceWater = false;

    //无伤末影珍珠
    @Rule(
            categories = {ORG, RuleCategory.SURVIVAL}
    )
    public static boolean notDamageEnderPearl = false;

    //保护类魔咒兼容
    @Rule(
            categories = {ORG, RuleCategory.FEATURE}
    )
    public static boolean protectionEnchantmentCompatible = false;

    //禁用伤害免疫
    @Rule(
            categories = {ORG, RuleCategory.FEATURE}
    )
    public static boolean disableDamageImmunity = false;

    //禁止传送门更新
    @Rule(
            categories = {ORG, RuleCategory.CREATIVE}
    )
    public static boolean disablePortalUpdate = false;

    //干草捆完全抵消摔落伤害
    @Rule(
            categories = {ORG, RuleCategory.FEATURE}
    )
    public static boolean hayBlockCompleteOffsetFall = false;

    //蓝冰上不能刷怪
    @Rule(
            categories = {ORG, RuleCategory.FEATURE}
    )
    public static boolean blueIceCanSpawn = false;

    //禁止蝙蝠生成
    @Rule(
            categories = {ORG, RuleCategory.FEATURE}
    )
    public static boolean disableBatCanSpawn = false;

    //海龟蛋快速孵化
    @Rule(
            categories = {ORG, RuleCategory.FEATURE}
    )
    public static boolean turtleEggFastHatch = false;

    //大范围信标
    @Rule(
            categories = {ORG, RuleCategory.SURVIVAL}
    )
    public static boolean wideRangeBeacon = false;

    //禁止海带生长
    @Rule(
            categories = {ORG, RuleCategory.FEATURE}
    )
    public static boolean disableKelpGrow = false;

    //强制开启潜影盒
    @Rule(
            categories = {ORG, RuleCategory.SURVIVAL}
    )
    public static boolean openShulkerBoxForcibly = false;

    //村民无限交易
    @Rule(
            categories = {ORG, RuleCategory.SURVIVAL}
    )
    public static boolean villagerInfiniteTrade = false;

    //烟花火箭使用冷却
    @Rule(
            categories = {ORG, RuleCategory.FEATURE}
    )
    public static boolean fireworkRocketUseCooldown = false;

    //强化激流
    @Rule(
            categories = {ORG, RuleCategory.SURVIVAL}
    )
    public static boolean riptideIgnoreWeather = false;

    //禁止猪灵僵尸化
    @Rule(
            categories = {ORG, RuleCategory.FEATURE}
    )
    public static boolean disablePiglinZombify = false;

    //猪灵快速交易
    @Rule(
            categories = {ORG, RuleCategory.FEATURE}
    )
    public static boolean piglinFastBarter = false;

    //禁止村民女巫化
    @Rule(
            categories = {ORG, RuleCategory.FEATURE}
    )
    public static boolean disableVillagerWitch = false;

    //禁止铁傀儡攻击玩家
    @Rule(
            categories = {ORG, RuleCategory.FEATURE}
    )
    public static boolean disableIronGolemAttackPlayer = false;

    //将镐作为基岩的有效采集工具
    @Rule(
            categories = {ORG, RuleCategory.FEATURE}
    )
    public static boolean pickaxeMinedBedrock = false;

    //雪傀儡不融化
    @Rule(
            categories = {ORG, RuleCategory.FEATURE}
    )
    public static boolean disableSnowGolemMelts = false;

    //村民回血
    @Rule(
            categories = {ORG, RuleCategory.FEATURE}
    )
    public static boolean villagerHeal = false;

    //假玩家回血
    @Rule(
            categories = {ORG, RuleCategory.FEATURE}
    )
    public static boolean fakePlayerHeal = false;

    //伤害附魔兼容
    @Rule(
            categories = {ORG, RuleCategory.FEATURE}
    )
    public static boolean damageEnchantmentCompatible = false;

    //假玩家工具命令
    @Rule(
            categories = {ORG, RuleCategory.COMMAND},
            options = {"true", "false", "ops", "0", "1", "2", "3", "4"}
    )
    public static String commandPlayerTools = "ops";

    //禁止岩浆怪生成于下界荒地
    @Rule(
            categories = {ORG, RuleCategory.FEATURE}
    )
    public static boolean disableMagmaCubeSpawnNetherWastes = false;

    //最大方块交互距离适用于实体
    @Rule(
            categories = {ORG, RuleCategory.FEATURE}
    )
    public static boolean maxBlockPlaceDistanceReferToEntity = false;

    //可再生迅捷潜行
    @Rule(
            categories = {ORG, RuleCategory.FEATURE}
    )
    public static boolean renewableSwiftSneak = false;

    //传送门生成僵尸猪灵概率
    @Rule(
            categories = {ORG, RuleCategory.FEATURE},
            validators = {CheckPortalSpawnZombifiedPiglinProbability.class}
    )
    public static int portalSpawnZombifiedPiglinProbability;

    //击退棒
    @Rule(
            categories = {ORG, RuleCategory.FEATURE}
    )
    public static boolean knockbackStick = false;

    //禁止重生方块爆炸
    @Rule(
            categories = {ORG, RuleCategory.FEATURE}
    )
    public static boolean disableRespawnBlocksExplode = false;

    //CCE更新抑制器
    @Rule(
            categories = {ORG, RuleCategory.FEATURE}
    )
    public static boolean CCEUpdateSuppression = false;

    //开放/seed命令权限
    @Rule(
            categories = {ORG, RuleCategory.COMMAND}
    )
    public static boolean openSeedPermissions = false;

    //发送消息命令
    @Rule(
            categories = {ORG, RuleCategory.COMMAND},
            options = {"true", "false", "ops", "0", "1", "2", "3", "4"}
    )
    public static String commandSendMessage = "ops";

    //开放/carpet命令权限
    @Rule(
            categories = {ORG, RuleCategory.COMMAND, RuleCategory.CLIENT}
    )
    public static boolean openCarpetPermissions = false;

    //开放/gamerule命令权限
    @Rule(
            categories = {ORG, RuleCategory.COMMAND}
    )
    public static boolean openGameRulePermissions = false;

    //打开村民物品栏
    @Rule(
            categories = {ORG, RuleCategory.FEATURE}
    )
    public static boolean openVillagerInventory = false;
/*
    //播放苦力怕音效
    @Rule(
            categories = {ORG, RuleCategory.COMMAND},
            options = {"true", "false", "ops", "0", "1", "2", "3", "4"}
    )
    public static String commandCreeper = "ops";*/

    //和平的苦力怕
    @Rule(
            categories = {ORG, RuleCategory.SURVIVAL}
    )
    public static boolean peacefulCreeper = false;

    //经验转移
    @Rule(
            categories = {ORG, RuleCategory.COMMAND},
            options = {"true", "false", "ops", "0", "1", "2", "3", "4"}
    )
    public static String commandXpTransfer = "ops";

    //生存旁观切换命令
    @Rule(
            categories = {ORG, RuleCategory.COMMAND},
            options = {"true", "false", "ops", "0", "1", "2", "3", "4"}
    )
    public static String commandSpectator = "ops";

    //方块查找器命令
    @Rule(
            categories = {ORG, RuleCategory.COMMAND},
            options = {"true", "false", "ops", "0", "1", "2", "3", "4"}
    )
    public static String commandFinder = "ops";

    //自杀
    @Rule(
            categories = {ORG, RuleCategory.COMMAND},
            options = {"true", "false", "ops", "0", "1", "2", "3", "4"}
    )
    public static String commandKillMe = "ops";

    //路径点管理器
    @Rule(
            categories = {ORG, RuleCategory.COMMAND},
            options = {"true", "false", "ops", "0", "1", "2", "3", "4"}
    )
    public static String commandLocations = "ops";

    //生命恢复附带饱和
    @Rule(
            categories = {ORG, RuleCategory.SURVIVAL}
    )
    public static boolean regenerationSaturation = false;

    //生命值不满可以进食
    @Rule(
            categories = {ORG, RuleCategory.SURVIVAL}
    )
    public static boolean healthNotFullCanEat = false;

    //可采集刷怪笼
    @Rule(
            categories = {ORG, RuleCategory.SURVIVAL}
    )
    public static boolean canMineSpawner = false;

    //假玩家生成时不保留击退
    @Rule(
            categories = {ORG, RuleCategory.FEATURE}
    )
    public static boolean fakePlayerSpawnNotRetainKnockback = false;

    //可激活侦测器
    @Rule(
            categories = {ORG, RuleCategory.FEATURE, RuleCategory.SURVIVAL}
    )
    public static boolean canActivatesObserver = false;

    //可解析路径点
    @Rule(
            categories = {ORG, RuleCategory.SURVIVAL}
    )
    public static boolean canParseWayPoint = false;

    //禁用聊天数据包顺序检查
    @Rule(
            categories = {ORG, RuleCategory.SURVIVAL}
    )
    public static boolean disableOutOfOrderChatCheck = false;

    //禁止水结冰
    @Rule(
            categories = {ORG, RuleCategory.FEATURE}
    )
    public static boolean disableWaterFreezes = false;

    //假玩家合成保留物品
    @Rule(
            categories = {ORG, RuleCategory.SURVIVAL}
    )
    public static boolean fakePlayerCraftKeepItem = false;

    //绘制粒子线命令
    @Rule(
            categories = {ORG, RuleCategory.COMMAND},
            options = {"true", "false", "ops", "0", "1", "2", "3", "4"}
    )
    public static String commandParticleLine = "true";// 默认为true，方便无权限玩家调用

    // 禁止特定生物在和平模式下被清除
    @Rule(
            categories = {ORG, RuleCategory.FEATURE}
    )
    public static boolean disableMobPeacefulDespawn = false;

    // 船可以直接走向一格高的方块
    @Rule(
            categories = {ORG, RuleCategory.FEATURE}
    )
    public static boolean climbingBoat = false;
}
