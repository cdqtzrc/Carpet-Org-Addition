# Carpet-Org-Addition

## 规则

制作物品分身(commandItemShadowing)

- 启用命令/itemshadowing以制作物品分身
    - 类型：`字符串`
    - 默认值：`"ops"`
    - 参考选项：`"true"`, `“false”`, `"ops"`, `"0"`, `"1"`, `"2"`,
      `"3"`, `"4"`
    - 分类：`Org`，`指令`

设置基岩硬度(setBedrockHardness)

- 将基岩硬度设置为指定值
- 值必须大于等于0，或者为-1
- 需要客户端支持
    - 类型：`单精度浮点数`
    - 默认值：`-1`
    - 分类：`Org`

绑定诅咒无效化(bindingCurseInvalidation)

- 启用后玩家可以取下带有绑定诅咒附魔的装备
- 需要客户端支持
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`生存`

禁用钓鱼开放水域检测(disableOpenOrWaterDetection)

- 启用后玩家可以在封闭水域中钓鱼并获得宝藏
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`生存`

幽匿尖啸体可生成监守者(sculkShriekerCanSummon)

- 启用后玩家手动放置的幽匿尖啸体默认可以生成监守者
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

村民立即补货(villagerImmediatelyRestock)

- 启用后每次与村民交互前村民会进行一次补货
- 每次与村民交易完毕后可以重新打开交易GUI来进行补货
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

假玩家保护命令(commandProtect)

- 启用/protect命令用来保护特定的假玩家
- damage：保护假玩家不受到直接来自玩家和虚空以外的伤害
- death：保护假玩家不被直接来自玩家和虚空以外的伤害杀死
- kill：保护假玩家不被/player命令杀死
    - 类型：`字符串`
    - 默认值：`"false"`
    - 参考选项：`"true"`, `“false”`, `"ops"`, `"0"`, `"1"`, `"2"`,
      `"3"`, `"4"`
    - 分类：`Org`，`指令`

创造玩家免疫/kill(creativeImmuneKill)

- 启用后命令/kill不能杀死创造模式的玩家
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`创造`

掉落物不消失(itemNeverDespawn)

- 启用后掉落物会一直保存在世界中
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

滑翔时不能对方块使用烟花(flyingUseOnBlockFirework)

- 使用鞘翅飞行时，烟花火箭只能用来飞行
- 需要客户端支持
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

注视末影人眼睛时不会激怒末影人(staringEndermanNotAngry)

- 末影人不会因为玩家盯着它的眼睛而被激怒，即便玩家没有佩戴雕刻南瓜
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`生存`，`特性`

耕地防踩踏(farmlandPreventStepping)

- 耕地不会因为生物踩踏而退化成泥土
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`生存`，`特性`

凋零骷髅可生成在传送门方块(witherSkeletonCanSpawnToPortal)

- 凋零骷髅可以生成下界传送门方块或其他亮度不高于11的方块上
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`生存`

最大方块放置距离(maxBlockPlaceDistance)

- 服务器不会拒绝在此交互范围内的操作
- 对方块的破坏和部分方块的交互同样有效，但不增加容器的交互距离
    - 类型：`双精度浮点数`
    - 默认值：`-1`
    - 分类：`Org`，`生存`，`特性`

简易更新跳略器(simpleUpdateSkipper)

- 通过让红石线不连接四周开启的活板门上的红石线来恢复1.20-pre2前的更新跳略器
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

强化引雷(ChannelingIgnoreWeather)

- 三叉戟引雷时忽略天气
- 依然会受其他条件限制，如维度，是否露天等
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

无伤末影珍珠(notDamageEnderPearl)

- 使用末影珍珠不会受到摔落伤害
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

保护类魔咒兼容(protectionEnchantmentCompatible)

- 所有的保护类附魔可以共存
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

禁用伤害免疫(disableDamageImmunity)

- 阻止游戏限制生物受伤频率
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

禁止传送门更新(disablePortalUpdate)

- 下界传送门方块收到方块更新后不会做出反应
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`创造`

干草捆完全抵消摔落伤害(hayBlockCompleteOffsetFall)

- 落在干草捆上时不会受到摔落伤害
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

蓝冰上不能刷怪(blueIceCanSpawn)

- 生物不能在蓝冰上生成
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

禁止蝙蝠生成(disableBatCanSpawn)

- 阻止蝙蝠自然生成
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

海龟蛋快速孵化(turtleEggFastHatch)

- 海龟蛋即使在白天也可以快速孵化
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

禁止海带生长(disableKelpGrow)

- 阻止海带自然生长
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

强制开启潜影盒(openShulkerBoxForcibly)

- 允许玩家打开被阻挡的潜影盒
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`生存`

村民无限交易(villagerInfiniteTrade)

- 与村民交易时不会增加交易次数
- 与村民立即补货的区别是，这不需要重新打开村民交易GUI
- 需要客户端支持
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`生存`

烟花火箭使用冷却(fireworkRocketUseCooldown)

- 使用烟花火箭后有5个游戏刻的冷却时间
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

强化激流(riptideIgnoreWeather)

- 使用激流时忽略天气
- 忽略天气，也忽略使用激流的其他条件
- 需要客户端支持
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

禁止猪灵僵尸化(disablePiglinZombify)

- 猪灵在下界以外的维度时不会变成僵尸猪灵
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

禁止村民女巫化(disableVillagerWitch)

- 村民被闪电击中后不会变成女巫
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

禁止铁傀儡攻击玩家(disableIronGolemAttackPlayer)

- 铁傀儡永远不会试图攻击玩家，即使这只铁傀儡不是玩家建造的
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

将镐作为基岩的有效采集工具(pickaxeMinedBedrock)

- 使用镐可以更快速的破坏基岩
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

村民回血(villagerHeal)

- 村民每4秒回复1生命值
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

假玩家回血(fakePlayerHeal)

- 假玩家每2秒回复1生命值
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

伤害附魔兼容(damageEnchantmentCompatible)

- 所有的伤害类附魔可以共存
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

假玩家工具命令(commandPlayerTools)

- 提供一些假玩家相关的辅助工具
    - 类型：`字符串`
    - 默认值：`"ops"`
    - 参考选项：`"true"`, `“false”`, `"ops"`, `"0"`, `"1"`, `"2"`,
      `"3"`, `"4"`
    - 分类：`Org`，`命令`

禁止岩浆怪生成在下界荒地(disableMagmaCubeSpawnNetherWastes)

- 岩浆怪不会在下界荒地生成
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

最大方块放置距离适用于实体(maxBlockPlaceDistanceReferToEntity)

- 玩家可以与最大方块交互距离内的实体交互
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

可再生迅捷潜行(renewableSwiftSneak)

- 图书管理员可以交易迅捷潜行附魔书
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

传送门生成僵尸猪灵概率(portalSpawnZombifiedPiglinProbability)

- 下界传送门方块接收到随机刻后有指定值/2000的概率生成僵尸猪灵
- 值必须介于0-2000之间，或者-1
    - 类型：`整数`
    - 默认值：`-1`
    - 分类：`Org`，`特性`

击退棒(knockbackStick)

- 启用后可以给木棍附魔击退
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

禁止重生方块爆炸(disableRespawnBlocksExplode)

- 玩家在非主世界维度使用床或在非下界维度使用重生锚时不会爆炸
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

CCE更新抑制器(CCEUpdateSuppression)

- "通过将潜影盒命名为“更新抑制器”或“updateSuppression”来制作基于"ClassCastException"的更新抑制器"
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

开放/seed命令权限(openSeedPermissions)

- 允许无权限玩家在服务器使用/seed命令
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`命令`

发送可复制文本命令(commandSendMessage)

- 可以让玩家发送一些可以复制的文本、链接，以及自己的位置
    - 类型：`字符串`
    - 默认值：`"ops"`
    - 参考选项：`"true"`, `“false”`, `"ops"`, `"0"`, `"1"`, `"2"`,
      `"3"`, `"4"`
    - 分类：`Org`，`命令`

开放/carpet命令权限(openCarpetPermissions)

- 仅在单人游戏有效
- 允许玩家在无作弊的单人游戏中使用/carpet命令
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`命令`，`客户端`

开放/gamerule命令权限(openGameRulePermissions)

- 允许无权限玩家使用/gamerule命令
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`命令`

打开村民物品栏(openVillagerInventory)

- 在潜行时对村民右键可以打开村民物品栏
- 因为村民只有8格物品栏，所以在打开GUI中，第九个格子不会存储物品，玩家在关闭GUI时物品会自动丢出
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

和平的苦力怕(peacefulCreeper)

- 苦力怕不会与玩家敌对
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`生存`

经验转移(commandXpTransfer)

- 启用/xpTransfer命令用来在玩家间互相分享经验
    - 类型：`字符串`
    - 默认值：`"ops"`
    - 参考选项：`"true"`, `“false”`, `"ops"`, `"0"`, `"1"`, `"2"`,
      `"3"`, `"4"`
    - 分类：`Org`，`命令`

生存旁观切换命令(commandSpectator)

- 允许玩家在生存模式和旁观模式间切换
    - 类型：`字符串`
    - 默认值：`"ops"`
    - 参考选项：`"true"`, `“false”`, `"ops"`, `"0"`, `"1"`, `"2"`,
      `"3"`, `"4"`
    - 分类：`Org`，`命令`

查找器(commandFinder)

- 启用/finder命令用来在指定范围内查找指定方块或物品
    - 类型：`字符串`
    - 默认值：`"ops"`
    - 参考选项：`"true"`, `“false”`, `"ops"`, `"0"`, `"1"`, `"2"`,
      `"3"`, `"4"`
    - 分类：`Org`，`命令`

自杀命令(commandKillMe)

- 启用/killMe命令用来自杀
    - 类型：`字符串`
    - 默认值：`"ops"`
    - 参考选项：`"true"`, `“false”`, `"ops"`, `"0"`, `"1"`, `"2"`,
      `"3"`, `"4"`
    - 分类：`Org`，`命令`

路径点管理器(commandLocations)

- 启用/locations命令用来管理路径点
    - 类型：`字符串`
    - 默认值：`"ops"`
    - 参考选项：`"true"`, `“false”`, `"ops"`, `"0"`, `"1"`, `"2"`,
      `"3"`, `"4"`
    - 分类：`Org`，`命令`

血量不满时可进食(healthNotFullCanEat)

- 血量没有完全恢复并且饱和度小于等于5时可以进食
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`生存`

可采集刷怪笼(canMineSpawner)

- 刷怪笼可以使用精准采集工具采集
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`生存`

假玩家生成无击退(fakePlayerSpawnNoKnockback)

- 假玩家生成时不会保留上一次的击退、着火时间，摔落距离
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

可解析路径点(canParseWayPoint)

- 通过移除路径点中的交互事件来让OMMC可以解析聊天中的路径点
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`生存`

禁用无序聊天数据包检查(disableOutOfOrderChatCheck)

- 玩家发送无序的聊天数据包时不会被踢出游戏
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`生存`

禁止水结冰(disableWaterFreezes)

- 在寒冷的生物群系中水不会结冰
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

假玩家合成保留物品(fakePlayerCraftKeepItem)

- 假玩家合成物品时会在物品栏中保留至少一个合成材料，除非该物品的最大堆叠数为1
- 对假玩家自动交易同样有效
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`生存`

绘制粒子线命令(commandParticleLine)

- 启用/particleLine命令用来使用粒子效果来绘制连接两点的线
    - 类型：`字符串`
    - 默认值：`"true"`
    - 参考选项：`"true"`, `“false”`, `"ops"`, `"0"`, `"1"`, `"2"`,
      `"3"`, `"4"`
    - 分类：`Org`，`指令`

禁止和平模式下持久生物被清除(disableMobPeacefulDespawn)

- 通过命名或其他方式获得PersistenceRequired标签的生物在和平模式下也不会消失
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

登山船(climbingBoat)

- 玩家驾驶的船可以直接爬上一格高的方块
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

可重复使用的锻造模板(reusableSmithingTemplate)

- 在锻造台合成物品不会消耗锻造模板
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`生存`

开放/tp命令权限

- 允许无权限玩家使用/tp命令
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`命令`

易碎深板岩(softDeepslate)

- 启用后深板岩及其变种与石头或圆石有相同的硬度
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`生存`

易碎黑曜石(softObsidian)

- 启用后黑曜石与末地石有相同的硬度
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`生存`

易碎矿石(softOres)

- 启用后矿石与对应的石头，深板岩或下界岩有相同的硬度
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`生存`

更好的不死图腾(betterTotemOfUndying)

- 玩家死亡时只要物品栏内有不死图腾就能生效，而不需要放在手上
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`生存`

假玩家动作命令(commandPlayerAction)

- 启用/playerAction命令用来让假玩家自动执行一些动作
    - 类型：`字符串`
    - 默认值：`"ops"`
    - 参考选项：`"true"`, `“false”`, `"ops"`, `"0"`, `"1"`, `"2"`,
      `"3"`, `"4"`
    - 分类：`Org`，`指令`

假玩家合成从潜影盒取物(fakePlayerCraftPickItemFromShulkerBox)

- 假玩家合成物品时支持从潜影盒中拿取物品
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`生存`

自定义猪灵交易时间(customPiglinBarteringTime)

- 将猪灵默认以物易物所需的时间设为指定值
    - 类型：`长整数`
    - 默认值：`-1`
    - 分类：`Org`，`生存`

快速设置假玩家合成(quickSettingFakePlayerCraft)

- 手持工作台对假玩家右键直接打开假玩家合成GUI，要求玩家拥有执行/playerAction命令的权限
- false：禁用本条规则
- sneaking：只有在潜行时才能右键打开
- true：总是可以右键打开
    - 类型：`枚举`
    - 默认值：`false`
    - 参考选项：`true`，`sneaking`，`false`
    - 分类：`Org`，`生存`

湿海绵立即蒸干(wetSpongeImmediatelyDry)

- 指定湿海绵是否在下界以外的干旱的生物群系里也可以立即蒸干
- false：禁用本条规则
- arid：在不会降水的生物群系可以立即蒸干
- all：在所有群系都可以立即蒸干
- disable：湿海绵即使在下界也不会立即蒸干
    - 类型：`枚举`
    - 默认值：`false`
    - 参考选项：`false`，`arid`，`all`，`disable`
    - 分类：`Org`，`生存`

假玩家死亡不掉落(fakePlayerKeepInventory)

- 假玩家死亡后保留物品栏物品和经验
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`生存`

苦力怕命令(commandCreeper)

- 启用/creeper命令用来在指定玩家周围生成一只立即爆炸但不会破坏方块的苦力怕
    - 类型：`字符串`
    - 默认值：`"false"`
    - 参考选项：`"true"`, `“false”`, `"ops"`, `"0"`, `"1"`, `"2"`, `"3"`, `"4"`
    - 分类：`Org`，`指令`

规则搜索命令(commandRuleSearch)

- 启用/ruleSearch命令用来搜索翻译后名称中包含指定字符串的规则
    - 类型：`字符串`
    - 默认值：`"ops"`
    - 参考选项：`"true"`, `“false”`, `"ops"`, `"0"`, `"1"`, `"2"`, `"3"`, `"4"`
    - 分类：`Org`，`指令`

增强闪电苦力怕(superChargedCreeper)

- 闪电苦力怕同时炸死多个生物时每个生物都掉落头颅
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`生存`

玩家掉落头(playerDropHead)

- 玩家被闪电苦力怕杀死时掉落头颅
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`生存`

信标范围扩展(beaconRangeExpand)

- 将信标范围扩展向四周指定格数
    - 类型：`整数`
    - 默认值：`0`
    - 分类：`Org`，`生存`

信标世界高度(beaconWorldHeight)

- 将信标竖直方向的范围调整为整个世界高度
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`生存`

生物是否可以捡起物品(mobWhetherOrNotCanPickItem)

- 设置掉落物是否可以被生物捡起
- no：任何生物都不能捡起掉落物
- vanilla：原版行为
- yes：任何生物都能捡起掉落物
- yes_only_hostile：任何敌对都能捡起掉落物，其它生物为原版行为
- no_only_hostile：任何敌对生物都不能捡起掉落物，其它生物为原版行为
    - 类型：`枚举`
    - 默认值：`vanilla`
    - 参考选项：`no`，`vanilla`，`yes`，`yes_only_hostile`，`no_only_hostile`
    - 分类：`Org`，`特性`

## 命令

[/itemshadowing](docs/commands/itemshadowing.md)

- 用来制作物品分身

[/protect](docs/commands/protect.md)

- 用来管理受保护的玩家列表

[/playerTools](docs/commands/playerTools.md)

- 提供一些关于假玩家的辅助工具

[/sendMessage](docs/commands/sendMessage.md)

- 发送一些可以单击复制的文本

[/xpTransfer](docs/commands/xpTransfer.md)

- 用来在玩家之间分享经验

[/spectator](docs/commands/spectator.md)

- 在生存模式和旁观模式间切换

[/finder](docs/commands/finder.md)

- 用来查找玩家周围的指定方块或物品

[/killMe](docs/commands/killMe.md)

- 自杀

[/locations](docs/commands/locations.md)

路径点管理器，mcdr中!!loc的替代命令

[/particleLine](docs/commands/particleLine.md)

- 绘制粒子线

[/playerAction](docs/commands/playerAction.md)

- 假玩家动作命令，控制假玩家的行为

[/creeper](docs/commands/creeper.md)

- 在指定玩家周围生成一只立即爆炸的苦力怕，爆炸不会破坏方块

[/ruleSearch](docs/commands/ruleSearch.md)

- 用来搜索翻译后名称中包含指定字符串的规则